package com.example.android.architecture.blueprints.todoapp.test.chapter2.customactions

import android.support.test.espresso.PerformException
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.CoordinatesProvider
import android.support.test.espresso.action.GeneralLocation
import android.support.test.espresso.action.PrecisionDescriber
import android.support.test.espresso.action.Press
import android.support.test.espresso.action.Tap
import android.support.test.espresso.action.Tapper
import android.support.test.espresso.core.internal.deps.guava.base.Optional
import android.support.test.espresso.util.HumanReadables
import android.view.View
import android.view.ViewConfiguration
import android.webkit.WebView

import org.hamcrest.Matcher

import android.support.test.espresso.core.internal.deps.guava.base.Preconditions.checkNotNull
import android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import org.hamcrest.CoreMatchers.allOf

/**
 * Class that holds a copy of Espresso ViewActions.click() and allows to perform
 * clicks on a view with different visibility.
 */
class CustomClickAction @JvmOverloads constructor(private val tapper: Tapper, private val coordinatesProvider: CoordinatesProvider,
                                                  private val precisionDescriber: PrecisionDescriber, rollbackAction: ViewAction? = null) : ViewAction {
    private val rollbackAction: Optional<ViewAction>

    init {
        this.rollbackAction = Optional.fromNullable(rollbackAction)
    }

    override fun getConstraints(): Matcher<View> {
        val standardConstraint = isDisplayingAtLeast(visibility)
        return if (rollbackAction.isPresent) {
            allOf(standardConstraint, rollbackAction.get().constraints)
        } else {
            standardConstraint
        }
    }

    override fun perform(uiController: UiController, view: View) {
        val coordinates = coordinatesProvider.calculateCoordinates(view)
        val precision = precisionDescriber.describePrecision()

        var status: Tapper.Status = Tapper.Status.FAILURE
        var loopCount = 0
        // Native event injection is quite a tricky process. A tap is actually 2
        // seperate motion events which need to get injected into the system. Injection
        // makes an RPC call from our app under test to the Android system server, the
        // system server decides which window layer to deliver the event to, the system
        // server makes an RPC to that window layer, that window layer delivers the event
        // to the correct UI element, activity, or window object. Now we need to repeat
        // that 2x. for a simple down and up. Oh and the down event triggers timers to
        // detect whether or not the event is a long vs. short press. The timers are
        // removed the moment the up event is received (NOTE: the possibility of eventTime
        // being in the future is totally ignored by most motion event processors).
        //
        // Phew.
        //
        // The net result of this is sometimes we'll want to do a regular tap, and for
        // whatever reason the up event (last half) of the tap is delivered after long
        // press timeout (depending on system load) and the long press behaviour is
        // displayed (EG: show a context menu). There is no way to avoid or handle this more
        // gracefully. Also the longpress behavour is app/widget specific. So if you have
        // a seperate long press behaviour from your short press, you can pass in a
        // 'RollBack' ViewAction which when executed will undo the effects of long press.

        while (status != Tapper.Status.SUCCESS && loopCount < 3) {
            try {
                status = tapper.sendTap(uiController, coordinates, precision)
            } catch (re: RuntimeException) {
                throw PerformException.Builder()
                        .withActionDescription(this.description)
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(re)
                        .build()
            }

            // ensures that all work enqueued to process the tap has been run.
            uiController.loopMainThreadForAtLeast(ViewConfiguration.getPressedStateDuration().toLong())
            if (status == Tapper.Status.WARNING) {
                if (rollbackAction.isPresent) {
                    rollbackAction.get().perform(uiController, view)
                } else {
                    break
                }
            }
            loopCount++
        }
        if (status == Tapper.Status.FAILURE) {
            throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(RuntimeException(String.format("Couldn't "
                            + "click at: %s,%s precision: %s, %s . Tapper: %s coordinate provider: %s precision " +
                            "describer: %s. Tried %s times. With Rollback? %s", coordinates[0], coordinates[1],
                            precision[0], precision[1], tapper, coordinatesProvider, precisionDescriber, loopCount,
                            rollbackAction.isPresent)))
                    .build()
        }

        if (tapper === Tap.SINGLE && view is WebView) {
            // WebViews will not process click events until double tap
            // timeout. Not the best place for this - but good for now.
            uiController.loopMainThreadForAtLeast(ViewConfiguration.getDoubleTapTimeout().toLong())
        }
    }

    override fun getDescription(): String {
        return tapper.toString().toLowerCase() + " click"
    }

    companion object {
        private var visibility = 90

        fun clickElementWithVisibility(viewVisibility: Int): ViewAction {
            checkNotNull(viewVisibility)
            if (viewVisibility > 0 && viewVisibility <= 100) {
                visibility = viewVisibility
            }
            return CustomClickAction(Tap.SINGLE, GeneralLocation.TOP_CENTER, Press.FINGER)
        }
    }
}
