package dev.euryperez.kotlinquizai.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.euryperez.kotlinquizai.BuildConfig
import dev.euryperez.kotlinquizai.data.datasource.quiz.QuizDataSource
import dev.euryperez.kotlinquizai.data.datasource.quiz.QuizVertexAiDataSource
import dev.euryperez.kotlinquizai.utils.DispatcherProvider
import dev.euryperez.kotlinquizai.utils.DispatcherProviderImpl
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindQuizDataSource(quizDataSource: QuizVertexAiDataSource): QuizDataSource

    companion object {
        @Provides
        fun provideDispatcherProvider(): DispatcherProvider = DispatcherProviderImpl()

        @OptIn(ExperimentalSerializationApi::class)
        @Singleton
        @Provides
        fun provideJson() = Json { allowTrailingComma = true }

        @Provides
        fun provideJsonSchema() = Schema.array(
            items = Schema.obj(
                mapOf(
                    "id" to Schema.string(),
                    "question" to Schema.string(),
                    "options" to Schema.array(
                        items = Schema.obj(
                            mapOf(
                                "id" to Schema.enumeration(
                                    listOf("1", "2", "3"),
                                    "Id of the answer"
                                ),
                                "text" to Schema.string()
                            )
                        )
                    ),
                    "correct_answer" to Schema.enumeration(
                        listOf("1", "2", "3"),
                        "Id of the correct answer"
                    ),
                    "explanation" to Schema.string()
                )
            )
        )

        @Provides
        fun provideVertexAiGenerativeModel(
            jsonSchema: Schema
        ): com.google.firebase.vertexai.GenerativeModel {
            return Firebase.vertexAI.generativeModel(
                BuildConfig.vertexAiModel,
                generationConfig = com.google.firebase.vertexai.type.generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = jsonSchema
                },
                systemInstruction = content {
                    text(
                        "You are a kotlin quiz generator, you receive a difficulty level " +
                                "and generate a quiz with a specified number of random questions."
                    )
                }
            )
        }

        @Provides
        fun provideGenerativeModel() = GenerativeModel(
            modelName = BuildConfig.geminiModel,
            apiKey = BuildConfig.geminiApiKey,
            generationConfig = generationConfig {
                temperature = 0f
                topK = 0
                topP = 0.4f
                maxOutputTokens = 8192
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
            ),
            requestOptions = RequestOptions(apiVersion = "v1beta")
        )
    }
}