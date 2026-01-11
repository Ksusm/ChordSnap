package cz.mendelu.pef.chordsnap.ui.screens.chordslibrary

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import cz.mendelu.pef.chordsnap.R
import cz.mendelu.pef.chordsnap.database.ChordEntity
import cz.mendelu.pef.chordsnap.ui.components.BottomBar
import cz.mendelu.pef.chordsnap.ui.theme.*

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
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        floatingActionButton = {
            if (selectedChords.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { onNavigateToPracticeCreation(selectedChords.toList()) },
                    containerColor = PrimaryPurple,
                    contentColor = SurfaceWhite,
                    shape = CustomShapes.fab,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = Dimensions.elevationFab
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Dimensions.paddingLarge),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.practice)
                        )
                        Text(
                            text = stringResource(R.string.practice),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Screen title centered at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimensions.screenHorizontalPadding,
                        vertical = Dimensions.paddingLarge
                    )
            ) {
                Text(
                    text = stringResource(R.string.chords_library_title),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )

                if (selectedChords.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.deleteSelectedChords() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_selected),
                            tint = AccentCyan
                        )
                    }
                }
            }

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
                        CircularProgressIndicator(color = PrimaryPurple)
                    }
                }
                is ChordsLibraryUiState.Success -> {
                    if (state.chords.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_chords_yet),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(Dimensions.screenHorizontalPadding),
                            verticalArrangement = Arrangement.spacedBy(Dimensions.cardSpacing)
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
            .padding(Dimensions.screenHorizontalPadding)
            .padding(bottom = Dimensions.paddingMedium)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.search_chords),
                    color = PrimaryPurple
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.cd_search),
                    tint = PrimaryPurple
                )
            },
            trailingIcon = {
                Box {
                    IconButton(onClick = { onFilterExpandChange(true) }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = stringResource(R.string.cd_filter),
                            tint = PrimaryPurple
                        )
                    }

                    DropdownMenu(
                        expanded = filterExpanded,
                        onDismissRequest = { onFilterExpandChange(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.filter_all)) },
                            onClick = { onFilterSelected(null) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.filter_major)) },
                            onClick = { onFilterSelected("major") }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.filter_minor)) },
                            onClick = { onFilterSelected("minor") }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.filter_diminished)) },
                            onClick = { onFilterSelected("diminished") }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.filter_7th)) },
                            onClick = { onFilterSelected("7") }
                        )
                    }
                }
            },
            singleLine = true,
            shape = CustomShapes.searchField,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
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
            .clickable(onClick = onChordClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = CustomShapes.chordCard,
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.elevationSmall
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            AsyncImage(
                model = chord.imageUrl,
                contentDescription = stringResource(R.string.cd_chord_diagram, chord.nameEng),
                modifier = Modifier.size(Dimensions.chordDiagramMedium),
                contentScale = ContentScale.Fit
            )

            Text(
                text = chord.nameEng,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

            Checkbox(
                checked = isSelected,
                onCheckedChange = onCheckboxChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryPurple,
                    uncheckedColor = TextUnselected,
                    checkmarkColor = SurfaceWhite
                )
            )
        }
    }
}