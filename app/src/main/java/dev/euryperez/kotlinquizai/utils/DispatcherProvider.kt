package dev.euryperez.kotlinquizai.utils

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}

class DispatcherProviderImpl : DispatcherProvider {
    override val io
        get() = kotlinx.coroutines.Dispatchers.IO

    override val main
        get() = kotlinx.coroutines.Dispatchers.Main

    override val default
        get() = kotlinx.coroutines.Dispatchers.Default
}

/*class TestDispatcherProvider : DispatcherProvider {
    val io
        get() = kotlinx.coroutines.test.TestCoroutineDispatcher()
    val main
        get() = kotlinx.coroutines.test.TestCoroutineDispatcher()

    val default
        get() = kotlinx.coroutines.test.TestCoroutineDispatcher()
}*/