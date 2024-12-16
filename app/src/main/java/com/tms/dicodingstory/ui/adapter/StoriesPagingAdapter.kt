package com.tms.dicodingstory.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tms.dicodingstory.R
import com.tms.dicodingstory.data.local.entity.StoryEntity
import com.tms.dicodingstory.databinding.StoriesItemBinding
import com.tms.dicodingstory.ui.storydetail.StoryDetailActivity

class StoriesPagingAdapter :
    PagingDataAdapter<StoryEntity, StoriesPagingAdapter.StoriesViewHolder>(DIFF_CALLBACK) {

    class StoriesViewHolder(private val binding: StoriesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(binding.storiesImageItem)

            binding.storiesUserNameItem.text =
                itemView.context.getString(R.string.stories_user_name, story.name)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                intent.putExtra(StoryDetailActivity.EXTRA_STORY, story)
                itemView.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder {
        val binding = StoriesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }


    companion object {
          val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}