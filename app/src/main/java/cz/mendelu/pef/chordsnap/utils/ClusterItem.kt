package cz.mendelu.pef.chordsnap.utils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import cz.mendelu.pef.chordsnap.models.MusicPlace

class ClusterItem(
    private val musicPlace: MusicPlace
) : ClusterItem {
    override fun getPosition(): LatLng = musicPlace.getLocation()
    override fun getTitle(): String = musicPlace.name
    override fun getSnippet(): String = musicPlace.address
    override fun getZIndex(): Float = 0f

    fun getMusicPlace(): MusicPlace = musicPlace
}