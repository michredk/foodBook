package com.example.spoonacularapp.ui.main.fragments.favorites.bottomsheet

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
import com.example.spoonacularapp.R
import com.example.spoonacularapp.data.database.entities.FavoritesGroupsEntity
import com.example.spoonacularapp.databinding.FavoritesBottomSheetBinding
import com.example.spoonacularapp.ui.main.MainViewModel
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

        binding.groupNameEditText.addTextChangedListener(textWatcher)

        binding.colorChipGroup.setOnCheckedStateChangeListener { group, selectedChipId ->
            selectedChip = selectedChipId[0]
        }

        binding.addBtn.setOnClickListener {
            val favGroup = FavoritesGroupsEntity(
                0,
                binding.groupNameEditText.text.toString(),
                selectedChipToColor(selectedChip)
            )
            mainViewModel.insertFavoritesGroup(favGroup)
            findNavController().navigate(R.id.action_favoritesGroupsBottomSheet_to_favoritesGroupsFragment)
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun selectedChipToColor(selectedChip: Int): String {
        if (selectedChip == 0 || selectedChip == binding.redChip.id) return resources.getString(R.color.red)
        if (selectedChip == binding.orangeChip.id) return resources.getString(R.color.orange)
        if (selectedChip == binding.yellowChip.id) return resources.getString(R.color.yellow)
        if (selectedChip == binding.greenChip.id) return resources.getString(R.color.themeGreen)
        if (selectedChip == binding.darkGreenChip.id) return resources.getString(R.color.themeGreenDark)
        if (selectedChip == binding.marineChip.id) return resources.getString(R.color.marine)
        if (selectedChip == binding.blueChip.id) return resources.getString(R.color.blue)
        else return resources.getString(R.color.colorPrimary)
    }

    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            binding.addBtn.isEnabled = (s.toString().trim().length > 1);
        }
    }
}