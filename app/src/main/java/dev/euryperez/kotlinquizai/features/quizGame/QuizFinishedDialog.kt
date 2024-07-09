package dev.euryperez.kotlinquizai.features.quizGame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.euryperez.kotlinquizai.R
import dev.euryperez.kotlinquizai.features.quizGame.QuizGameViewModel.GameStatus
import dev.euryperez.kotlinquizai.models.Answer
import dev.euryperez.kotlinquizai.models.Question
import dev.euryperez.kotlinquizai.ui.theme.KotlinQuizAITheme
import dev.euryperez.kotlinquizai.ui.buttons.PrimaryButton

@Composable
fun QuizFinishedDialog(
    gameState: GameStatus.Finished,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss
    ) {
        Scaffold(modifier = modifier) { padding ->
            QuizFinishedDialogContent(
                gameState = gameState,
                modifier = Modifier.padding(padding),
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun QuizFinishedDialogContent(
    gameState: GameStatus.Finished,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scorePercentage = gameState.scorePercentage
    val totalCorrectAnswers = gameState.totalCorrectAnswers
    val responses = gameState.responses

    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(key = scorePercentage) {
                ScoreImage(
                    modifier = Modifier.padding(bottom = 16.dp),
                    scorePercentage = scorePercentage
                )
            }

            item(totalCorrectAnswers, responses) {
                ResultText(
                    modifier = Modifier.padding(bottom = 16.dp),
                    scorePercentage = scorePercentage,
                    totalCorrectAnswers = totalCorrectAnswers,
                    responsesCount = responses.size
                )
            }

            items(responses, key = { it.id }) { question ->
                ResponseItem(question)
            }
        }

        StartOverButtonSection(onDismiss)
    }
}

@Composable
private fun StartOverButtonSection(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider()

        PrimaryButton(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.start_over),
            onClick = { onDismiss() }
        )
    }
}

@Composable
private fun ResultText(
    scorePercentage: Int,
    totalCorrectAnswers: Int,
    responsesCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val title = remember(scorePercentage) {
            when {
                scorePercentage >= 80 -> context.getString(R.string.you_nailed_it)
                scorePercentage >= 60 -> context.getString(R.string.you_are_getting_there)
                else -> context.getString(R.string.not_this_time)
            }
        }

        Text(
            text = "$totalCorrectAnswers/$responsesCount",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ScoreImage(
    scorePercentage: Int,
    modifier: Modifier = Modifier
) {
    val imageDrawable = remember(scorePercentage) {
        when {
            scorePercentage >= 80 -> R.drawable.ic_congratulations
            scorePercentage >= 60 -> R.drawable.ic_work_in_progress
            else -> R.drawable.ic_thinking
        }
    }

    Image(
        modifier = modifier
            .height(180.dp),
        painter = painterResource(imageDrawable),
        contentDescription = null
    )
}

@Composable
private fun ResponseItem(
    question: Question,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .padding(bottom = 16.dp)
    ) {
        val answer = question.options.firstOrNull()

        ResponseHeader(
            question.question,
            isCorrectAnswer = answer?.isCorrectAnswer == true
        )

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {
            val answerText = question.options.firstOrNull()?.text

            if (answerText == null) {
                Text(
                    text = stringResource(R.string.you_did_not_select_an_answer)
                )
            } else {
                Text(text = answerText)
            }
        }
    }
}

@Composable
private fun ResponseHeader(
    question: String,
    isCorrectAnswer: Boolean,
    modifier: Modifier = Modifier
) {
    val icon = if (isCorrectAnswer) R.drawable.ic_check else R.drawable.ic_error

    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceBright)
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = icon),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = question,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun QuizFinishedDialogPreview() {
    KotlinQuizAITheme(dynamicColor = false) {
        QuizFinishedDialog(
            onDismiss = {},
            gameState = GameStatus.Finished(
                responses = listOf(
                    Question(
                        id = "1",
                        question = "What is the capital of France?",
                        options = listOf(
                            Answer("1", "Paris", isCorrectAnswer = true)
                        ),
                        correctAnswerId = "1",
                        explanation = "Paris is the capital of France."
                    ),
                    Question(
                        id = "2",
                        question = "What is the capital of Spain?",
                        options = listOf(
                            Answer("1", "Paris", isCorrectAnswer = true)
                        ),
                        correctAnswerId = "4",
                        explanation = "Madrid is the capital of Spain."
                    ),
                    Question(
                        id = "3",
                        question = "What is the capital of Spain?",
                        options = listOf(),
                        correctAnswerId = "4",
                        explanation = "Madrid is the capital of Spain."
                    ),
                    Question(
                        id = "4",
                        question = "What is the capital of Spain?",
                        options = listOf(),
                        correctAnswerId = "4",
                        explanation = "Madrid is the capital of Spain."
                    ),
                ),
                totalCorrectAnswers = 2,
                scorePercentage = 66
            )
        )
    }
}