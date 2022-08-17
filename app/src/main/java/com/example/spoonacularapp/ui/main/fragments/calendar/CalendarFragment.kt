package com.example.spoonacularapp.ui.main.fragments.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoonacularapp.adapters.CalendarRecipesAdapter
import com.example.spoonacularapp.databinding.FragmentCalendarBinding
import com.example.spoonacularapp.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

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

        binding.calendarView.setOnDateChangeListener { calendarView, y, m, d ->
            mainViewModel.readCalendarRecipes.observe(viewLifecycleOwner) { groupEntity ->
                mAdapter.setData(groupEntity.filter {
                    it.date == y.toString() + m.toString() + d.toString()
                })
            }
        }

        return binding.root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}