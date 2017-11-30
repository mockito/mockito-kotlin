package com.nhaarman.mockitokotlin2

class VerifyScope<T>(val mock: T) {
    operator inline fun Int.times(call: T.() -> Unit) {
        verify(mock, com.nhaarman.mockitokotlin2.times(this)).call()
    }
}