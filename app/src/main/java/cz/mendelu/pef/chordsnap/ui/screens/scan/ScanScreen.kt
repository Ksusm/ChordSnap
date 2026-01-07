package cz.mendelu.pef.chordsnap.ui.screens.scan

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import cz.mendelu.pef.chordsnap.analyzers.ChordTextAnalyzer
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateBack: () -> Unit,
    onChordRecognized: (String) -> Unit,
    viewModel: ScanScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var hasCameraPermission by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    // Camera permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Check permission on start
    LaunchedEffect(Unit) {
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        if (permission == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Handle successful chord recognition
    LaunchedEffect(uiState) {
        if (uiState is ScanUiState.Success) {
            val chord = (uiState as ScanUiState.Success).chord
            onChordRecognized(chord.id)
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan a chord") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasCameraPermission) {
                var analyzer by remember { mutableStateOf<ChordTextAnalyzer?>(null) }

                CameraPreviewWithAnalyzer(
                    modifier = Modifier.fillMaxSize(),
                    onTextRecognized = { recognizedText ->
                        if (uiState is ScanUiState.Idle) {
                            viewModel.searchChordByName(recognizedText)
                        }
                    },
                    onAnalyzerCreated = { createdAnalyzer ->
                        analyzer = createdAnalyzer
                    }
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (uiState) {
                        is ScanUiState.Idle -> {
                            Text(
                                "Position chord name and tap to scan",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )

                            FloatingActionButton(
                                onClick = { analyzer?.triggerScan() }
                            ) {
                                Icon(Icons.Default.CameraAlt, "Scan chord")
                            }
                        }
                        is ScanUiState.Scanning -> {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Recognizing text...", color = Color.White)
                        }
                        is ScanUiState.Processing -> {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Searching chord...", color = Color.White)
                        }
                        is ScanUiState.Error -> {
                            Text(
                                (uiState as ScanUiState.Error).message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red
                            )

                            Button(
                                onClick = { viewModel.resetState() }
                            ) {
                                Text("Try Again")
                            }
                        }
                        is ScanUiState.Success -> {
                            // Will navigate away
                        }
                    }
                }
            } else {
                // No permission
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Camera permission is required to scan chords",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreviewWithAnalyzer(
    modifier: Modifier = Modifier,
    onTextRecognized: (String) -> Unit,
    onAnalyzerCreated: (ChordTextAnalyzer) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = Executors.newSingleThreadExecutor()

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val analyzer = ChordTextAnalyzer { recognizedText ->
                    onTextRecognized(recognizedText)
                }

                onAnalyzerCreated(analyzer)

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor, analyzer)
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
}