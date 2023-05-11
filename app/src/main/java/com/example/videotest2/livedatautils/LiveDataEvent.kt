package com.example.videotest2.livedatautils

/**
 * https://medium.com/proandroiddev/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
 */
open class LiveDataEvent<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}