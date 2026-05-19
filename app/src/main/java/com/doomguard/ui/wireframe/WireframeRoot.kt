package com.doomguard.ui.wireframe

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.doomguard.DoomGuardApp
import com.doomguard.ui.DoomViewModel
import com.doomguard.ui.theme.DoomBrushes
import com.doomguard.ui.theme.DoomGuardTheme
import com.doomguard.ui.theme.DoomThemeVariant
import com.doomguard.ui.theme.WireframeColors
import kotlinx.coroutines.flow.first

@Composable
fun WireframeRoot() {
    val context = LocalContext.current
    val vm: DoomViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application,
        ),
    )
    val nav = rememberNavController()
    DoomGuardTheme(variant = DoomThemeVariant.LightMain) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            NavHost(
                navController = nav,
                startDestination = "bootstrap",
            ) {
                composable("bootstrap") {
                    LaunchedEffect(Unit) {
                        val app = context.applicationContext as DoomGuardApp
                        // Read DataStore directly — vm.onboardingComplete.first() only sees stateIn’s
                        // initial value before prefs load, so onboarding would repeat every cold start.
                        val done = app.preferences.onboardingComplete.first()
                        val nameSaved = app.preferences.displayName.first().trim().isNotEmpty()
                        when {
                            !done -> nav.navigate("onboarding") {
                                popUpTo("bootstrap") { inclusive = true }
                            }
                            !nameSaved -> nav.navigate("name") {
                                popUpTo("bootstrap") { inclusive = true }
                            }
                            else -> nav.navigate("main") {
                                popUpTo("bootstrap") { inclusive = true }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DoomBrushes.hullVertical),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = WireframeColors.CyanBright)
                    }
                }
                composable("onboarding") {
                    OnboardingFlow(vm) {
                        nav.navigate("name") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                }
                composable("name") {
                    NameEntryScreen(vm) {
                        nav.navigate("main") {
                            popUpTo("name") { inclusive = true }
                        }
                    }
                }
                composable("main") {
                    WireframeMainShell(nav, vm)
                }
                composable("focus") {
                    FocusModeScreen { nav.popBackStack() }
                }
                composable("achievements") {
                    AchievementsScreen { nav.popBackStack() }
                }
            }
        }
    }
}
