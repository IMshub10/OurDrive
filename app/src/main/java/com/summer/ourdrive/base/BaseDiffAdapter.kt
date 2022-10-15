package com.summer.ourdrive.base


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseDiffAdapter<T, L>(diffItemCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, BaseDiffAdapter.BaseViewHolder<T, L>>(diffItemCallback) {

    private var itemActionListener: L? = null


    fun setItemActionListener(itemActionListener: L) {
        this.itemActionListener = itemActionListener
    }


    fun getActionListener(): L? = itemActionListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, L> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
        return BaseViewHolder(binding, itemActionListener)
    }


    override fun onBindViewHolder(holder: BaseViewHolder<T, L>, position: Int) =
        holder.bind(getItem(position))


    class BaseViewHolder<T, L>(
        private val binding: ViewDataBinding,
        private val itemActionListener: L?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T) {
          /*binding.setVariable(BR.model, item)
            itemActionListener?.let {
               binding.setVariable(BR.clicker, it)
            }
            binding.executePendingBindings()*/
        }
    }
}