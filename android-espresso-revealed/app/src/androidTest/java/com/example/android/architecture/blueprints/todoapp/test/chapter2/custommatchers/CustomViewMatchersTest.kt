package com.example.android.architecture.blueprints.todoapp.test.chapter2.custommatchers

import android.graphics.Color

import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.test.BaseTest

import org.junit.Test

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.hasErrorText
import android.support.test.espresso.matcher.ViewMatchers.withId

class CustomViewMatchersTest : BaseTest() {

    @Test
    fun addsNewToDoError() {
        // adding new TO-DO
        onView(withId(R.id.fab_add_task)).perform(click())
        onView(withId(R.id.fab_edit_task_done)).perform(click())
        onView(withId(R.id.add_task_title))
                .check(matches(hasErrorText("Title cannot be empty!")))
                .check(matches(CustomViewMatchers.withHintColor(Color.RED)))
    }
}
