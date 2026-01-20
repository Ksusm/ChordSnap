package cz.mendelu.pef.chordsnap.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.compose.*
import cz.mendelu.pef.chordsnap.R
import cz.mendelu.pef.chordsnap.utils.ClusterItem
import cz.mendelu.pef.chordsnap.utils.ClusterRenderer
import cz.mendelu.pef.chordsnap.models.MusicPlace
import cz.mendelu.pef.chordsnap.models.MusicPlaceType
import cz.mendelu.pef.chordsnap.ui.components.BottomBar
import cz.mendelu.pef.chordsnap.ui.theme.*
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.cardPaddingInternal
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.elevationMedium
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.iconSizeMedium
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.paddingLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.paddingXLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingMedium
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingSmall
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingXSmall

const val TestTagMapTitle = "TestTagMapTitle"
const val TestTagMapLegendButton = "TestTagMapLegendButton"
const val TestTagMapLegendCard = "TestTagMapLegendCard"
const val TestTagMapLegendClose = "TestTagMapLegendClose"
const val TestTagMapContent = "TestTagMapContent"

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
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.map_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.testTag(TestTagMapTitle)
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showLegend = !showLegend },
                        modifier = Modifier.testTag(TestTagMapLegendButton)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = stringResource(R.string.show_legend),
                            tint = AccentCyan
                        )
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
                        .padding(paddingLarge),
                    onDismiss = { showLegend = false }
                )
            }
        }
    }

    if (uiState.selectedPlace != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissBottomSheet() },
            sheetState = sheetState,
            shape = CustomShapes.chordCard,
            containerColor = MaterialTheme.colorScheme.surface
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
        modifier = modifier.testTag(TestTagMapLegendCard),
        shape = CustomShapes.chordCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevationMedium
        )
    ) {
        Column(
            modifier = Modifier.padding(cardPaddingInternal),
            verticalArrangement = Arrangement.spacedBy(spacingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.map_legend),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(iconSizeMedium)
                        .testTag(TestTagMapLegendClose)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_close),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            MusicPlaceType.entries.forEach { type ->
                LegendItem(
                    iconRes = type.iconRes,
                    text = stringResource(type.labelRes)
                )
            }
        }
    }
}

@Composable
fun LegendItem(iconRes: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacingMedium),
        modifier = Modifier.padding(vertical = spacingXSmall)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(iconSizeMedium),
            tint = Color.Unspecified
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
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
        modifier = Modifier
            .fillMaxSize()
            .testTag(TestTagMapContent),
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
            .padding(bottom = paddingXLarge),
        verticalArrangement = Arrangement.spacedBy(spacingLarge)
    ) {
        // Place image
        AsyncImage(
            model = place.imageUrl,
            contentDescription = place.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(horizontal = paddingXLarge),
            verticalArrangement = Arrangement.spacedBy(spacingLarge)
        ) {
            // Place name
            Text(
                text = place.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Place type
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacingSmall)
            ) {
                Icon(
                    painter = painterResource(id = placeType.iconRes),
                    contentDescription = stringResource(R.string.cd_place_type),
                    tint = PrimaryPurple,
                    modifier = Modifier.size(iconSizeMedium)
                )
                Text(
                    text = stringResource(placeType.labelRes),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Address
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacingSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = stringResource(R.string.address),
                    tint = AccentCyan,
                    modifier = Modifier.size(iconSizeMedium)
                )
                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Rating
            if (place.rating != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacingSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.rating),
                        tint = PrimaryPurple,
                        modifier = Modifier.size(iconSizeMedium)
                    )
                    Text(
                        text = stringResource(R.string.rating_format, place.rating.toString()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Website
            if (place.website != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacingSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = stringResource(R.string.website),
                        tint = AccentCyan,
                        modifier = Modifier.size(iconSizeMedium)
                    )
                    Text(
                        text = place.website,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentCyan,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}