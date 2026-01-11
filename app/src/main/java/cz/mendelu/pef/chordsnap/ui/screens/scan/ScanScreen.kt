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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import cz.mendelu.pef.chordsnap.R
import cz.mendelu.pef.chordsnap.analyzers.ChordTextAnalyzer
import cz.mendelu.pef.chordsnap.ui.theme.*
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.elevationFab
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.paddingLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.paddingXLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingSmall
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
        containerColor = Color.Black,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.scan_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = SurfaceWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = SurfaceWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
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
                        .padding(paddingXLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacingLarge)
                ) {
                    when (uiState) {
                        is ScanUiState.Idle -> {
                            Text(
                                text = stringResource(R.string.scan_instruction),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = SurfaceWhite,
                                textAlign = TextAlign.Center
                            )

                            FloatingActionButton(
                                onClick = { analyzer?.triggerScan() },
                                containerColor = PrimaryPurple,
                                contentColor = SurfaceWhite,
                                shape = CustomShapes.fab,
                                elevation = FloatingActionButtonDefaults.elevation(
                                    defaultElevation = elevationFab
                                )
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = stringResource(R.string.scan_chord)
                                )
                            }
                        }
                        is ScanUiState.Processing -> {
                            CircularProgressIndicator(
                                color = PrimaryPurple
                            )
                            Spacer(modifier = Modifier.height(spacingSmall))
                            Text(
                                text = stringResource(R.string.scan_searching),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = SurfaceWhite
                            )
                        }
                        is ScanUiState.Error -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = CustomShapes.chordCard,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    text = stringResource((uiState as ScanUiState.Error).messageResId),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(paddingLarge)
                                )
                            }

                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(2000)
                                viewModel.resetState()
                            }

                            Spacer(modifier = Modifier.height(spacingLarge))

                            FloatingActionButton(
                                onClick = { analyzer?.triggerScan() },
                                containerColor = PrimaryPurple,
                                contentColor = SurfaceWhite,
                                shape = CustomShapes.fab,
                                elevation = FloatingActionButtonDefaults.elevation(
                                    defaultElevation = elevationFab
                                )
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = stringResource(R.string.scan_chord)
                                )
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
                        .padding(paddingXLarge),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                   // verticalArrangement = Arrangement.spacedBy(spacingLarge)
                ) {
                    Text(
                        text = stringResource(R.string.camera_permission_required),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple,
                            contentColor = SurfaceWhite
                        ),
                        shape = CustomShapes.chordCard
                    ) {
                        Text(
                            text = stringResource(R.string.grant_permission),
                            fontWeight = FontWeight.SemiBold
                        )
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