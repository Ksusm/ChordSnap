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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import cz.mendelu.pef.chordsnap.R
import cz.mendelu.pef.chordsnap.database.ChordEntity
import cz.mendelu.pef.chordsnap.ui.theme.*
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.chordDiagramSmall
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.elevationFab
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.elevationMedium
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.paddingLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingMedium
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingSmall
import kotlinx.coroutines.launch

// TEST TAGS
const val TestTagPracticeCreationTitle = "TestTagPracticeCreationTitle"
const val TestTagPracticeCreationNameField = "TestTagPracticeCreationNameField"
const val TestTagPracticeCreationSaveButton = "TestTagPracticeCreationSaveButton"
const val TestTagPracticeCreationBackButton = "TestTagPracticeCreationBackButton"
const val TestTagPracticeCreationAddFab = "TestTagPracticeCreationAddFab"
const val TestTagPracticeCreationEmptyMessage = "TestTagPracticeCreationEmptyMessage"
const val TestTagPracticeCreationLazyColumn = "TestTagPracticeCreationLazyColumn"
const val TestTagPracticeCreationAddDialogTitle = "TestTagPracticeCreationAddDialogTitle"
const val TestTagPracticeCreationSearchLabel = "TestTagPracticeCreationSearchLabel"
const val TestTagPracticeCreationCancelButton = "TestTagPracticeCreationCancelButton"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeCreationScreen(
    practiceId: Long?,
    selectedChordIds: List<String>,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: PracticeCreationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var showAddChordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadPractice(practiceId, selectedChordIds)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.practice_creation_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.testTag(TestTagPracticeCreationTitle)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag(TestTagPracticeCreationBackButton)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = PrimaryPurple
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (viewModel.savePractice()) {
                                    onSaved()
                                }
                            }
                        },
                        modifier = Modifier.testTag(TestTagPracticeCreationSaveButton)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(R.string.cd_save),
                            tint = AccentCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (uiState is PracticeCreationUiState.Success) {
                FloatingActionButton(
                    onClick = { showAddChordDialog = true },
                    containerColor = PrimaryPurple,
                    contentColor = SurfaceWhite,
                    shape = CustomShapes.fab,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = elevationFab
                    ),
                    modifier = Modifier.testTag(TestTagPracticeCreationAddFab)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_chord)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Practice name TextField
            val practiceName = if (uiState is PracticeCreationUiState.Success) {
                (uiState as PracticeCreationUiState.Success).practiceName
            } else {
                ""
            }

            OutlinedTextField(
                value = practiceName,
                onValueChange = { newValue ->
                    if (newValue.length <= 18) {
                        viewModel.updatePracticeName(newValue)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingLarge)
                    .testTag(TestTagPracticeCreationNameField),
                label = {
                    Text(
                        text = stringResource(R.string.practice_name),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                supportingText = {
                    Text(
                        text = stringResource(R.string.practice_name_max_length, practiceName.length),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                singleLine = true,
                shape = CustomShapes.searchField,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    focusedLabelColor = PrimaryPurple,
                    cursorColor = PrimaryPurple,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (val state = uiState) {
                    is PracticeCreationUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = PrimaryPurple
                        )
                    }
                    is PracticeCreationUiState.Success -> {
                        if (state.chords.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingLarge)
                                    .testTag(TestTagPracticeCreationEmptyMessage),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.no_chords_selected),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag(TestTagPracticeCreationLazyColumn),
                                contentPadding = PaddingValues(paddingLarge),
                                verticalArrangement = Arrangement.spacedBy(spacingMedium)
                            ) {
                                itemsIndexed(
                                    items = state.chords,
                                    key = { _, chord -> chord.id }
                                ) { index, chord ->
                                    PracticeChordItem(
                                        chord = chord,
                                        index = index,
                                        isFirst = index == 0,
                                        isLast = index == state.chords.size - 1,
                                        onRemove = { viewModel.removeChord(chord.id) },
                                        onMoveUp = { viewModel.moveChordUp(index) },
                                        onMoveDown = { viewModel.moveChordDown(index) }
                                    )
                                }
                            }
                        }
                    }
                    is PracticeCreationUiState.Error -> {
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
    }

    if (showAddChordDialog) {
        AddChordDialog(
            onDismiss = { showAddChordDialog = false },
            onChordSelected = { chord ->
                viewModel.addChord(chord.id, chord)
                showAddChordDialog = false
            },
            viewModel = hiltViewModel()
        )
    }
}

@Composable
fun PracticeChordItem(
    chord: ChordEntity,
    index: Int,
    isFirst: Boolean,
    isLast: Boolean,
    onRemove: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("practice_chord_item_${chord.id}"),
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
            Text(
                text = "${index + 1}.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryPurple
            )

            AsyncImage(
                model = chord.imageUrl,
                contentDescription = stringResource(R.string.cd_chord_diagram, chord.nameEng),
                modifier = Modifier.size(chordDiagramSmall),
                contentScale = ContentScale.Fit
            )

            Text(
                text = chord.nameEng,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = !isFirst,
                    modifier = Modifier.testTag("move_up_${chord.id}")
                ) {
                    Icon(
                        Icons.Default.ArrowUpward,
                        contentDescription = stringResource(R.string.move_up),
                        tint = if (isFirst) TextUnselected else PrimaryPurple
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.testTag("remove_${chord.id}")
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.remove),
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                IconButton(
                    onClick = onMoveDown,
                    enabled = !isLast,
                    modifier = Modifier.testTag("move_down_${chord.id}")
                ) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        contentDescription = stringResource(R.string.move_down),
                        tint = if (isLast) TextUnselected else PrimaryPurple
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChordDialog(
    onDismiss: () -> Unit,
    onChordSelected: (ChordEntity) -> Unit,
    viewModel: cz.mendelu.pef.chordsnap.ui.screens.chordslibrary.ChordsLibraryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.add_chord),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.testTag(TestTagPracticeCreationAddDialogTitle)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTagPracticeCreationSearchLabel),
                    label = { Text(stringResource(R.string.search_chords)) },
                    singleLine = true,
                    shape = CustomShapes.searchField,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPurple,
                        focusedLabelColor = PrimaryPurple,
                        cursorColor = PrimaryPurple,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(spacingSmall))

                when (val state = uiState) {
                    is cz.mendelu.pef.chordsnap.ui.screens.chordslibrary.ChordsLibraryUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(spacingSmall)
                        ) {
                            items(
                                count = state.chords.size,
                                key = { state.chords[it].id }
                            ) { index ->
                                val chord = state.chords[index]
                                Card(
                                    onClick = { onChordSelected(chord) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = CustomShapes.chordCard,
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(spacingMedium),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacingMedium)
                                    ) {
                                        AsyncImage(
                                            model = chord.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                        Text(
                                            text = chord.nameEng,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryPurple)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag(TestTagPracticeCreationCancelButton)
                ) {
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