package com.summer.ourdrive.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.summer.ourdrive.R
import com.summer.ourdrive.databinding.ItemImageBinding
import com.summer.ourdrive.ui.models.Image

class ImageAdapter(private val itemClickListener: ItemClickListener) :
    ListAdapter<Image, ImageAdapter.ImageHolder>(Callback()) {

    inner class ImageHolder(private val itemFolderBinding: ItemImageBinding) :
        RecyclerView.ViewHolder(itemFolderBinding.root) {
        fun bind(image: Image) {
            itemFolderBinding.run {
                model = image
                root.setOnClickListener {
                    itemClickListener.onItemClick(image)
                }
                ivItemImageCloudUpload.setOnClickListener {
                    itemClickListener.onUploadClick(image)
                }
            }
        }
    }

    internal class Callback : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(
            oldItem: Image,
            newItem: Image
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Image,
            newItem: Image
        ): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.imageUrl == newItem.imageUrl
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ImageHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_image,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ImageHolder, position: Int) =
        holder.bind(getItem(position))

    interface ItemClickListener {
        fun onItemClick(model: Image)
        fun onUploadClick(model: Image)
    }
}