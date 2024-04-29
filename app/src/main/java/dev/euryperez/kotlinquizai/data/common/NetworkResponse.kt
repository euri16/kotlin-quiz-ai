package dev.euryperez.kotlinquizai.data.common

sealed interface NetworkResponse<out T> {
    data class Success<out T>(val data: T) : NetworkResponse<T>
    data class Error(val throwable: Throwable) : NetworkResponse<Nothing>
}