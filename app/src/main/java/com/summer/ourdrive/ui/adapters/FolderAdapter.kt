package com.summer.ourdrive.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.summer.ourdrive.R
import com.summer.ourdrive.databinding.ItemFolderBinding
import com.summer.ourdrive.ui.models.Folder

class FolderAdapter(private val itemClickListener: ItemClickListener) :
    ListAdapter<Folder, FolderAdapter.FolderHolder>(Callback()) {

    inner class FolderHolder(private val itemFolderBinding: ItemFolderBinding) :
        RecyclerView.ViewHolder(itemFolderBinding.root) {
        fun bind(folder: Folder) {
            itemFolderBinding.model = folder
            itemFolderBinding.root.setOnClickListener {
                itemClickListener.onItemClick(folder)
            }
        }
    }

    internal class Callback : DiffUtil.ItemCallback<Folder>() {
        override fun areItemsTheSame(
            oldItem: Folder,
            newItem: Folder
        ): Boolean {
            return oldItem.folderId == newItem.folderId
        }

        override fun areContentsTheSame(
            oldItem: Folder,
            newItem: Folder
        ): Boolean {
            return oldItem.folderId == newItem.folderId
                    && oldItem.folderName == newItem.folderName
                    && oldItem.createdAt == newItem.createdAt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FolderHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_folder,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: FolderHolder, position: Int) =
        holder.bind(getItem(position))

    interface ItemClickListener {
        fun onItemClick(model: Folder)
    }
}