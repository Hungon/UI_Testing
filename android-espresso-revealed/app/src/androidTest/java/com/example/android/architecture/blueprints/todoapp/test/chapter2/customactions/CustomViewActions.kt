package com.example.android.architecture.blueprints.todoapp.test.chapter2.customactions

import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFragment
import com.example.android.architecture.blueprints.todoapp.test.chapter11.testdata.TodoItem

import org.hamcrest.Matcher

import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import junit.framework.Assert.assertFalse

object CustomViewActions {

    /**
     * ViewAction which asserts that TO-DO item is not in side the [RecyclerView]
     * adapter.
     * @param taskItem - TO-DO object.
     * @return [ViewAction] view action.
     */
    fun verifyTaskNotInTheList(taskItem: TodoItem): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(RecyclerView::class.java)
            }

            override fun getDescription(): String {
                return ("Expected TO-DO with title "
                        + taskItem.title
                        + " would not present in the list but it was.")
            }

            override fun perform(uiController: UiController, view: View) {

                var isExist = false

                val recyclerView = view as RecyclerView
                val adapter = recyclerView.adapter

                if (adapter is TasksFragment.TasksAdapter) {
                    val amount = adapter.itemCount
                    for (i in 0 until amount) {
                        val taskItemView = recyclerView.layoutManager!!.findViewByPosition(i)
                        val textView = taskItemView!!.findViewById<TextView>(R.id.title)
                        if (textView != null && textView.text != null) {
                            if (textView.text.toString() == taskItem.title) {
                                isExist = true
                            }
                        }
                    }
                }
                assertFalse("Task with title: "
                        + taskItem.title
                        + " is present in the list but it shouldn't", isExist)
            }
        }
    }
}
