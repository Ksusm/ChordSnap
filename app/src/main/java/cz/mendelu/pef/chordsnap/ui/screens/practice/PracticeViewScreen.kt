package cz.mendelu.pef.chordsnap.ui.screens.practice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PracticeViewScreen(
    practiceId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Practice View Screen",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Practice ID: $practiceId")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Saved practice chords will be displayed here")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToEdit) {
            Text("Edit Practice")
        }
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
    }
}