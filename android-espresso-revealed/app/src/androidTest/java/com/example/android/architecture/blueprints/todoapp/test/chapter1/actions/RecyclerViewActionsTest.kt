package com.example.android.architecture.blueprints.todoapp.test.chapter1.actions

import android.support.test.espresso.Espresso
import android.support.test.espresso.ViewInteraction
import android.support.v7.widget.RecyclerView

import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.test.BaseTest
import com.example.android.architecture.blueprints.todoapp.test.chapter2.customactions.CustomRecyclerViewActions

import org.junit.Test

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.example.android.architecture.blueprints.todoapp.test.chapter2.customactions.CustomClickAction.clickElementWithVisibility
import com.example.android.architecture.blueprints.todoapp.test.chapter2.customactions.CustomRecyclerViewActions.ClickTodoCheckBoxWithTitleViewAction.clickTodoCheckBoxWithTitle
import com.example.android.architecture.blueprints.todoapp.test.chapter4.conditionwatchers.ConditionWatchers.waitForElement
import com.example.android.architecture.blueprints.todoapp.test.chapter4.conditionwatchers.ConditionWatchers.waitForElementIsGone

/**
 * Demonstrates [RecyclerView] actions usage.
 */
class RecyclerViewActionsTest : BaseTest() {

    private val todoSavedSnackbar = onView(withText(R.string.successfully_saved_task_message))

    @Test
    @Throws(Exception::class)
    fun addNewToDos() {
        generateToDos(12)
        onView(withId(R.id.tasks_list))
                .perform(actionOnItemAtPosition<ViewHolder>(10, scrollTo()))
        onView(withId(R.id.tasks_list))
                .perform(scrollToPosition<ViewHolder>(1))
        onView(withId(R.id.tasks_list))
                .perform(scrollToPosition<ViewHolder>(11))
        onView(withId(R.id.tasks_list))
                .perform(actionOnItemAtPosition<ViewHolder>(11, click()))
        Espresso.pressBack()
        onView(withId(R.id.tasks_list))
                .perform(scrollToPosition<ViewHolder>(2))
    }

    @Test
    @Throws(Exception::class)
    fun addNewToDosChained() {
        generateToDos(12)
        onView(withId(R.id.tasks_list))
                .perform(actionOnItemAtPosition<ViewHolder>(10, scrollTo()))
                .perform(scrollToPosition<ViewHolder>(1))
                .perform(scrollToPosition<ViewHolder>(11))
                .perform(actionOnItemAtPosition<ViewHolder>(11, click()))
        Espresso.pressBack()
        onView(withId(R.id.tasks_list)).perform(scrollToPosition<ViewHolder>(2))
    }

    @Test
    @Throws(Exception::class)
    fun completeToDo() {
        generateToDos(10)
        onView(withId(R.id.tasks_list)).perform(clickTodoCheckBoxWithTitle("item 2"))
        onView(withId(R.id.tasks_list))
                .perform(CustomRecyclerViewActions.ScrollToLastHolder.scrollToLastHolder())
    }

    /**
     * Helper function that adds needed TO-DOs amount.
     * @param count - amount of TO-DOs to add.
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun generateToDos(count: Int) {
        for (i in 1..count) {
            waitForElementIsGone(todoSavedSnackbar, 3000)
            // Adding new TO-DO.
            onView(withId(R.id.fab_add_task)).perform(clickElementWithVisibility(20))
            onView(withId(R.id.add_task_title))
                    .perform(typeText("item $i"), closeSoftKeyboard())
            onView(withId(R.id.fab_edit_task_done)).perform(click())
            waitForElement(todoSavedSnackbar, 3000)
        }
        waitForElementIsGone(todoSavedSnackbar, 3000)
    }
}
