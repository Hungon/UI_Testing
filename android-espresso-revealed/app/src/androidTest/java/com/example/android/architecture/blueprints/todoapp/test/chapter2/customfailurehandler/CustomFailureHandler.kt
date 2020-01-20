package com.example.android.architecture.blueprints.todoapp.test.chapter2.customfailurehandler

import android.content.Context
import android.support.test.espresso.FailureHandler
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.base.DefaultFailureHandler
import android.view.View

import org.hamcrest.Matcher

/**
 * CustomFailureHandler sample.
 */
class CustomFailureHandler(targetContext: Context) : FailureHandler {

    private val delegate: FailureHandler

    init {
        delegate = DefaultFailureHandler(targetContext)
    }

    override fun handle(error: Throwable, viewMatcher: Matcher<View>) {
        try {
            delegate.handle(error, viewMatcher)
        } catch (e: NoMatchingViewException) {
            // For example done device dump, take screenshot, etc.
            throw e
        }

    }
}
