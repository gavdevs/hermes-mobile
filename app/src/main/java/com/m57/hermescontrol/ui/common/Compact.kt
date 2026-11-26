package com.m57.hermescontrol.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration

/** Height-only boundary, provided at the theme root; IME size does not select a different layout. */
val LocalCompactLayout: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { false }

@Composable
fun rememberCompactLayout(): Boolean = LocalConfiguration.current.screenHeightDp < 520
