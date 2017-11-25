package com.nhaarman.mockito_kotlin

class VerifyScope<T>(val mock: T) {
	operator inline fun Int.times(call: T.() -> Unit) {
		com.nhaarman.mockito_kotlin.verify(mock, com.nhaarman.mockito_kotlin.times(this)).call()
	}
}