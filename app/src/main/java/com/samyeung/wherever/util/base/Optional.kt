package com.samyeung.wherever.util.base

import android.support.annotation.Nullable

class Optional<M>(@param:Nullable private val optional: M?) {

    val isEmpty: Boolean
        get() = this.optional == null

    fun get(): M {
        if (optional == null) {
            throw NoSuchElementException("No value present")
        }
        return optional
    }
}