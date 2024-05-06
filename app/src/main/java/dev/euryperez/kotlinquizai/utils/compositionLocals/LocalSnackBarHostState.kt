package dev.euryperez.kotlinquizai.utils.compositionLocals

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf

val LocalSnackBarHostState = compositionLocalOf<SnackbarHostState> {
    error("CompositionLocal LocalSnackBarHostState not present")
}