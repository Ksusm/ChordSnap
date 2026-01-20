package cz.mendelu.pef.chordsnap.ui.screens.settings

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import cz.mendelu.pef.chordsnap.R
import cz.mendelu.pef.chordsnap.ui.theme.*
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.cardPaddingInternal
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.elevationMedium
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.paddingLarge
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingMedium
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingSmall
import cz.mendelu.pef.chordsnap.ui.theme.Dimensions.spacingXSmall
import kotlinx.coroutines.launch

const val TestTagSettingsTitle = "TestTagSettingsTitle"
const val TestTagSettingsBackButton = "TestTagSettingsBackButton"
const val TestTagSettingsLanguageItem = "TestTagSettingsLanguageItem"
const val TestTagSettingsThemeItem = "TestTagSettingsThemeItem"
const val TestTagSettingsLanguageDialog = "TestTagSettingsLanguageDialog"
const val TestTagSettingsThemeDialog = "TestTagSettingsThemeDialog"
const val TestTagSettingsLanguageEnglish = "TestTagSettingsLanguageEnglish"
const val TestTagSettingsLanguageCzech = "TestTagSettingsLanguageCzech"
const val TestTagSettingsThemeLight = "TestTagSettingsThemeLight"
const val TestTagSettingsThemeDark = "TestTagSettingsThemeDark"
const val TestTagSettingsThemeSystem = "TestTagSettingsThemeSystem"
const val TestTagSettingsCancelButton = "TestTagSettingsCancelButton"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val language by viewModel.language.collectAsState()
    val theme by viewModel.theme.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.testTag(TestTagSettingsTitle)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag(TestTagSettingsBackButton)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = PrimaryPurple
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // App Section
            SettingsSectionTitle(title = stringResource(R.string.settings_app_settings))

            // Language Setting
            SettingsItem(
                title = stringResource(R.string.settings_language),
                subtitle = when (language) {
                    "en" -> stringResource(R.string.settings_language_english)
                    "cs" -> stringResource(R.string.settings_language_czech)
                    else -> stringResource(R.string.settings_language_english)
                },
                onClick = { showLanguageDialog = true },
                modifier = Modifier.testTag(TestTagSettingsLanguageItem)
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = paddingLarge),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // Theme Setting
            SettingsItem(
                title = stringResource(R.string.settings_theme),
                subtitle = when (theme) {
                    "light" -> stringResource(R.string.settings_theme_light)
                    "dark" -> stringResource(R.string.settings_theme_dark)
                    "system" -> stringResource(R.string.settings_theme_system)
                    else -> stringResource(R.string.settings_theme_system)
                },
                onClick = { showThemeDialog = true },
                modifier = Modifier.testTag(TestTagSettingsThemeItem)
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = paddingLarge),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // About Section
            SettingsSectionTitle(title = stringResource(R.string.settings_about))

            SettingsItem(
                title = stringResource(R.string.settings_version),
                subtitle = "1.0.0",
                onClick = { }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = paddingLarge),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // Detailed About Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingLarge),
                shape = CustomShapes.chordCard,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = elevationMedium
                )
            ) {
                Column(
                    modifier = Modifier.padding(cardPaddingInternal),
                    verticalArrangement = Arrangement.spacedBy(spacingMedium)
                ) {
                    Text(
                        text = stringResource(R.string.settings_app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryPurple
                    )
                    Text(
                        text = stringResource(R.string.settings_app_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(spacingXSmall))
                    Text(
                        text = stringResource(R.string.settings_app_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacingXSmall))
                    Text(
                        text = stringResource(R.string.settings_app_project_info),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.select_language),
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacingSmall),
                    modifier = Modifier.testTag(TestTagSettingsLanguageDialog)
                ) {
                    LanguageOption(
                        language = stringResource(R.string.settings_language_english),
                        isSelected = language == "en",
                        onClick = {
                            scope.launch {
                                viewModel.setLanguage("en")
                                kotlinx.coroutines.delay(100)
                                showLanguageDialog = false
                                (context as? ComponentActivity)?.recreate()
                            }
                        },
                        modifier = Modifier.testTag(TestTagSettingsLanguageEnglish)
                    )
                    LanguageOption(
                        language = stringResource(R.string.settings_language_czech),
                        isSelected = language == "cs",
                        onClick = {
                            scope.launch {
                                viewModel.setLanguage("cs")
                                kotlinx.coroutines.delay(100)
                                showLanguageDialog = false
                                (context as? ComponentActivity)?.recreate()
                            }
                        },
                        modifier = Modifier.testTag(TestTagSettingsLanguageCzech)
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showLanguageDialog = false },
                    modifier = Modifier.testTag(TestTagSettingsCancelButton)
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = PrimaryPurple,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            shape = CustomShapes.chordCard
        )
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.select_theme),
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacingSmall),
                    modifier = Modifier.testTag(TestTagSettingsThemeDialog)
                ) {
                    ThemeOption(
                        theme = stringResource(R.string.settings_theme_light),
                        isSelected = theme == "light",
                        onClick = {
                            viewModel.setTheme("light")
                            showThemeDialog = false
                        },
                        modifier = Modifier.testTag(TestTagSettingsThemeLight)
                    )
                    ThemeOption(
                        theme = stringResource(R.string.settings_theme_dark),
                        isSelected = theme == "dark",
                        onClick = {
                            viewModel.setTheme("dark")
                            showThemeDialog = false
                        },
                        modifier = Modifier.testTag(TestTagSettingsThemeDark)
                    )
                    ThemeOption(
                        theme = stringResource(R.string.settings_theme_system),
                        isSelected = theme == "system",
                        onClick = {
                            viewModel.setTheme("system")
                            showThemeDialog = false
                        },
                        modifier = Modifier.testTag(TestTagSettingsThemeSystem)
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showThemeDialog = false },
                    modifier = Modifier.testTag(TestTagSettingsCancelButton)
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = PrimaryPurple,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            shape = CustomShapes.chordCard
        )
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = PrimaryPurple,
        modifier = Modifier.padding(
            horizontal = paddingLarge,
            vertical = paddingLarge
        )
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingLarge, vertical = paddingLarge),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacingXSmall)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LanguageOption(
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = CustomShapes.chordCard,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacingSmall)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = PrimaryPurple,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = language,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ThemeOption(
    theme: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = CustomShapes.chordCard,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacingSmall)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = PrimaryPurple,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = theme,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}