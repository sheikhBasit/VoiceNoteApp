package com.abstudio.voicenote.core.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass?> { null }

@Composable
fun DeviceConfigurationProvider(
    windowSizeClass: WindowSizeClass,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        content()
    }
}

/**
 * Utility to check if the current screen is "Expanded" (Tablet/Large screen)
 */
@Composable
fun isExpandedScreen(): Boolean {
    val windowSize = LocalWindowSizeClass.current
    return windowSize?.widthSizeClass == WindowWidthSizeClass.Expanded
}

/**
 * Utility to check if the current screen is "Medium" (Foldable/Small Tablet)
 */
@Composable
fun isMediumScreen(): Boolean {
    val windowSize = LocalWindowSizeClass.current
    return windowSize?.widthSizeClass == WindowWidthSizeClass.Medium
}
