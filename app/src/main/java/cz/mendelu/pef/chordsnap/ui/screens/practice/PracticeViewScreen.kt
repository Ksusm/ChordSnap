package cz.mendelu.pef.chordsnap.ui.screens.practice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import cz.mendelu.pef.chordsnap.R
import cz.mendelu.pef.chordsnap.database.ChordEntity
import cz.mendelu.pef.chordsnap.ui.theme.*
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.chordDiagramLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.elevationMedium
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.paddingLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingMedium
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingXSmall
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeViewScreen(
    practiceId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToChordDetail: (String) -> Unit,
    viewModel: PracticeViewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(practiceId) {
        viewModel.loadPractice(practiceId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    when (val state = uiState) {
                        is PracticeViewUiState.Success -> Text(
                            text = state.practice.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        else -> Text(
                            text = stringResource(R.string.practice_view_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = PrimaryPurple
                        )
                    }
                },
                actions = {
                    if (uiState is PracticeViewUiState.Success) {
                        IconButton(onClick = { onNavigateToEdit(practiceId) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.cd_edit),
                                tint = AccentCyan
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.cd_delete),
                                tint = AccentCyan
                            )
                        }
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
                is PracticeViewUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryPurple
                    )
                }
                is PracticeViewUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(paddingLarge),
                        verticalArrangement = Arrangement.spacedBy(spacingMedium)
                    ) {
                        itemsIndexed(
                            items = state.chords,
                            key = { _, chord -> chord.id }
                        ) { index, chord ->
                            PracticeChordViewItem(
                                chord = chord,
                                index = index,
                                onClick = { onNavigateToChordDetail(chord.id) }
                            )
                        }
                    }
                }
                is PracticeViewUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingLarge),
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.delete_practice_title),
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(stringResource(R.string.delete_practice_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.deletePractice(practiceId)
                            showDeleteDialog = false
                            onNavigateBack()
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = CustomShapes.chordCard
        )
    }
}

@Composable
fun PracticeChordViewItem(
    chord: ChordEntity,
    index: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = CustomShapes.chordCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevationMedium
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacingMedium)
        ) {
            // Index number
            Text(
                text = "${index + 1}.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryPurple
            )

            // Chord diagram
            AsyncImage(
                model = chord.imageUrl,
                contentDescription = stringResource(R.string.cd_chord_diagram, chord.nameEng),
                modifier = Modifier.size(chordDiagramLarge),
                contentScale = ContentScale.Fit
            )

            // Chord info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacingXSmall)
            ) {
                Text(
                    text = chord.nameEng,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(R.string.tap_to_see_details),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Chevron icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = PrimaryPurple
            )
        }
    }
}