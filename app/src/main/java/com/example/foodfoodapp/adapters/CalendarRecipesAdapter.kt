package com.example.foodfoodapp.adapters

import android.os.Build
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfoodapp.R
import com.example.foodfoodapp.data.database.entities.CalendarEntity
import com.example.foodfoodapp.databinding.CalendarRecipeRowLayoutBinding
import com.example.foodfoodapp.ui.main.MainViewModel
import com.example.foodfoodapp.ui.main.fragments.calendar.CalendarFragmentDirections
import com.example.foodfoodapp.util.RecipesDiffUtil
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar

class CalendarRecipesAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<CalendarRecipesAdapter.MyViewHolder>(),
    ActionMode.Callback {

    private var calendarRecipes = emptyList<CalendarEntity>()
    private var multiSelection = false
    private var selectedRecipes = arrayListOf<CalendarEntity>()
    private var previouslyRemovedRecipes = arrayListOf<CalendarEntity>()
    private var myViewHolders = arrayListOf<MyViewHolder>()

    private lateinit var mActionMode: ActionMode
    private lateinit var rootView: View

    class MyViewHolder(private val binding: CalendarRecipeRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(calendarEntity: CalendarEntity) {
            binding.calendarEntity = calendarEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CalendarRecipeRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolders.add(holder)
        rootView = holder.itemView.rootView

        clickAnimationForCard(holder)

        val currentRecipe = calendarRecipes[position]
        holder.bind(currentRecipe)
        saveItemStateOnScroll(currentRecipe, holder)

        // single click listener
        holder.itemView.findViewById<ConstraintLayout>(R.id.favoriteRecipesRowLayout)
            .setOnClickListener {
                if (multiSelection) {
                    applySelection(holder, currentRecipe)
                } else {
                    val action =
                        CalendarFragmentDirections.actionCalendarFragmentToDetailsActivity(
                            currentRecipe.result
                        )
                    holder.itemView.findNavController().navigate(action)
                }
            }

        // long click listener
        holder.itemView.findViewById<ConstraintLayout>(R.id.favoriteRecipesRowLayout)
            .setOnLongClickListener {
                if (!multiSelection) {
                    multiSelection = true
                    requireActivity.startActionMode(this)
                    applySelection(holder, currentRecipe)
                    true
                } else {
                    applySelection(holder, currentRecipe)
                    true
                }
            }
    }

    private fun clickAnimationForCard(holder: MyViewHolder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val attrs = intArrayOf(android.R.attr.selectableItemBackgroundBorderless)
            val typedArray = holder.itemView.context.obtainStyledAttributes(attrs)
            val selectableItemBackground = typedArray.getResourceId(0, 0)
            typedArray.recycle()
            holder.itemView.isClickable = true
            holder.itemView.isFocusable = true
            holder.itemView.foreground =
                holder.itemView.context.getDrawable(selectableItemBackground)
        }
    }

    private fun saveItemStateOnScroll(currentRecipe: CalendarEntity, holder: MyViewHolder) {
        if (selectedRecipes.contains(currentRecipe)) {
            changeRecipeStyle(
                holder,
                R.color.selectedStrokeColor
            )
        } else {
            changeRecipeStyle(holder, R.color.strokeColor)
        }
    }

    private fun applySelection(holder: MyViewHolder, currentRecipe: CalendarEntity) {
        if (selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder, R.color.strokeColor)
            applyActionModeTitle()
        } else {
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(
                holder,
                R.color.selectedStrokeColor
            )
            applyActionModeTitle()
        }
    }

    private fun changeRecipeStyle(holder: MyViewHolder, strokeColor: Int) {
        holder.itemView.findViewById<MaterialCardView>(R.id.calendar_row_cardView).strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    private fun applyActionModeTitle() {
        when (selectedRecipes.size) {
            0 -> {
                mActionMode.finish()
                multiSelection = false
            }
            1 -> {
                mActionMode.title = "${selectedRecipes.size} recipe selected"
            }
            else -> {
                mActionMode.title = "${selectedRecipes.size} recipes selected"
            }
        }
    }

    override fun getItemCount(): Int {
        return calendarRecipes.size
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        previouslyRemovedRecipes.clear()
        actionMode?.menuInflater?.inflate(R.menu.calendar_contextual_menu, menu)
        mActionMode = actionMode!!
        applyStatusBarColor(R.color.colorSecondary)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if (menu?.itemId == R.id.deleteRecipeFromCalendar_menu) {
            removeSelectedFromCalendar(actionMode)
        }
        return true
    }

    private fun removeSelectedFromCalendar(actionMode: ActionMode?) {
        selectedRecipes.forEach {
            mainViewModel.deleteRecipeFromCalendar(it)
        }
        previouslyRemovedRecipes.clear()
        selectedRecipes.forEach {
            previouslyRemovedRecipes.add(it)
        }

        showSnackBar("${selectedRecipes.size} recipe/s removed")
        multiSelection = false
        selectedRecipes.clear()
        actionMode?.finish()
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        myViewHolders.forEach { holder ->
            changeRecipeStyle(holder, R.color.strokeColor)
        }
        multiSelection = false
        selectedRecipes.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun applyStatusBarColor(color: Int) {
        requireActivity.window.statusBarColor = ContextCompat.getColor(requireActivity, color)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.undo) {
            previouslyRemovedRecipes.forEach {
                mainViewModel.insertRecipeToCalendar(it)
            }
            previouslyRemovedRecipes.clear()
        }.show()
    }

    fun clearContextualActionMode() {
        if (this::mActionMode.isInitialized) {
            mActionMode.finish()
        }
    }

    fun setData(newCalendarRecipes: List<CalendarEntity>) {
        val calendarRecipesDiffUtil = RecipesDiffUtil(calendarRecipes, newCalendarRecipes)
        val diffUtilResult = DiffUtil.calculateDiff(calendarRecipesDiffUtil)
        calendarRecipes = newCalendarRecipes
        diffUtilResult.dispatchUpdatesTo(this)
    }
}