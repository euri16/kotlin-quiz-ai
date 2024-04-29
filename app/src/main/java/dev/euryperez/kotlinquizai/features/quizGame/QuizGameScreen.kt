package dev.euryperez.kotlinquizai.features.quizGame

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.euryperez.kotlinquizai.R
import dev.euryperez.kotlinquizai.features.quizGame.QuizGameViewModel.GameState
import dev.euryperez.kotlinquizai.features.quizGame.QuizGameViewModel.ViewEffect
import dev.euryperez.kotlinquizai.models.Answer
import dev.euryperez.kotlinquizai.models.Question
import dev.euryperez.kotlinquizai.features.quizGame.QuizGameViewModel.ViewEvent
import dev.euryperez.kotlinquizai.ui.theme.green
import dev.euryperez.kotlinquizai.ui.theme.KotlinQuizAITheme
import dev.euryperez.kotlinquizai.ui.buttons.PrimaryButton
import dev.euryperez.kotlinquizai.utils.compositionLocals.LocalNavController
import dev.euryperez.kotlinquizai.utils.compositionLocals.LocalSnackBarHostState
import kotlinx.coroutines.delay

@Composable
fun QuizGameScreen(
    modifier: Modifier = Modifier,
    viewModel: QuizGameViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current,
    navController: NavController = LocalNavController.current
) {
    val state by viewModel.viewStateFlow.collectAsState()

    when (val gameState = state.gameState) {
        is GameState.Finished ->
            QuizFinishedDialog(gameState = gameState, onDismiss = navController::popBackStack)

        is GameState.InProgress ->
            QuizGameScreen(
                gameState = gameState,
                isLoading = state.isLoading,
                onEvent = viewModel::processEvent,
                modifier = modifier
            )
    }


    val context = LocalContext.current

    LaunchedEffect(state.viewEffect) {
        state.viewEffect?.let { viewEffect ->
            when (viewEffect) {
                ViewEffect.ErrorGettingDifficultyLevel -> {
                    snackBarHostState.showSnackbar(context.getString(R.string.error_starting_quiz))
                    navController.popBackStack()
                }

                is ViewEffect.ShowSnackBar ->
                    snackBarHostState.showSnackbar(context.getString(viewEffect.stringRes))
            }

            viewModel.processEvent(ViewEvent.ConsumeEffect)
        }
    }
}

@Composable
private fun QuizGameScreen(
    gameState: GameState.InProgress,
    isLoading: Boolean,
    onEvent: (ViewEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isLoading,
        label = "",
        transitionSpec = {
            slideInVertically(initialOffsetY = { it })
                .togetherWith(slideOutVertically(targetOffsetY = { -it }) + fadeOut())
        }
    ) {
        if (it) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            Column(modifier = modifier) {
                QuestionContainer(
                    question = gameState.selectedQuestion,
                    selectedAnswerId = gameState.selectedAnswerId,
                    onEvent = onEvent
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    modifier = Modifier.padding(24.dp),
                    text = stringResource(R.string.skip),
                    enabled = gameState.selectedAnswerId == null,
                    onClick = { onEvent(ViewEvent.NextQuestion) }
                )
            }
        }
    }
}

@Composable
private fun QuestionContainer(
    question: Question,
    onEvent: (ViewEvent) -> Unit,
    modifier: Modifier = Modifier,
    selectedAnswerId: String? = null,
) {
    var timeLeft by remember { mutableIntStateOf(TIMER_SECONDS) }

    LaunchedEffect(question) { timeLeft = TIMER_SECONDS }

    Column(modifier = modifier) {
        CountdownIndicator(
            timeLeft = timeLeft,
            isCountdownPaused = selectedAnswerId != null,
            tick = { timeLeft-- },
            onCountdownFinished = {
                onEvent(ViewEvent.NextQuestion).also { timeLeft = TIMER_SECONDS }
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        AnimatedContent(
            targetState = question,
            label = "",
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it })
                    .togetherWith(slideOutHorizontally(targetOffsetX = { -it }) + fadeOut())
            }
        ) {
            QuestionItem(
                modifier = Modifier.padding(16.dp),
                question = it,
                selectedAnswerId = selectedAnswerId,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun QuestionItem(
    question: Question,
    onEvent: (ViewEvent) -> Unit,
    modifier: Modifier = Modifier,
    selectedAnswerId: String? = null,
) {
    Column(modifier = modifier) {
        Text(
            question.question,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(50.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            question.options.forEach { option ->
                AnswerItem(
                    answer = option,
                    explanation = question.explanation,
                    selectedAnswerId = selectedAnswerId,
                    onEvent = onEvent
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun AnswerItem(
    answer: Answer,
    explanation: String,
    onEvent: (ViewEvent) -> Unit,
    modifier: Modifier = Modifier,
    selectedAnswerId: String? = null
) {
    val isSelectedAnswer = selectedAnswerId == answer.id

    val borderColor = when {
        isSelectedAnswer.not() -> Color.Transparent
        answer.isCorrectAnswer -> MaterialTheme.colorScheme.green
        else -> MaterialTheme.colorScheme.error

    }

    val alpha by animateFloatAsState(
        label = "alpha",
        targetValue = if (selectedAnswerId == null || isSelectedAnswer) 1f else 0.3f
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clickable(enabled = selectedAnswerId == null) {
                onEvent(ViewEvent.SelectAnswer(answer))
            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(3.dp, borderColor)
    ) {
        val text = if (isSelectedAnswer && answer.isCorrectAnswer) explanation else answer.text

        AnimatedContent(targetState = text, label = "") {
            Text(
                it,
                modifier = Modifier.padding(20.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun CountdownIndicator(
    modifier: Modifier = Modifier,
    timeLeft: Int,
    isCountdownPaused: Boolean = false,
    tick: () -> Unit,
    onCountdownFinished: () -> Unit
) {
    val progressDecimal by remember(timeLeft) {
        mutableFloatStateOf(timeLeft / TIMER_SECONDS.toFloat())
    }

    val progress by animateFloatAsState(targetValue = progressDecimal, label = "")

    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(5.dp)
    )

    if (isCountdownPaused.not()) {
        LaunchedEffect(timeLeft) {
            delay(1000)
            if (timeLeft == 1) onCountdownFinished() else tick()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun PreviewQuizGameScreen() {
    KotlinQuizAITheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Kotlin Quiz AI") }) }
        ) {
            QuizGameScreen(
                modifier = Modifier.padding(it),
                gameState = GameState.InProgress(
                    selectedQuestion = Question(
                        id = "1",
                        question = "What is the capital of France?",
                        options = listOf(
                            Answer("1", "Paris"),
                            Answer("2", "London", isCorrectAnswer = true),
                            Answer("3", "Berlin"),
                            Answer("4", "Madrid")
                        ),
                        correctAnswerId = "1",
                        explanation = "Paris is the capital of France",
                    ),
                ),
                isLoading = false,
                onEvent = {}
            )
        }
    }
}

private const val TIMER_SECONDS = 15
