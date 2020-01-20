package com.example.android.architecture.blueprints.todoapp.test.chapter2.customactions

import android.support.test.espresso.PerformException
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.util.HumanReadables
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewConfiguration
import android.widget.CheckBox
import android.widget.TextView

import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFragment

import org.hamcrest.Matcher

import android.support.test.espresso.action.ViewActions.actionWithAssertions
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matchers.allOf

/**
 * Demonstrates custom [RecyclerView] actions implementation.
 */
interface CustomRecyclerViewActions : ViewAction {

    /**
     * RecyclerView action that performs click on TO-DO checkbox based on its title.
     */
    class ClickTodoCheckBoxWithTitleViewAction(private val toDoTitle: String) : CustomRecyclerViewActions {

        override fun getConstraints(): Matcher<View> {
            return allOf(isAssignableFrom(RecyclerView::class.java), isDisplayed())
        }

        override fun getDescription(): String {
            return "Complete ToDo with title: \"$toDoTitle\" by clicking its checkbox."
        }

        override fun perform(uiController: UiController, view: View) {
            try {
                val recyclerView = view as RecyclerView
                val adapter = recyclerView.adapter
                if (adapter is TasksFragment.TasksAdapter) {
                    for (i in 0 until adapter.itemCount) {
                        val taskItemView = recyclerView.layoutManager!!.findViewByPosition(i)
                        if (taskItemView != null) {
                            val textView = taskItemView.findViewById<TextView>(R.id.todo_title)
                            if (textView != null && textView.text != null) {
                                if (textView.text.toString() == toDoTitle) {
                                    val completeCheckBox = taskItemView.findViewById<CheckBox>(R.id.todo_complete)
                                    completeCheckBox.performClick()
                                }
                            } else {
                                throw RuntimeException(
                                        "Unable to find TO-DO item with title \"$toDoTitle\"")
                            }
                        }
                    }
                }
                uiController.loopMainThreadForAtLeast(ViewConfiguration.getTapTimeout().toLong())
            } catch (e: RuntimeException) {
                throw PerformException.Builder().withActionDescription(this.description)
                        .withViewDescription(HumanReadables.describe(view)).withCause(e).build()
            }

        }

        companion object {

            fun clickTodoCheckBoxWithTitle(toDoTitle: String): ViewAction {
                return actionWithAssertions(ClickTodoCheckBoxWithTitleViewAction(toDoTitle))
            }
        }
    }

    /**
     * ViewAction that scrolls to the last item in RecyclerView.
     */
    class ScrollToLastHolder : CustomRecyclerViewActions {

        override fun getConstraints(): Matcher<View> {
            return allOf(isAssignableFrom(RecyclerView::class.java), isDisplayed())
        }

        override fun perform(uiController: UiController, view: View) {
            val recyclerView = view as RecyclerView
            val itemCount = recyclerView.adapter!!.itemCount
            try {
                recyclerView.scrollToPosition(itemCount - 1)
                uiController.loopMainThreadUntilIdle()
            } catch (e: RuntimeException) {
                throw PerformException.Builder().withActionDescription(this.description)
                        .withViewDescription(HumanReadables.describe(view)).withCause(e).build()
            }

        }

        override fun getDescription(): String {
            return "scroll to last holder in RecyclerView"
        }

        companion object {

            fun scrollToLastHolder(): ViewAction {
                return actionWithAssertions(ScrollToLastHolder())
            }
        }
    }
}
