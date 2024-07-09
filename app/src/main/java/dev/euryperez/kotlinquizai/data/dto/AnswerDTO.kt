package dev.euryperez.kotlinquizai.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnswerDTO(val id: String, val text: String)
