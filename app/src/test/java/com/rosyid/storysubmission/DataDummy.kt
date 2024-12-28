package com.rosyid.storysubmission

import com.rosyid.storysubmission.data.remote.pref.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                id = "story-$i",
                name = "Story $i",
                description = "Description $i",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-$i.jpg",
                createdAt = "2024-01-08T06:34:18.598Z",
                lat = -6.8957643,
                lon = 107.6338462
            )
            items.add(story)
        }
        return items
    }
}
