package cz.mendelu.pef.chordsnap

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import cz.mendelu.pef.chordsnap.datastore.UserPreferencesManager
import cz.mendelu.pef.chordsnap.navigation.NavGraph
import cz.mendelu.pef.chordsnap.ui.theme.ChordSnapTheme
import cz.mendelu.pef.chordsnap.utils.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: UserPreferencesManager

    override fun attachBaseContext(newBase: Context) {
        val preferences = UserPreferencesManager(newBase)
        val savedLanguage = runBlocking {
            val storedLanguage = preferences.getStoredLanguage()
            storedLanguage ?: getSystemLanguage(newBase)
        }

        val context = LocaleHelper.setLocale(newBase, savedLanguage)
        super.attachBaseContext(context)
    }

    private fun getSystemLanguage(context: Context): String {
        val systemLocale = context.resources.configuration.locales[0]
        return when (systemLocale.language) {
            "cs" -> "cs"
            else -> "en"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        windowInsetsController.show(WindowInsetsCompat.Type.statusBars())

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            val theme by preferencesManager.themeFlow.collectAsState(initial = "system")

            val darkTheme = when (theme) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            ChordSnapTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(navController = navController)
                }
            }
        }
    }
}