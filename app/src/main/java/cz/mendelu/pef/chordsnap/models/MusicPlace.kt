package cz.mendelu.pef.chordsnap.models

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

@Serializable
data class MusicPlace(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double? = null,
    val type: String,
    val imageUrl: String,
    val website: String? = null
) {
    fun getLocation(): LatLng = LatLng(latitude, longitude)

    fun getPlaceType(): MusicPlaceType = MusicPlaceType.valueOf(type)
}

@Serializable
data class MusicPlacesResponse(
    val places: List<MusicPlace>
)

enum class MusicPlaceType {
    MUSIC_SHOP,
    MUSIC_SCHOOL,
    CONSERVATORY,
    MUSIC_ACADEMY,
    RECORDING_STUDIO,
    REHEARSAL_ROOM
}