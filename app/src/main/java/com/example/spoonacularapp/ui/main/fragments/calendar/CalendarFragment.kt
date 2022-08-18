package com.example.spoonacularapp.ui.main.fragments.calendar

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.example.spoonacularapp.data.database.entities.CalendarEntity
import com.example.spoonacularapp.databinding.FragmentCalendarBinding
import com.example.spoonacularapp.model.ExtendedIngredient
import com.example.spoonacularapp.ui.main.MainViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
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
        binding.date = String.format("%d%02d%02d",
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH) + 1,
            now.get(Calendar.DAY_OF_MONTH)
        ).toInt()
        binding.calendarView.setOnDateChangeListener { _, y, m, d ->
            binding.date = String.format("%d%02d%02d", y, m + 1, d).toInt()
        }

        binding.shoppingListFab.setOnClickListener {
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText(getString(R.string.get_shopping_list))
                    .setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))
                    .setTheme(R.style.MaterialDatePickerCustom)
                    .build()
            dateRangePicker.addOnPositiveButtonClickListener {
                createShoppingList(it.first!!, it.second!!)
            }
            dateRangePicker.show(activity?.supportFragmentManager!!, dateRangePicker.toString())
        }

        return binding.root
    }

    private fun createShoppingList(first: Long, second: Long) {
        var calendar: List<CalendarEntity>? = mainViewModel.readCalendarRecipes.value
        val date1: Int = SimpleDateFormat("yyyyMMdd").format(Date(first)).toInt()
        val date2: Int = SimpleDateFormat("yyyyMMdd").format(Date(second)).toInt()
        calendar = calendar?.filter {
            it.date in date1..date2
        }
        if(calendar.isNullOrEmpty()){
            showSnackBar("No saved recipes on selected dates!")
            return
        }

        val ingredients: MutableList<ExtendedIngredient> = mutableListOf()
        val units: MutableMap<String, String> = mutableMapOf()
        calendar.forEach {
            it.result.extendedIngredients.forEach { ing ->
                ingredients.add(ing)
                units[ing.name] = ing.unit
            }
        }
        val shoppingList = ingredients
            .groupBy({ it.name }, { it.amount })
            .mapValues { it.value.sum() }

        var list: String = ""
        shoppingList.forEach { (key, value) ->
            if(value % 1 == 0.0) {
                list += String.format("%s - %d ${units[key]}\n", key, value.toInt())
            } else {
                list += String.format("%s - %.2f ${units[key]}\n", key, value)
            }
        }
        val clipboardManager = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Shopping list",list ))
        showSnackBar("Shopping list saved to your clipboard!")
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.calendarLayout,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction(R.string.okay) {}
            .show()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}