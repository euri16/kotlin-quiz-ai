package dev.euryperez.kotlinquizai.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}

class DispatcherProviderImpl : DispatcherProvider {
    override val io = Dispatchers.IO

    override val main = Dispatchers.Main

    override val default = Dispatchers.Default
}

/*class TestDispatcherProvider : DispatcherProvider {
    val io
        get() = test.TestCoroutineDispatcher()
    val main
        get() = test.TestCoroutineDispatcher()

    val default
        get() = test.TestCoroutineDispatcher()
}*/