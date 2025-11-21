package test

import junit.framework.AssertionFailedError

/** Inspired by JUnit5, asserts an exception being thrown */
inline fun <reified E: Throwable> assertThrows(block: () -> Unit): E {
    return try {
        block.invoke()
        throw AssertionFailedError("No exception of type ${(E::class).simpleName} was thrown")
    } catch (e: Throwable) {
        e as? E ?: throw e
    }
}
