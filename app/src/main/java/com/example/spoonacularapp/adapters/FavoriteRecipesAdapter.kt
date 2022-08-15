package com.example.spoonacularapp.adapters

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.spoonacularapp.R
import com.example.spoonacularapp.data.database.entities.FavoritesEntity
import com.example.spoonacularapp.databinding.FavoriteRecipeRowLayoutBinding
import com.example.spoonacularapp.ui.main.MainViewModel
import com.example.spoonacularapp.ui.main.fragments.favorites.FavoriteRecipesFragmentDirections
import com.example.spoonacularapp.util.RecipesDiffUtil
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar

class FavoriteRecipesAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel,
    private val groupColor: String
) : RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>(),
    ActionMode.Callback {

    private var favoriteRecipes = emptyList<FavoritesEntity>()
    private var multiSelection = false
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var previouslyRemovedRecipes = arrayListOf<FavoritesEntity>()
    private var myViewHolders = arrayListOf<MyViewHolder>()

    private lateinit var mActionMode: ActionMode
    private lateinit var rootView: View

    class MyViewHolder(private val binding: FavoriteRecipeRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoritesEntity: FavoritesEntity) {
            binding.favoritesEntity = favoritesEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRecipeRowLayoutBinding.inflate(layoutInflater, parent, false)
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

        val currentRecipe = favoriteRecipes[position]
        holder.bind(currentRecipe)
        saveItemStateOnScroll(currentRecipe, holder)

        // single click listener
        holder.itemView.findViewById<ConstraintLayout>(R.id.favoriteRecipesRowLayout)
            .setOnClickListener {
                if (multiSelection) {
                    applySelection(holder, currentRecipe)
                } else {
                    val action =
                        FavoriteRecipesFragmentDirections.actionFavouriteRecipesFragmentToDetailsActivity(
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

    private fun saveItemStateOnScroll(currentRecipe: FavoritesEntity, holder: MyViewHolder) {
        if (selectedRecipes.contains(currentRecipe)) {
            changeRecipeStyle(
                holder,
                R.color.selectedStrokeColor
            )
        } else {
            changeRecipeStyle(holder, R.color.strokeColor)
        }
    }

    private fun applySelection(holder: MyViewHolder, currentRecipe: FavoritesEntity) {
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
        holder.itemView.findViewById<MaterialCardView>(R.id.favorite_row_cardView).strokeColor =
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
        return favoriteRecipes.size
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorites_contextual_menu, menu)
        mActionMode = actionMode!!
        requireActivity.window.statusBarColor = Color.parseColor(groupColor)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if (menu?.itemId == R.id.deleteFavoriteRecipe_menu) {
            selectedRecipes.forEach {
                mainViewModel.deleteFavoriteRecipe(it)
            }

            selectedRecipes.forEach {
                previouslyRemovedRecipes.add(it)
            }

            showSnackBar("${selectedRecipes.size} recipe/s removed")
            multiSelection = false
            selectedRecipes.clear()
            actionMode?.finish()
        }
        return true
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
        ).setAction("Undo") {
            Log.d("addingRemoved", "status before removing: ${previouslyRemovedRecipes.size}")
            previouslyRemovedRecipes.forEach {
                mainViewModel.insertFavoriteRecipe(it)
            }
            previouslyRemovedRecipes.clear()
        }.show()
    }

    fun clearContextualActionMode() {
        if (this::mActionMode.isInitialized) {
            mActionMode.finish()
        }
    }

    fun setData(newFavoriteRecipes: List<FavoritesEntity>) {
        val favoriteRecipesDiffUtil = RecipesDiffUtil(favoriteRecipes, newFavoriteRecipes)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        favoriteRecipes = newFavoriteRecipes
        diffUtilResult.dispatchUpdatesTo(this)
    }
}