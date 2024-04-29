package dev.euryperez.kotlinquizai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.euryperez.kotlinquizai.ui.theme.KotlinQuizAITheme
import dev.euryperez.kotlinquizai.utils.compositionLocals.LocalNavController
import dev.euryperez.kotlinquizai.utils.compositionLocals.LocalSnackBarHostState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinQuizAITheme(dynamicColor = false) {
                val snackBarHostState = remember { SnackbarHostState() }

                CompositionLocalProvider(
                    LocalSnackBarHostState provides snackBarHostState,
                    LocalNavController provides rememberNavController()
                ) {
                    KotlinQuizApp(snackBarHostState)
                }
            }
        }
    }
}