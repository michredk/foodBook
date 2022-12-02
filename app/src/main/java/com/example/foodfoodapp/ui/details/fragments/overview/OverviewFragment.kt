package com.example.foodfoodapp.ui.details.fragments.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import coil.load
import com.example.foodfoodapp.R
import com.example.foodfoodapp.bindingadapters.RecipesRowBinding
import com.example.foodfoodapp.databinding.FragmentOverviewBinding
import com.example.foodfoodapp.model.Result
import com.example.foodfoodapp.util.Constants.Companion.RECIPE_RESULT_KEY


class OverviewFragment : Fragment() {

    private lateinit var binding: FragmentOverviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOverviewBinding.inflate(layoutInflater, container, false)

        val args = arguments
        val myBundle: Result = args!!.getParcelable<Result>(RECIPE_RESULT_KEY) as Result

        binding.mainImageView.load(myBundle.image)
        binding.titleTextView.text = myBundle.title
        binding.likesTextView.text = myBundle.aggregateLikes.toString()
        binding.timeTextView.text = myBundle.readyInMinutes.toString()
        RecipesRowBinding.parseHtml(binding.descriptionTextView, myBundle.summary)

        updateColors(myBundle.vegetarian, binding.vegetarianTextView, binding.vegetarianImageView, R.color.green)
        updateColors(myBundle.vegan, binding.veganTextView, binding.veganImageView, R.color.green)
        updateColors(myBundle.glutenFree, binding.glutenFreeTextView, binding.glutenFreeImageView, R.color.green)
        updateColors(myBundle.dairyFree, binding.dairyFreeTextView, binding.dairyFreeImageView, R.color.green)
        updateColors(myBundle.veryHealthy, binding.healthyTextView, binding.healthyImageView, R.color.red)
        updateColors(myBundle.cheap, binding.cheapTextView, binding.cheapImageView, R.color.yellow)

        return binding.root
    }

    private fun updateColors(stateIsOn: Boolean, textView: TextView, imageView: ImageView, color: Int){
        if(stateIsOn){
            imageView.setColorFilter(ContextCompat.getColor(requireContext(), color))
            textView.setTextColor(ContextCompat.getColor(requireContext(), color))
        }
    }

}