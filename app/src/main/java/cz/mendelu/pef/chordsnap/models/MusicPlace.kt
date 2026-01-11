package cz.mendelu.pef.chordsnap.models

import androidx.annotation.StringRes
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
    @StringRes val labelRes: Int,
    val iconRes: Int
) {
    MUSIC_SHOP(R.string.place_type_music_shop, R.drawable.shop),
    MUSIC_SCHOOL(R.string.place_type_music_school, R.drawable.school),
    CONSERVATORY(R.string.place_type_conservatory, R.drawable.academy),
    MUSIC_ACADEMY(R.string.place_type_music_academy, R.drawable.academy),
    RECORDING_STUDIO(R.string.place_type_recording_studio, R.drawable.record),
    REHEARSAL_ROOM(R.string.place_type_rehearsal_room, R.drawable.rehearsal)
}