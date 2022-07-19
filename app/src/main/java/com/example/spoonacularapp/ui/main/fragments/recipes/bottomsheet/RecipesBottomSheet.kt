package com.example.spoonacularapp.ui.main.fragments.recipes.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.spoonacularapp.databinding.RecipesBottomSheetBinding
import com.example.spoonacularapp.ui.main.fragments.recipes.RecipesViewModel
import com.example.spoonacularapp.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.spoonacularapp.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.*

class RecipesBottomSheet : BottomSheetDialogFragment() {

    private lateinit var recipesViewModel: RecipesViewModel

    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    lateinit var binding: RecipesBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = RecipesBottomSheetBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner) { value ->
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            updateChip(value.selectedMealTypeId, binding.mealTypeChipGroup)
            updateChip(value.selectedDietTypeId, binding.dietTypeChipGroup)
        }

        binding.mealTypeChipGroup.setOnCheckedStateChangeListener{ group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId[0])
            val selectedMealType = chip.text.toString().lowercase(Locale.getDefault())
            mealTypeChip = selectedMealType
            mealTypeChipId = selectedChipId[0]
        }

        binding.dietTypeChipGroup.setOnCheckedStateChangeListener{ group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId[0])
            val selectedDietType = chip.text.toString().lowercase(Locale.getDefault())
            dietTypeChip = selectedDietType
            dietTypeChipId = selectedChipId[0]
        }

        binding.applyBtn.setOnClickListener{
            recipesViewModel.saveMealAndDietTypeTemp(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId
            )
                val action = RecipesBottomSheetDirections
                    .actionRecipesBottomSheetToRecipesFragment(true)
                findNavController().navigate(action)
        }

        return binding.root
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if(chipId != 0){
            try{
                val targetView = chipGroup.findViewById<Chip>(chipId)
                targetView.isChecked = true
                chipGroup.requestChildFocus(targetView, targetView)
            } catch (e: Exception){
                Log.d("RecipesBottomSheet", e.message.toString())
            }
        }
    }

}