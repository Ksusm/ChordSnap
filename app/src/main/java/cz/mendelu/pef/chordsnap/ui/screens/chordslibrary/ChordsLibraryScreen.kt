package cz.mendelu.pef.chordsnap.ui.screens.chordslibrary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import cz.mendelu.pef.chordsnap.database.ChordEntity
import cz.mendelu.pef.chordsnap.ui.components.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordsLibraryScreen(
    navController: NavHostController,
    currentRoute: String?,
    onNavigateToChordDetail: (String) -> Unit,
    onNavigateToPracticeCreation: (List<String>) -> Unit,
    viewModel: ChordsLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedChords by viewModel.selectedChords.collectAsState()

    var showFilterMenu by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelection()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chords library") },
                actions = {
                    if (selectedChords.isNotEmpty()) {
                        IconButton(onClick = { viewModel.deleteSelectedChords() }) {
                            Icon(Icons.Default.Delete, "Delete selected")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        floatingActionButton = {
            if (selectedChords.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { onNavigateToPracticeCreation(selectedChords.toList()) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, "Practice")
                        Text("Practice")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBarWithFilter(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                filterExpanded = showFilterMenu,
                onFilterExpandChange = { showFilterMenu = it },
                onFilterSelected = { type ->
                    viewModel.filterByType(type)
                    showFilterMenu = false
                }
            )

            when (val state = uiState) {
                is ChordsLibraryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ChordsLibraryUiState.Success -> {
                    if (state.chords.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No chords yet. Scan some chords to get started!",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.chords,
                                key = { it.id }
                            ) { chord ->
                                ChordListItem(
                                    chord = chord,
                                    isSelected = selectedChords.contains(chord.id),
                                    onChordClick = { onNavigateToChordDetail(chord.id) },
                                    onCheckboxChange = { isChecked ->
                                        viewModel.toggleChordSelection(chord.id, isChecked)
                                    }
                                )
                            }
                        }
                    }
                }
                is ChordsLibraryUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
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
fun SearchBarWithFilter(
    query: String,
    onQueryChange: (String) -> Unit,
    filterExpanded: Boolean,
    onFilterExpandChange: (Boolean) -> Unit,
    onFilterSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search chords") },
            leadingIcon = {
                Icon(Icons.Default.Search, "Search")
            },
            trailingIcon = {
                Box {
                    IconButton(onClick = { onFilterExpandChange(true) }) {
                        Icon(Icons.Default.Menu, "Filter")
                    }

                    DropdownMenu(
                        expanded = filterExpanded,
                        onDismissRequest = { onFilterExpandChange(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All") },
                            onClick = { onFilterSelected(null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Major") },
                            onClick = { onFilterSelected("major") }
                        )
                        DropdownMenuItem(
                            text = { Text("Minor") },
                            onClick = { onFilterSelected("minor") }
                        )
                        DropdownMenuItem(
                            text = { Text("Diminished") },
                            onClick = { onFilterSelected("diminished") }
                        )
                        DropdownMenuItem(
                            text = { Text("7th") },
                            onClick = { onFilterSelected("7") }
                        )
                    }
                }
            },
            singleLine = true
        )
    }
}

@Composable
fun ChordListItem(
    chord: ChordEntity,
    isSelected: Boolean,
    onChordClick: () -> Unit,
    onCheckboxChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onChordClick)
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
                contentDescription = "Diagram for ${chord.nameEng}",
                modifier = Modifier
                    .size(80.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = chord.nameEng,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Checkbox(
                checked = isSelected,
                onCheckedChange = onCheckboxChange
            )
        }
    }
}