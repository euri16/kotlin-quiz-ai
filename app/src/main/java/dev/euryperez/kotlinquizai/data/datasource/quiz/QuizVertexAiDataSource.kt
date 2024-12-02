package dev.euryperez.kotlinquizai.data.datasource.quiz

import android.util.Log
import androidx.annotation.IntRange
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.GenerateContentResponse
import dev.euryperez.kotlinquizai.data.common.NetworkResponse
import dev.euryperez.kotlinquizai.data.dto.QuestionDTO
import dev.euryperez.kotlinquizai.data.dto.toModel
import dev.euryperez.kotlinquizai.models.DifficultyLevel
import dev.euryperez.kotlinquizai.models.Quiz
import dev.euryperez.kotlinquizai.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class QuizVertexAiDataSource @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val model: GenerativeModel,
    private val json: Json
) : QuizDataSource {

    override suspend fun getQuizResponse(
        difficultyLevel: DifficultyLevel,
        numberOfQuestions: Int
    ): NetworkResponse<Quiz> {
        return runCatching {
            withContext(dispatcherProvider.default) {
                getGeminiResponse(difficultyLevel, numberOfQuestions)
                    .onSuccess { Log.d("vertex-ai", it.toString()) }
                    .onFailure { Log.e("vertex-ai", it.toString()) }
                    .takeIf { it.isSuccess }
                    ?.getOrNull()
                    ?.text
                    ?.let { json.decodeFromString<List<QuestionDTO>>(it) }
                    ?.map(QuestionDTO::toModel)
                    ?.let(::Quiz)
                    ?.let { NetworkResponse.Success(it) }
                    ?: NetworkResponse.Error(throwable = Throwable("response is null"))
            }
        }.getOrElse {
            NetworkResponse.Error(it)
        }
    }

    /**
     * Generates a list of random single-choice quiz questions at a specified difficulty level.
     *
     * @param difficultyLevel the difficulty level of the questions to generate
     * @param numberOfQuestions the number of questions to generate
     */
    private suspend fun getGeminiResponse(
        difficultyLevel: DifficultyLevel,
        @IntRange(from = 1, to = 10) numberOfQuestions: Int
    ): Result<GenerateContentResponse> {
        return withContext(dispatcherProvider.io) {
            runCatching {
                model.generateContent(buildPrompt(difficultyLevel, numberOfQuestions))
            }
        }
    }

    private fun buildPrompt(difficulty: DifficultyLevel, numberOfQuestions: Int) = """
        Generate a list of $numberOfQuestions random single-choice quiz questions, 
        at a ${difficulty.displayValue} difficulty level, to evaluate the knowledge of Kotlin. 
        Each question should have 3 answer options, only one the options should be correct.
    """.trimIndent()
}
