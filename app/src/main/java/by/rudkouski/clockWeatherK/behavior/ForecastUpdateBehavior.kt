package by.rudkouski.clockWeatherK.behavior

import android.animation.ValueAnimator
import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_SHORT
import android.util.AttributeSet
import android.view.View
import android.view.View.SCROLL_AXIS_VERTICAL
import android.widget.TextView
import by.rudkouski.clockWeatherK.R
import by.rudkouski.clockWeatherK.app.App
import by.rudkouski.clockWeatherK.receiver.NetworkChangeChecker
import by.rudkouski.clockWeatherK.receiver.WeatherUpdateBroadcastReceiver

/**
 * The class is used to set behavior for [AppBarLayout] with id "appBar_forecast" from "..\res\layout\forecast_activity.xml".
 */
class ForecastUpdateBehavior(context: Context, attrs: AttributeSet) : AppBarLayout.Behavior(context, attrs) {

    private var isFirstTouch = true
    private var startY: Float = 0.toFloat()

    companion object {
        private const val ANIMATION_DURATION = 100
        private const val EXTRA_Y = 200
        private const val UPDATE_Y = 700
        private const val MAX_Y = UPDATE_Y + EXTRA_Y
        private const val PADDING_SCALE = 5
    }

    override fun onStartNestedScroll(parent: CoordinatorLayout, child: AppBarLayout, directTargetChild: View,
                                     target: View, nestedScrollAxes: Int, type: Int): Boolean =
        nestedScrollAxes == SCROLL_AXIS_VERTICAL

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View,
                                dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {

        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        if (child.height == child.bottom) {
            target.setOnTouchListener { _, event ->
                if (isFirstTouch) {
                    startY = event.y
                    isFirstTouch = false
                }
                val deltaY = event.y - startY
                changeSettingBeforeUpdate(child, target, deltaY)
                target.performClick()
            }
        }
    }

    private fun changeSettingBeforeUpdate(child: AppBarLayout, target: View, valueY: Float) {
        if (valueY > 0) {
            if (valueY <= MAX_Y) {
                target.setPadding(0, valueY.toInt() / PADDING_SCALE, 0, 0)
            }
            if (valueY <= UPDATE_Y) {
                changeTextViewSetting(child, valueY)
            }
        }
    }

    private fun changeTextViewSetting(child: AppBarLayout, valueY: Float) {
        val createDate = child.findViewById<TextView>(R.id.create_current_weather)
        createDate.scaleY = 1.0f - valueY / UPDATE_Y
        createDate.alpha = 1.0f - valueY / UPDATE_Y
        val updateDate = child.findViewById<TextView>(R.id.update_current_weather)
        updateDate.scaleY = valueY / UPDATE_Y
        updateDate.alpha = valueY / UPDATE_Y
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)
        if (target.paddingTop >= UPDATE_Y / PADDING_SCALE) {
            WeatherUpdateBroadcastReceiver.updateWeatherPendingIntent(App.appContext).send()
            if (NetworkChangeChecker.isOnline()) {
                Snackbar.make(target, App.appContext.getString(R.string.update), LENGTH_SHORT).show()
            }
        }

        val animation = ValueAnimator.ofInt(target.paddingTop, 0)
        animation.duration = ANIMATION_DURATION.toLong()
        animation.addUpdateListener {
            changeSettingAfterUpdate(abl, target, java.lang.Float.parseFloat(animation.animatedValue.toString()))
        }
        animation.start()
        isFirstTouch = true
    }

    private fun changeSettingAfterUpdate(child: AppBarLayout, target: View, valueY: Float) {
        target.setPadding(0, valueY.toInt(), 0, 0)
        changeTextViewSetting(child, valueY)
    }
}