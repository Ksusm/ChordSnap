package cz.mendelu.examtemplate2025.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParksResponse(
    @Json(name = "parks")
    val parks: List<Park>
)