package cz.mendelu.examtemplate2025.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Park(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "country")
    val country: String,
    @Json(name = "region")
    val region: String,
    @Json(name = "center")
    val center: Any,
    @Json(name = "show_tips")
    val showTips: Boolean
)