package com.example.forecast.internal

import kotlinx.coroutines.*

fun <T> lazyDeferrd(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>> {
    return lazy {
        GlobalScope.async(start = CoroutineStart.LAZY) {
            block.invoke(this)
        }
    }
}