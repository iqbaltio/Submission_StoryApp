package com.iqbaltio.storyapp

import com.iqbaltio.storyapp.data.*

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStory> {
        val item = arrayListOf<ListStory>()
        for (i in 0 until 10) {
            val story = ListStory(
                "Arif Faizin",
                "This is Desc",
                "https://images.pexels.com/photos/3861972/pexels-photo-3861972.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                "2023-05-06",
                112.691111,
                -7.830912,
                "user-yj5pc_LARC_AgK61",
            )
            item.add(story)
        }
        return item
    }
}