package by.rudkouski.widget.view.setting

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.content.ContextCompat.startActivity
import by.rudkouski.widget.R
import by.rudkouski.widget.app.App.Companion.isLocationExact
import by.rudkouski.widget.app.App.Companion.locationUpdateInMinutes
import by.rudkouski.widget.app.App.Companion.weatherUpdateInMinutes
import by.rudkouski.widget.entity.Setting
import by.rudkouski.widget.provider.WidgetProvider.Companion.updateWidget
import by.rudkouski.widget.repository.SettingRepository.updateSettingValueById
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler.startLocationUpdateScheduler
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler.startWeatherUpdateScheduler
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler.stopLocationUpdateScheduler
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler.stopWeatherUpdateScheduler
import by.rudkouski.widget.view.setting.SettingActivity.Companion.startSettingActivityIntent


class SettingItemView : LinearLayout {

    private val defaultIntervals = arrayOf(15, 30, 45)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun updateSettingItemView(setting: Setting) {
        val view = findViewById<View>(R.id.setting_item)
        val settingNameTextView = view.findViewById<TextView>(R.id.setting_name)
        settingNameTextView.text = setting.getName(context)
        val settingDescriptionTextView = view.findViewById<TextView>(R.id.setting_description)
        settingDescriptionTextView.text = setting.getDescription(context)
        val settingValueSpinner = view.findViewById<Spinner>(R.id.setting_spinner_value)
        val settingValueCheckBox = view.findViewById<CheckBox>(R.id.setting_checkbox_value)
        if (Setting.Type.BOOLEAN == setting.type) {
            settingValueSpinner.visibility = GONE
            setSettingValueCheckBoxData(settingValueCheckBox, setting)
        } else {
            settingValueCheckBox.visibility = GONE
            setSettingValueSpinner(settingValueSpinner, setting)
        }
    }

    private fun setSettingValueCheckBoxData(checkBox: CheckBox, setting: Setting) {
        checkBox.isChecked = setting.getBooleanValue()
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            val updatedSetting = updateSettingValueById(setting.id, if (isChecked) 1 else 0)
            if (updatedSetting != null) {
                if (Setting.Code.SETTING_EXACT_LOCATION == setting.code) {
                    isLocationExact = updatedSetting.getBooleanValue()
                } else {
                    updateWidget(context)
                    updateSettingActivity(updatedSetting)
                }
            }
        }
    }

    private fun setSettingValueSpinner(spinner: Spinner, setting: Setting) {
        val interval = getIntervals(setting.code)
        val adapter =
            ArrayAdapter<String>(context, R.layout.spinner_item, interval.map { it.toString() + " " + context.getString(R.string.minute) })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        var selectedPosition = interval.binarySearch(setting.value)
        spinner.setSelection(selectedPosition)
        spinner.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                if (selectedPosition != position) {
                    updateScheduler(updateSettingValueById(setting.id, interval[position]), setting.code)
                    selectedPosition = position
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
    }

    private fun getIntervals(code: Setting.Code): List<Int> {
        return if (Setting.Code.SETTING_WEATHER == code) {
            defaultIntervals.map {
                it.times(2)
            }
        } else {
            defaultIntervals.toList()
        }
    }

    private fun updateSettingActivity(setting: Setting) {
        val widgetId = setting.widgetId
        if (widgetId != null && Setting.Code.SETTING_THEME == setting.code) {
            val intent = startSettingActivityIntent(context, widgetId)
            startActivity(context, intent, null)
        }
    }

    private fun updateScheduler(setting: Setting?, code: Setting.Code) {
        if (setting != null) {
            if (Setting.Code.SETTING_WEATHER == code) {
                weatherUpdateInMinutes = setting.value.toLong()
                stopWeatherUpdateScheduler()
                startWeatherUpdateScheduler()
            } else {
                locationUpdateInMinutes = setting.value.toLong()
                stopLocationUpdateScheduler()
                startLocationUpdateScheduler()
            }
        }
    }
}