package io.afdon.data.source.api.response


import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("documentation_url")
    val documentationUrl: String?
)