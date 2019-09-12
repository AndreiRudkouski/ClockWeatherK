package by.rudkouski.widget.view.setting

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Setting
import by.rudkouski.widget.provider.WidgetProvider.Companion.updateWidget
import by.rudkouski.widget.repository.SettingRepository.updateSettingValueById
import by.rudkouski.widget.view.setting.SettingActivity.Companion.startSettingActivityIntent

class SettingItemView : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun updateSettingItemView(setting: Setting) {
        val view = findViewById<View>(R.id.setting_item)
        val settingNameTextView = view.findViewById<TextView>(R.id.setting_name)
        settingNameTextView.text = setting.getName(context)
        val settingDescriptionTextView = view.findViewById<TextView>(R.id.setting_description)
        settingDescriptionTextView.text = setting.getDescription(context)
        val settingValueCheckBox = view.findViewById<CheckBox>(R.id.setting_value)
        settingValueCheckBox.isChecked = setting.getBooleanValue()
        settingValueCheckBox.setOnCheckedChangeListener(checkedChangeListener)
        settingValueCheckBox.tag = setting.id
    }

    private val checkedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        val settingId = buttonView.tag as Int
        val setting = updateSettingValueById(settingId, if (isChecked) 1 else 0)
        updateWidget(context)
        updateSettingActivity(setting)
    }

    private fun updateSettingActivity(setting: Setting?) {
        val widgetId = setting?.widgetId
        if (widgetId != null && Setting.Code.SETTING_THEME == setting.code) {
            val intent = startSettingActivityIntent(context, widgetId)
            startActivity(context, intent, null)
        }
    }
}