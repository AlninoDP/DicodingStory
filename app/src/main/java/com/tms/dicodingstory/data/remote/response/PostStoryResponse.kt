package com.tms.dicodingstory.data.remote.response

import com.google.gson.annotations.SerializedName

data class PostStoryResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
