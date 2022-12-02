package com.example.foodfoodapp.ui.details.fragments.instructions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.foodfoodapp.databinding.FragmentInstructionsBinding
import com.example.foodfoodapp.model.Result
import com.example.foodfoodapp.util.Constants


class InstructionsFragment : Fragment() {

    lateinit var binding: FragmentInstructionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInstructionsBinding.inflate(layoutInflater, container, false)

        val args = arguments
        val myBundle: Result? = args?.getParcelable(Constants.RECIPE_RESULT_KEY)

        binding.instructionsWebView.webViewClient = object : WebViewClient() {}
        val websiteUrl: String = myBundle!!.sourceUrl
        binding.instructionsWebView.loadUrl(websiteUrl)

        return binding.root
    }

}