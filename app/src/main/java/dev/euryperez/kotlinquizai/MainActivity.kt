package dev.euryperez.kotlinquizai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.euryperez.kotlinquizai.ui.theme.KotlinQuizAITheme
import dev.euryperez.kotlinquizai.utils.compositionLocals.LocalNavController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinQuizAITheme(dynamicColor = false) {
                CompositionLocalProvider(LocalNavController provides rememberNavController()) {
                    KotlinQuizApp()
                }
            }
        }
    }
}