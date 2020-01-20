package com.example.android.architecture.blueprints.todoapp.test.chapter1.matchers

import android.support.design.widget.FloatingActionButton
import android.support.test.runner.AndroidJUnit4
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox

import com.example.android.architecture.blueprints.todoapp.R

import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.LayoutMatchers.hasEllipsizedText
import android.support.test.espresso.matcher.LayoutMatchers.hasMultilineText
import android.support.test.espresso.matcher.PreferenceMatchers.withKey
import android.support.test.espresso.matcher.PreferenceMatchers.withSummaryText
import android.support.test.espresso.matcher.PreferenceMatchers.withTitle
import android.support.test.espresso.matcher.RootMatchers.isDialog
import android.support.test.espresso.matcher.RootMatchers.isPlatformPopup
import android.support.test.espresso.matcher.RootMatchers.isTouchable
import android.support.test.espresso.matcher.ViewMatchers.hasContentDescription
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.hasImeAction
import android.support.test.espresso.matcher.ViewMatchers.hasSibling
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isChecked
import android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.isEnabled
import android.support.test.espresso.matcher.ViewMatchers.isFocusable
import android.support.test.espresso.matcher.ViewMatchers.isSelected
import android.support.test.espresso.matcher.ViewMatchers.supportsInputMethods
import android.support.test.espresso.matcher.ViewMatchers.withChild
import android.support.test.espresso.matcher.ViewMatchers.withClassName
import android.support.test.espresso.matcher.ViewMatchers.withContentDescription
import android.support.test.espresso.matcher.ViewMatchers.withHint
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withParent
import android.support.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not

/**
 * Lists all ViewMatchers. ViewMatchers here are without functional load.
 * This is done for demonstration purposes.
 */
@RunWith(AndroidJUnit4::class)
class ViewMatchersTest {

    @Test
    fun userProperties() {
        onView(withId(R.id.fab_add_task))
        onView(withText("All TO-DOs"))
        onView(withContentDescription(R.string.menu_filter))
        onView(hasContentDescription())
        onView(withHint(R.string.name_hint))
    }

    @Test
    fun uiProperties() {
        onView(isDisplayed())
        onView(isEnabled())
        onView(isChecked())
        onView(isSelected())
    }

    @Test
    fun objectMatcher() {
        onView(not<View>(isChecked()))
        onView(allOf<View>(withText("item 1"), isChecked()))
    }

    @Test
    fun hierarchy() {
        onView(withParent(withId(R.id.todo_item)))
        onView(withChild(withText("item 2")))
        onView(isDescendantOfA(withId(R.id.todo_item)))
        onView(hasDescendant(isChecked()))
                .check(matches(isDisplayed()))
                .check(matches(isFocusable()))
        onView(hasSibling(withContentDescription(R.string.menu_filter)))
    }

    @Test
    fun input() {
        onView(supportsInputMethods())
        onView(hasImeAction(EditorInfo.IME_ACTION_SEND))
    }

    @Test
    fun classMatchers() {
        onView(isAssignableFrom(CheckBox::class.java))
        onView(withClassName(`is`(FloatingActionButton::class.java.canonicalName)))
    }

    @Test
    fun rootMatchers() {
        onView(isFocusable())
        onView(withText(R.string.name_hint)).inRoot(isTouchable())
        onView(withText(R.string.name_hint)).inRoot(isDialog())
        onView(withText(R.string.name_hint)).inRoot(isPlatformPopup())
    }

    @Test
    fun preferenceMatchers() {
        onData(withSummaryText("3 days"))
        onData(withTitle(R.string.pref_title_send_notifications))
        onData(withKey("example_switch"))
        onView(isEnabled())
    }

    @Test
    fun layoutMatchers() {
        onView(hasEllipsizedText())
        onView(hasMultilineText())
    }
}
