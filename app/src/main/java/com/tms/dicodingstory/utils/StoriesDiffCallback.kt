package com.tms.dicodingstory.utils

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.tms.dicodingstory.data.remote.response.ListStoryItem


class StoriesDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
    override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem.name == newItem.name
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: ListStoryItem,
        newItem: ListStoryItem
    ): Boolean {
        return oldItem == newItem
    }


}