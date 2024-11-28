package com.tms.dicodingstory

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tms.dicodingstory.data.remote.response.ListStoryItem
import com.tms.dicodingstory.databinding.StoriesItemBinding
import com.tms.dicodingstory.ui.storydetail.StoryDetailActivity
import com.tms.dicodingstory.utils.StoriesDiffCallback

class StoriesAdapter :
    ListAdapter<ListStoryItem, StoriesAdapter.StoriesViewHolder>(StoriesDiffCallback()) {

    class StoriesViewHolder(private val binding: StoriesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.storiesImageItem)

            binding.storiesUserNameItem.text =
                itemView.context.getString(R.string.stories_user_name, story.name)

            itemView.setOnClickListener {
                // TODO: ADD EXTRA STORY
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
        holder.bind(item)
    }
}