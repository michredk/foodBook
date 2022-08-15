package com.example.spoonacularapp.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.spoonacularapp.R
import com.example.spoonacularapp.data.database.entities.FavoritesGroupsEntity
import com.example.spoonacularapp.databinding.GroupRowLayoutBinding
import com.example.spoonacularapp.ui.main.fragments.favorites.FavoritesGroupsFragmentDirections
import com.example.spoonacularapp.util.RecipesDiffUtil

class FavoritesGroupsAdapter : RecyclerView.Adapter<FavoritesGroupsAdapter.MyViewHolder>() {

    private var groups = emptyList<FavoritesGroupsEntity>()

    class MyViewHolder(private val binding: GroupRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoritesGroupsEntity: FavoritesGroupsEntity) {
            binding.group = favoritesGroupsEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GroupRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        clickAnimationForCard(holder)

        val currentGroup = groups[position]
        holder.bind(currentGroup)

        // single click
        holder.itemView.findViewById<ConstraintLayout>(R.id.groupRowLayout).setOnClickListener {
            val action =
                FavoritesGroupsFragmentDirections.actionFavoritesGroupsFragmentToFavouriteRecipesFragment(
                    currentGroup.id, currentGroup.color
                )
            holder.itemView.findNavController().navigate(action)
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

    override fun getItemCount(): Int {
        return groups.size
    }

    fun setData(newGroupsList: List<FavoritesGroupsEntity>) {
        val recipesDiffUtil = RecipesDiffUtil(groups, newGroupsList)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        groups = newGroupsList
        diffUtilResult.dispatchUpdatesTo(this)
    }
}