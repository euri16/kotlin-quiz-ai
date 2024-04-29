package dev.euryperez.kotlinquizai.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.euryperez.kotlinquizai.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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