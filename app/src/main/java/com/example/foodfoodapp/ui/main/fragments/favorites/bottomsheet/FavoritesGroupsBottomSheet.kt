package com.example.foodfoodapp.ui.main.fragments.favorites.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.foodfoodapp.R
import com.example.foodfoodapp.data.database.entities.FavoritesGroupsEntity
import com.example.foodfoodapp.databinding.FavoritesBottomSheetBinding
import com.example.foodfoodapp.ui.main.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesGroupsBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: FavoritesBottomSheetBinding
    private val mainViewModel: MainViewModel by viewModels()

    private var selectedChip = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialogWithKeyboard)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FavoritesBottomSheetBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this

        binding.groupNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                binding.addBtn.isEnabled = (s.toString().trim().length > 1)
            }
        })

        binding.colorChipGroup.setOnCheckedStateChangeListener { _, selectedChipId ->
            selectedChip = selectedChipId[0]
        }

        binding.addBtn.setOnClickListener(addBtnClick())

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun addBtnClick(): (View) -> Unit = {
        val favGroup = FavoritesGroupsEntity(
            0,
            binding.groupNameEditText.text.toString(),
            selectedChipToColor(selectedChip)
        )
        mainViewModel.insertFavoritesGroup(favGroup)
        findNavController().navigate(R.id.action_favoritesGroupsBottomSheet_to_favoritesGroupsFragment)
    }

    private fun selectedChipToColor(selectedChip: Int): Int {
        if (selectedChip == 0 || selectedChip == binding.redChip.id) return R.color.red
        if (selectedChip == binding.orangeChip.id) return R.color.orange
        if (selectedChip == binding.yellowChip.id) return R.color.yellow
        if (selectedChip == binding.greenChip.id) return R.color.themeGreen
        if (selectedChip == binding.darkGreenChip.id) return R.color.themeGreenDark
        if (selectedChip == binding.marineChip.id) return R.color.marine
        return if (selectedChip == binding.blueChip.id) R.color.blue
        else R.color.colorPrimary
    }
}