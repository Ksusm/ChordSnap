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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = if (uiState is PracticeCreationUiState.Success) {
                    (uiState as PracticeCreationUiState.Success).practiceName
                } else {
                    ""
                },
                onValueChange = { viewModel.updatePracticeName(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text("Practice name") },
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