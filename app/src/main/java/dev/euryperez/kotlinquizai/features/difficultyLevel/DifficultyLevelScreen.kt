package dev.euryperez.kotlinquizai.features.difficultyLevel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.euryperez.kotlinquizai.R
import dev.euryperez.kotlinquizai.models.DifficultyLevel
import dev.euryperez.kotlinquizai.ui.theme.KotlinQuizAITheme
import dev.euryperez.kotlinquizai.ui.buttons.PrimaryButton
import dev.euryperez.kotlinquizai.utils.AppNavigation.QuizGameDestination
import dev.euryperez.kotlinquizai.utils.NavRoute
import dev.euryperez.kotlinquizai.utils.compositionLocals.LocalNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DifficultyLevelScreen(
    modifier: Modifier = Modifier,
    navController: NavController = LocalNavController.current
) {
    DifficultyLevelContent(
        modifier = modifier,
        onNavigate = { navController.navigate(it.route) }
    )
}

@Composable
private fun DifficultyLevelContent(
    onNavigate: (NavRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 32.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(stringResource(R.string.choose_your_difficulty_level))

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.basic),
            onClick = {
                onNavigate(QuizGameDestination.getNavRoute(DifficultyLevel.BASIC))
            }
        )

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.intermediate),
            onClick = {
                onNavigate(QuizGameDestination.getNavRoute(DifficultyLevel.MEDIUM))
            }
        )

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.advanced),
            onClick = {
                onNavigate(QuizGameDestination.getNavRoute(DifficultyLevel.ADVANCED))
            }
        )
    }
}

@PreviewLightDark
@Composable
private fun DifficultyLevelScreenPreview() {
    KotlinQuizAITheme(dynamicColor = false) {
        Scaffold {
            DifficultyLevelContent(
                modifier = Modifier.padding(it),
                onNavigate = {}
            )
        }
    }
}