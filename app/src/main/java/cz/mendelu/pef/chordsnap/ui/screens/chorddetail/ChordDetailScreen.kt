package cz.mendelu.pef.chordsnap.ui.screens.chorddetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import cz.mendelu.pef.chordsnap.R
import cz.mendelu.pef.chordsnap.ui.theme.*
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.cardPaddingInternal
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.chordDiagramDetail
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.elevationMedium
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.paddingLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingMedium
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingSmall
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingXLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingXSmall

const val TestTagChordDetailTitle = "TestTagChordDetailTitle"
const val TestTagChordDetailBackButton = "TestTagChordDetailBackButton"
const val TestTagChordDetailLoading = "TestTagChordDetailLoading"
const val TestTagChordDetailError = "TestTagChordDetailError"
const val TestTagChordDetailContent = "TestTagChordDetailContent"
const val TestTagChordDetailDiagramCard = "TestTagChordDetailDiagramCard"
const val TestTagChordDetailNotesCard = "TestTagChordDetailNotesCard"
const val TestTagChordDetailInfoCard = "TestTagChordDetailInfoCard"
const val TestTagChordDetailDiagramTitle = "TestTagChordDetailDiagramTitle"
const val TestTagChordDetailNotesTitle = "TestTagChordDetailNotesTitle"
const val TestTagChordDetailInfoTitle = "TestTagChordDetailInfoTitle"
const val TestTagChordDetailChordTypeLabel = "TestTagChordDetailChordTypeLabel"
const val TestTagChordDetailBaseNoteLabel = "TestTagChordDetailBaseNoteLabel"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordDetailScreen(
    chordId: String,
    onNavigateBack: () -> Unit,
    viewModel: ChordDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(chordId) {
        viewModel.loadChord(chordId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (val state = uiState) {
                            is ChordDetailUiState.Success -> state.chord.name.eng
                            else -> stringResource(R.string.chord_detail_title)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.testTag(TestTagChordDetailTitle)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag(TestTagChordDetailBackButton)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = PrimaryPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ChordDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag(TestTagChordDetailLoading),
                        color = PrimaryPurple
                    )
                }
                is ChordDetailUiState.Success -> {
                    ChordDetailContent(state)
                }
                is ChordDetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingLarge)
                            .testTag(TestTagChordDetailError),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChordDetailContent(state: ChordDetailUiState.Success) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingLarge)
            .testTag(TestTagChordDetailContent),
        verticalArrangement = Arrangement.spacedBy(spacingXLarge)
    ) {
        // Chord Diagram Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TestTagChordDetailDiagramCard),
            shape = CustomShapes.chordCard,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Dimensions.elevationMedium
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(cardPaddingInternal),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacingMedium)
            ) {
                Text(
                    text = stringResource(R.string.chord_diagram),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.testTag(TestTagChordDetailDiagramTitle)
                )

                AsyncImage(
                    model = state.chord.images.pos1,
                    contentDescription = stringResource(R.string.cd_chord_diagram, state.chord.name.eng),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chordDiagramDetail),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Notes in Chord Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TestTagChordDetailNotesCard),
            shape = CustomShapes.chordCard,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevationMedium
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(cardPaddingInternal),
                verticalArrangement = Arrangement.spacedBy(spacingMedium)
            ) {
                Text(
                    text = stringResource(R.string.notes_in_chord),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.testTag(TestTagChordDetailNotesTitle)
                )

                // Notes with gradient badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacingSmall)
                ) {
                    state.chord.notes.forEach { note ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GradientUtils.noteBadgeGradient(
                                    startColor = MaterialTheme.colorScheme.tertiary,
                                    endColor = MaterialTheme.colorScheme.tertiaryContainer
                                ))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = note,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium,
                                color = SurfaceWhite
                            )
                        }
                    }
                }
            }
        }

        // Chord Information Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TestTagChordDetailInfoCard),
            shape = CustomShapes.chordCard,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevationMedium
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(cardPaddingInternal),
                verticalArrangement = Arrangement.spacedBy(spacingMedium)
            ) {
                Text(
                    text = stringResource(R.string.chord_information),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.testTag(TestTagChordDetailInfoTitle)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Chord Type
                    Column {
                        Text(
                            text = stringResource(R.string.chord_type),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag(TestTagChordDetailChordTypeLabel)
                        )
                        Spacer(modifier = Modifier.height(spacingXSmall))
                        Text(
                            text = state.chordType?.name?.eng ?: state.chord.typeId,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Base Note
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = stringResource(R.string.base_note),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag(TestTagChordDetailBaseNoteLabel)
                        )
                        Spacer(modifier = Modifier.height(spacingXSmall))
                        Text(
                            text = state.baseNote?.name?.eng ?: state.chord.noteId,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}