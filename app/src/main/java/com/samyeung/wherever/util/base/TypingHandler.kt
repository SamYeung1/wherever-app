package com.samyeung.wherever.util.base

import android.os.Handler

class TypingHandler(private val doSomething: (TypingHandler, String) -> Unit) {
    private var delay: Long = 1000 // 1 seconds after user stops typing
    private var lastTextEdit: Long = 0
    private var handler = Handler()
    var value: String = ""

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            this.doSomething(this, value)
        }
    }

    fun start(value: String) {
        this.value = value
        this.lastTextEdit = System.currentTimeMillis()
        this.handler.postDelayed(this.inputFinishChecker, delay)
    }

    fun stop() {
        this.handler.removeCallbacks(this.inputFinishChecker)
    }

}