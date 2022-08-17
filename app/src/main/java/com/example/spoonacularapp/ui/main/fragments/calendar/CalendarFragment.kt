package com.example.spoonacularapp.ui.main.fragments.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoonacularapp.R
import com.example.spoonacularapp.adapters.CalendarRecipesAdapter
import com.example.spoonacularapp.databinding.FragmentCalendarBinding
import com.example.spoonacularapp.ui.main.MainViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    lateinit var binding: FragmentCalendarBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter: CalendarRecipesAdapter by lazy {
        CalendarRecipesAdapter(requireActivity(), mainViewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.mAdapter = mAdapter

        setupRecyclerView(binding.calendarRecipesRecyclerView)

        val now = Calendar.getInstance()
        binding.date =
            now.get(Calendar.YEAR).toString() +
            now.get(Calendar.MONTH).toString() +
            now.get(Calendar.DAY_OF_MONTH).toString()
        binding.calendarView.setOnDateChangeListener { _, y, m, d ->
            binding.date = y.toString() + m.toString() + d.toString()
        }

        binding.shoppingListFab.setOnClickListener {
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select dates")
                    .setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
                    .setTheme(R.style.MaterialDatePickerCustom)
                    .build()
            dateRangePicker.addOnPositiveButtonClickListener {  }
            dateRangePicker.show(activity?.supportFragmentManager!!, dateRangePicker.toString())
        }

        return binding.root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}