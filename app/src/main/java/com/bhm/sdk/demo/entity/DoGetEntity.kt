package com.bhm.sdk.demo.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by bhm on 2022/9/15.
 */
@Suppress("unused")
class DoGetEntity : Serializable {
    @SerializedName("date")
    var date: String? = null

    @SerializedName("stories")
    var stories: List<StoriesEntity>? = null

    @SerializedName("top_stories")
    var top_stories: List<TopStoriesEntity>? = null

    inner class StoriesEntity : Serializable {
        @SerializedName("image")
        var image: List<String>? = null

        @SerializedName("type")
        var type = 0

        @SerializedName("id")
        var id = 0

        @SerializedName("ga_prefix")
        var ga_prefix: String? = null

        @SerializedName("title")
        var title: String? = null
    }

    inner class TopStoriesEntity : Serializable {
        @SerializedName("image")
        var image: String? = null

        @SerializedName("type")
        var type = 0

        @SerializedName("id")
        var id = 0

        @SerializedName("ga_prefix")
        var ga_prefix: String? = null

        @SerializedName("title")
        var title: String? = null
    }
}