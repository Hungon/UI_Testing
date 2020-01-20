package com.example.android.architecture.blueprints.todoapp.test.chapter2.custommatchers

import android.support.test.espresso.intent.Checks
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import android.widget.TextView

import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFragment
import com.example.android.architecture.blueprints.todoapp.test.chapter11.testdata.TodoItem

import org.hamcrest.Description
import org.hamcrest.Matcher

import com.example.android.architecture.blueprints.todoapp.tasks.TasksFragment.TasksAdapter

object RecyclerViewMatchers {

    fun withTitle(taskTitle: String): Matcher<RecyclerView.ViewHolder> {
        Checks.checkNotNull(taskTitle)

        return object : BoundedMatcher<RecyclerView.ViewHolder, TasksFragment.TasksAdapter.ViewHolder>(
                TasksAdapter.ViewHolder::class.java) {
            override fun matchesSafely(holder: TasksAdapter.ViewHolder): Boolean {
                val holderTaskTitle = holder.holderTask.title
                return taskTitle == holderTaskTitle
            }

            override fun describeTo(description: Description) {
                description.appendText("with task title: $taskTitle")
            }
        }
    }

    fun withTask(taskItem: TodoItem): Matcher<RecyclerView.ViewHolder> {
        Checks.checkNotNull(taskItem)

        return object : BoundedMatcher<RecyclerView.ViewHolder, TasksFragment.TasksAdapter.ViewHolder>(
                TasksAdapter.ViewHolder::class.java) {
            override fun matchesSafely(holder: TasksAdapter.ViewHolder): Boolean {
                val holderTaskTitle = holder.holderTask.title
                val holderTaskDesc = holder.holderTask.description
                return taskItem.title == holderTaskTitle && taskItem.description == holderTaskDesc
            }

            override fun describeTo(description: Description) {
                description.appendText("task with title: " + taskItem.title
                        + " and description: " + taskItem.description)
            }
        }
    }

    fun withTaskTitleFromTextView(taskTitle: String): Matcher<RecyclerView.ViewHolder> {
        Checks.checkNotNull(taskTitle)

        return object : BoundedMatcher<RecyclerView.ViewHolder, TasksFragment.TasksAdapter.ViewHolder>(
                TasksAdapter.ViewHolder::class.java) {
            override fun matchesSafely(holder: TasksAdapter.ViewHolder): Boolean {
                val titleTextView = holder.itemView.findViewById<View>(R.id.title) as TextView
                return taskTitle == titleTextView.text.toString()
            }

            override fun describeTo(description: Description) {
                description.appendText("with task title: $taskTitle")
            }
        }
    }
}
