package cz.mendelu.pef.chordsnap.utils

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import androidx.core.graphics.createBitmap

class ClusterRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<ClusterItem>
) : DefaultClusterRenderer<ClusterItem>(context, map, clusterManager) {

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterItem>): Boolean {
        return cluster.size > 3
    }

    override fun onBeforeClusterItemRendered(
        item: ClusterItem,
        markerOptions: MarkerOptions
    ) {
        val type = item.getMusicPlace().getPlaceType()

        val customIcon = bitmapDescriptorFromVector(context, type.iconRes)

        if (customIcon != null) {
            markerOptions.icon(customIcon)
        } else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        }

        markerOptions.title(item.title)
        markerOptions.snippet(item.snippet)
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) ?: return null

        val size = 96
        vectorDrawable.setBounds(0, 0, size, size)

        val bitmap = createBitmap(size, size)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}