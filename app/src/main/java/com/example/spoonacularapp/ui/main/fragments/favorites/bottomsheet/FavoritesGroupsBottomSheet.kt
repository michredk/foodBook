package com.example.spoonacularapp.ui.main.fragments.favorites.bottomsheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.spoonacularapp.R
import com.example.spoonacularapp.databinding.FavoritesBottomSheetBinding
import com.example.spoonacularapp.ui.main.fragments.favorites.FavoritesGroupsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FavoritesGroupsBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: FavoritesBottomSheetBinding
    private lateinit var favoritesGroupsViewModel: FavoritesGroupsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoritesGroupsViewModel =
            ViewModelProvider(requireActivity())[FavoritesGroupsViewModel::class.java]
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
        binding.addBtn.setOnClickListener {
            favoritesGroupsViewModel.addFavoriteGroup(binding.groupNameEditText.toString())
            findNavController().navigate(R.id.action_favoritesGroupsBottomSheet_to_favouriteRecipesFragment)
        }

        return binding.root
    }

    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            binding.addBtn.isEnabled = (s.toString().trim().length > 1);
        }
    }

}