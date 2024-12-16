package com.tms.dicodingstory

import com.tms.dicodingstory.data.local.entity.StoryEntity

object DataDummy {

    fun generateDummyStoryEntity(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val story = StoryEntity(
                id = i.toString(),
                photoUrl = "photoUrl $i",
                name = "story $i",
                description = "description $i",
                createdAt = "22/10/2023",
            )
            items.add(story)
        }
        return items
    }
}
