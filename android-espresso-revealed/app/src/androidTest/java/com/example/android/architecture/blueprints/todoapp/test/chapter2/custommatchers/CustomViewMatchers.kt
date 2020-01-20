package com.example.android.architecture.blueprints.todoapp.test.chapter2.custommatchers

import android.support.test.espresso.matcher.BoundedMatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

import com.example.android.architecture.blueprints.todoapp.R

import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * Shows custom [ViewMatchers] samples.
 */
object CustomViewMatchers {

    fun todoWithTitle(expectedTitle: String): Matcher<View> {
        return object : BoundedMatcher<View, LinearLayout>(LinearLayout::class.java) {

            override fun matchesSafely(linearLayout: LinearLayout): Boolean {
                val textView = linearLayout.findViewById<TextView>(R.id.todo_title)
                return expectedTitle == textView.text.toString()
            }

            override fun describeTo(description: Description) {
                description.appendText("with TO-DO title: $expectedTitle")
            }
        }
    }

    /**
     * Matches EditText hint by specific text color.
     * @param expectedColor - expected color code
     * @return [<]
     */
    fun withHintColor(expectedColor: Int): Matcher<View> {
        return object : BoundedMatcher<View, EditText>(EditText::class.java) {

            override fun matchesSafely(editText: EditText): Boolean {
                return expectedColor == editText.currentHintTextColor
            }

            override fun describeTo(description: Description) {
                description.appendText("expected with hint color: $expectedColor")
            }
        }
    }
}
