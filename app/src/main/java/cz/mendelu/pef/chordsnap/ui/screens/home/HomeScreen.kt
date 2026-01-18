package cz.mendelu.pef.chordsnap.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import cz.mendelu.pef.chordsnap.R
import cz.mendelu.pef.chordsnap.database.PracticeEntity
import cz.mendelu.pef.chordsnap.ui.components.BottomBar
import cz.mendelu.pef.chordsnap.ui.theme.*

// TEST TAGS
const val TestTagHomeTitle = "TestTagHomeTitle"
const val TestTagHomeSettingsButton = "TestTagHomeSettingsButton"
const val TestTagHomeScanFab = "TestTagHomeScanFab"
const val TestTagHomePracticesRow = "TestTagHomePracticesRow"
const val TestTagHomeNoPracticesMessage = "TestTagHomeNoPracticesMessage"

@Composable
fun HomeScreen(
    navController: NavController,
    currentRoute: String?,
    onNavigateToScan: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPractice: (Long) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToScan,
                containerColor = PrimaryPurple,
                contentColor = SurfaceWhite,
                shape = CustomShapes.fab,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = Dimensions.elevationFab
                ),
                modifier = Modifier.testTag(TestTagHomeScanFab)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = stringResource(R.string.scan_chord)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(
                horizontal = Dimensions.screenHorizontalPadding,
                vertical = Dimensions.screenVerticalPadding
            )
        ) {
            // Header with app name and settings
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimensions.paddingLarge),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.displayLarge,
                        color = AppNamePurple,
                        modifier = Modifier.testTag(TestTagHomeTitle)
                    )

                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag(TestTagHomeSettingsButton)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.cd_settings),
                            tint = AccentCyan
                        )
                    }
                }
            }

            // Main image section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimensions.paddingSmall),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.home_ready_to_explore),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = Dimensions.spacingLarge)
                    )

                    Card(
                        shape = CustomShapes.mainImage,
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = Dimensions.elevationLarge
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(Dimensions.mainImageAspectRatio)
                    ) {
                        AsyncImage(
                            model = "file:///android_asset/images/main_image.jpeg",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimensions.spacingXLarge))
            }

            // Practices section header
            item {
                Text(
                    text = stringResource(R.string.home_your_practices),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(
                        top = Dimensions.spacingMedium,
                        bottom = Dimensions.spacingXSmall
                    )
                )
            }

            // Content based on state
            item {
                when (val state = uiState) {
                    is HomeScreenUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimensions.paddingXLarge),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryPurple)
                        }
                    }
                    is HomeScreenUiState.Error -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = CustomShapes.chordCard,
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = Dimensions.elevationSmall
                            )
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(Dimensions.cardPaddingInternal)
                            )
                        }
                    }
                    is HomeScreenUiState.Success -> {
                        if (state.practices.isEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag(TestTagHomeNoPracticesMessage),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                shape = CustomShapes.practiceCard,
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = Dimensions.elevationSmall
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Dimensions.paddingXLarge),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(R.string.home_no_practices),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(Dimensions.spacingSmall))
                                    Text(
                                        text = stringResource(R.string.home_no_practices_subtitle),
                                        style = MaterialTheme.typography.labelLarge,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Practice cards - only when Success with practices
            if (uiState is HomeScreenUiState.Success &&
                (uiState as HomeScreenUiState.Success).practices.isNotEmpty()) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.cardSpacing),
                        modifier = Modifier.testTag(TestTagHomePracticesRow)
                    ) {
                        items(
                            items = (uiState as HomeScreenUiState.Success).practices,
                            key = { it.id }
                        ) { practice ->
                            PracticeCard(
                                practice = practice,
                                onClick = { onNavigateToPractice(practice.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PracticeCard(
    practice: PracticeEntity,
    onClick: () -> Unit
) {
    val chordCount = practice.chordIds.split(",").filter { it.isNotBlank() }.size
    val chordText = if (chordCount == 1) {
        stringResource(R.string.home_chord_count, chordCount)
    } else {
        stringResource(R.string.home_chords_count, chordCount)
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(Dimensions.practiceCardWidth)
            .height(Dimensions.practiceCardHeight)
            .testTag("practice_card_${practice.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = CustomShapes.practiceCard,
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.elevationMedium
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
                    .background(
                        brush = GradientUtils.practiceNameGradient(
                            startColor = MaterialTheme.colorScheme.tertiary,
                            endColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                    .padding(Dimensions.cardPaddingInternal),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = practice.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiary,
                    maxLines = 2
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .padding(horizontal = Dimensions.cardPaddingInternal),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = chordText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}