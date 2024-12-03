package dev.euryperez.kotlinquizai.features.difficultyLevel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.euryperez.kotlinquizai.R
import dev.euryperez.kotlinquizai.models.DifficultyLevel
import dev.euryperez.kotlinquizai.navigation.KotlinQuizAppRoute
import dev.euryperez.kotlinquizai.navigation.QuizGameRoute
import dev.euryperez.kotlinquizai.ui.theme.KotlinQuizAITheme
import dev.euryperez.kotlinquizai.ui.buttons.PrimaryButton
import dev.euryperez.kotlinquizai.utils.compositionLocals.LocalNavController

@Composable
fun DifficultyLevelScreen(
    modifier: Modifier = Modifier,
    navController: NavController = LocalNavController.current
) {
    DifficultyLevelContent(modifier = modifier, onNavigate = navController::navigate)
}

@Composable
private fun DifficultyLevelContent(
    onNavigate: (KotlinQuizAppRoute) -> Unit,
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
            onClick = { onNavigate(QuizGameRoute(DifficultyLevel.BASIC)) }
        )

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.intermediate),
            onClick = { onNavigate(QuizGameRoute(DifficultyLevel.MEDIUM)) }
        )

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.advanced),
            onClick = { onNavigate(QuizGameRoute(DifficultyLevel.ADVANCED)) }
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