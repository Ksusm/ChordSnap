package cz.mendelu.pef.chordsnap.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.compose.*
import cz.mendelu.pef.chordsnap.utils.ClusterItem
import cz.mendelu.pef.chordsnap.utils.ClusterRenderer
import cz.mendelu.pef.chordsnap.models.MusicPlace
import cz.mendelu.pef.chordsnap.models.MusicPlaceType
import cz.mendelu.pef.chordsnap.ui.components.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    currentRoute: String?,
    viewModel: MapScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showLegend by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music Places") },
                actions = {
                    IconButton(onClick = { showLegend = !showLegend }) {
                        Icon(Icons.Default.Info, "Show legend")
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MapContent(
                uiState = uiState,
                onPlaceClick = { place ->
                    viewModel.onPlaceSelected(place)
                }
            )

            if (showLegend) {
                MapLegend(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    onDismiss = { showLegend = false }
                )
            }
        }
    }

    if (uiState.selectedPlace != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissBottomSheet() },
            sheetState = sheetState
        ) {
            PlaceDetailBottomSheet(place = uiState.selectedPlace!!)
        }
    }
}

@Composable
fun MapLegend(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Legend", style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Divider()

            MusicPlaceType.entries.forEach { type ->
                LegendItem(
                    iconRes = type.iconRes,
                    text = type.label
                )
            }
        }
    }
}

@Composable
fun LegendItem(iconRes: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun MapContent(
    uiState: MapUiState,
    onPlaceClick: (MusicPlace) -> Unit
) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.cameraPosition, 13f)
    }

    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var clusterManager by remember { mutableStateOf<ClusterManager<ClusterItem>?>(null) }

    val clusterItems = remember(uiState.musicPlaces) {
        uiState.musicPlaces.map { ClusterItem(it) }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = false
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = false,
            mapToolbarEnabled = false,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true,
            tiltGesturesEnabled = false,
            zoomGesturesEnabled = true
        )
    ) {
        MapEffect(key1 = uiState.musicPlaces) { map ->
            if (googleMap == null) {
                googleMap = map
            }

            if (clusterManager == null) {
                clusterManager = ClusterManager<ClusterItem>(context, map)
                clusterManager?.renderer = ClusterRenderer(
                    context,
                    map,
                    clusterManager!!
                )

                clusterManager?.setOnClusterItemClickListener { item ->
                    onPlaceClick(item.getMusicPlace())
                    true
                }

                map.setOnCameraIdleListener {
                    clusterManager?.onCameraIdle()
                }
            }

            clusterManager?.clearItems()
            clusterManager?.addItems(clusterItems)
            clusterManager?.cluster()
        }
    }
}

@Composable
fun PlaceDetailBottomSheet(place: MusicPlace) {
    val placeType = place.getPlaceType()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = place.imageUrl,
            contentDescription = place.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.headlineSmall
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = placeType.iconRes),
                    contentDescription = "Place type",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = placeType.label,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Address",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (place.rating != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "${place.rating} / 5.0",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            if (place.website != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Website",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = place.website,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}