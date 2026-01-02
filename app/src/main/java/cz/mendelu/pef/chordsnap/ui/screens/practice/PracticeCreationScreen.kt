package cz.mendelu.pef.chordsnap.ui.screens.practice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PracticeCreationScreen(
    onNavigateBack: () -> Unit,
    onSavePractice: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Practice Creation Screen",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Practice name input and chord arrangement will be here")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onSavePractice("test_practice_id") }) {
            Text("Save Practice")
        }
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
    }
}