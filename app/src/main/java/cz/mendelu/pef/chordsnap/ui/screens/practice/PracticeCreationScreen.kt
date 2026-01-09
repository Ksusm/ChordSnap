package cz.mendelu.pef.chordsnap.ui.screens.practice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import cz.mendelu.pef.chordsnap.database.ChordEntity
import kotlinx.coroutines.launch

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
        topBar = {
            TopAppBar(
                title = { Text("Practice") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
                        }
                    ) {
                        Icon(Icons.Default.Check, "Save")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState is PracticeCreationUiState.Success) {
                FloatingActionButton(
                    onClick = { showAddChordDialog = true }
                ) {
                    Icon(Icons.Default.Add, "Add chord")
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
                    .padding(16.dp),
                label = { Text("Practice name") },
                supportingText = { Text("${practiceName.length}/18") },
                singleLine = true
            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (val state = uiState) {
                    is PracticeCreationUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is PracticeCreationUiState.Success -> {
                        if (state.chords.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No chords selected",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                .padding(16.dp),
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${index + 1}.",
                style = MaterialTheme.typography.titleMedium
            )

            AsyncImage(
                model = chord.imageUrl,
                contentDescription = "Diagram for ${chord.nameEng}",
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = chord.nameEng,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = !isFirst
                ) {
                    Icon(
                        Icons.Default.ArrowUpward,
                        "Move up",
                        tint = if (isFirst) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                IconButton(
                    onClick = onMoveDown,
                    enabled = !isLast
                ) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        "Move down",
                        tint = if (isLast) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.primary
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
        title = { Text("Add chord") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search chords") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                when (val state = uiState) {
                    is cz.mendelu.pef.chordsnap.ui.screens.chordslibrary.ChordsLibraryUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                count = state.chords.size,
                                key = { state.chords[it].id }
                            ) { index ->
                                val chord = state.chords[index]
                                Card(
                                    onClick = { onChordSelected(chord) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        AsyncImage(
                                            model = chord.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                        Text(
                                            text = chord.nameEng,
                                            style = MaterialTheme.typography.bodyLarge
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
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}