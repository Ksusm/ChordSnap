package cz.mendelu.pef.chordsnap.ui.screens.chordslibrary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.mendelu.pef.chordsnap.ui.components.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordsLibraryScreen(
    navController: NavController,
    currentRoute: String?,
    onNavigateToChordDetail: (String) -> Unit,
    onNavigateToPracticeCreation: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chords Library") }
            )
        },
        bottomBar = {
            BottomBar(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Chords Library Screen",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("List of chords will be here (LazyColumn)")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onNavigateToChordDetail("test_chord_id") }) {
                Text("Go to Chord Detail")
            }
            Button(onClick = onNavigateToPracticeCreation) {
                Text("Create Practice")
            }
        }
    }
}