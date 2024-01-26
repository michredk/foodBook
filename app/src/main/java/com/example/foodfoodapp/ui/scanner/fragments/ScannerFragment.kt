package com.example.foodfoodapp.ui.scanner.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.foodfoodapp.R
import com.example.foodfoodapp.databinding.FragmentScannerBinding
import com.example.foodfoodapp.ui.scanner.ScannerViewModel
import com.example.foodfoodapp.util.ImageClassifierHelper
import com.example.foodfoodapp.util.safeNavigate
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerFragment : Fragment(), ImageClassifierHelper.ClassifierListener {

    companion object {
        private const val TAG = "Image Classifier"
    }

    private val viewModel: ScannerViewModel by viewModels()

    private var _fragmentScannerBinding: FragmentScannerBinding? = null
    private val fragmentScannerBinding
        get() = _fragmentScannerBinding!!

    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var bitmapBuffer: Bitmap
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    override fun onResume() {
        super.onResume()

        if (!PermissionsFragment.hasPermissions(requireContext())) {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        _fragmentScannerBinding = null
        super.onDestroyView()

        // Shut down our background executor
        cameraExecutor.shutdown()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentScannerBinding = FragmentScannerBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {uiState ->
                    _fragmentScannerBinding?.acceptResultButton?.text = uiState.label
                }
        }

        return fragmentScannerBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageClassifierHelper =
            ImageClassifierHelper(context = requireContext(), imageClassifierListener = this)
        cameraExecutor = Executors.newSingleThreadExecutor()
        fragmentScannerBinding.viewFinder.post {
            // Set up the camera and its use cases
            setUpCamera()
        }

        // adding Listeners to Views
        _fragmentScannerBinding?.acceptResultButton?.setOnClickListener {
            val btn = it as Button
            handleAddChip(btn.text)
        }
        _fragmentScannerBinding?.resultsToSelectChipGroup?.setOnCheckedStateChangeListener { _, checkedIds ->
            val isAtLeastOneChipSelected = checkedIds.isNotEmpty()
            _fragmentScannerBinding?.searchBtn?.isEnabled = isAtLeastOneChipSelected
        }
        _fragmentScannerBinding?.searchBtn?.setOnClickListener { handleSearchButtonClick() }

    }

    // Initialize CameraX, and prepare to bind the camera use cases
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = fragmentScannerBinding.viewFinder.display.rotation
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - makes assumption that we're only using the back camera
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview =
            Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentScannerBinding.viewFinder.display.rotation)
                .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentScannerBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        if (!::bitmapBuffer.isInitialized) {
                            // The image rotation and RGB image buffer are initialized only once
                            // the analyzer has started running
                            bitmapBuffer = Bitmap.createBitmap(
                                image.width,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )
                        }

                        classifyImage(image)
                    }
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(fragmentScannerBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun getScreenOrientation() : Int {
        val outMetrics = DisplayMetrics()

        val display: Display?
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display = requireActivity().display
            display?.getRealMetrics(outMetrics)
        } else {
            display = requireActivity().windowManager.defaultDisplay
            display.getMetrics(outMetrics)
        }

        return display?.rotation ?: 0
    }

    private fun classifyImage(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        // Pass Bitmap and rotation to the image classifier helper for processing and classification
        imageClassifierHelper.classify(bitmapBuffer, getScreenOrientation())
    }

    override fun onError(error: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            viewModel.updateResults(null)
        }
    }

    override fun onResults(
        results: List<Classifications>?,
        inferenceTime: Long
    ) {
        activity?.runOnUiThread {
            viewModel.updateResults(results)
            if(_fragmentScannerBinding?.autoAddCheckBox?.isChecked == true) {
                handleAddChip(_fragmentScannerBinding?.acceptResultButton?.text)
            }
        }
    }

    private fun handleAddChip(result: CharSequence?) {
        if(result.isNullOrBlank()) return
        val chipGroup = _fragmentScannerBinding?.resultsToSelectChipGroup
        chipGroup?.let {
            // check if current result chip is already added to chipGroup
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as? Chip
                chip?.let {
                    if (it.text == result) {
                        return
                    }
                }
            }
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val chip = inflater.inflate(R.layout.result_chip_layout, chipGroup, false) as Chip
            chip.text = result
            chipGroup.addView(chip)
        }
    }

    private fun handleSearchButtonClick() {
        val chipGroup = _fragmentScannerBinding?.resultsToSelectChipGroup
        chipGroup?.let {
            val selectedResults = mutableListOf<String>()
            // check if current result chip is already added to chipGroup
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as? Chip
                chip?.let {
                    selectedResults.add(it.text.toString())
                }
            }
            findNavController().safeNavigate(
                ScannerFragmentDirections.actionScannerFragmentToMainActivity(
                    selectedResults.toTypedArray()
                ))
        }
    }
}
