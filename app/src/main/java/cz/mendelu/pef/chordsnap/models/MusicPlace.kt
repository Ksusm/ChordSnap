package cz.mendelu.pef.chordsnap.models

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable
import cz.mendelu.pef.chordsnap.R

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

enum class MusicPlaceType(
    val label: String,
    val iconRes: Int
) {
    MUSIC_SHOP("Music Shop", R.drawable.shop),
    MUSIC_SCHOOL("Music School", R.drawable.school),
    CONSERVATORY("Conservatory", R.drawable.academy),
    MUSIC_ACADEMY("Music Academy", R.drawable.academy),
    RECORDING_STUDIO("Recording Studio", R.drawable.record),
    REHEARSAL_ROOM("Rehearsal Room", R.drawable.rehearsal)
}