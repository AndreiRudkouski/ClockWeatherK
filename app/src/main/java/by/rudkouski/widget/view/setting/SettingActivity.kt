package by.rudkouski.widget.view.setting

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Location
import by.rudkouski.widget.entity.Setting
import by.rudkouski.widget.repository.SettingRepository.getAllSettingsByWidgetId
import by.rudkouski.widget.repository.WidgetRepository
import by.rudkouski.widget.view.BaseActivity

class SettingActivity : BaseActivity() {

    companion object {

        fun startSettingActivityIntent(context: Context, widgetId: Int): Intent {
            val intent = Intent(context, SettingActivity::class.java)
            intent.putExtra(EXTRA_APPWIDGET_ID, widgetId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = getAllSettingsByWidgetId(widgetId)
        if (settings != null) {
            setContentView(R.layout.setting_activity)
            val toolbar: Toolbar = findViewById(R.id.toolbar_setting)
            setSupportActionBar(toolbar)
            val settingsView: ListView = findViewById(R.id.settings)
            val adapter = SettingsViewAdapter(this,  correctSettings(settings))
            settingsView.adapter = adapter
        }
    }

    private fun correctSettings(settings: List<Setting>): List<Setting> {
        val widget = WidgetRepository.getWidgetById(widgetId)
        if (Location.CURRENT_LOCATION_ID != widget!!.locationId) {
            return settings.filter { !it.code.name.contains("location", true) }
        }
        return settings.filter { !it.code.name.contains("weather", true) }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}