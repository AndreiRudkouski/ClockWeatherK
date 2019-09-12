package by.rudkouski.widget.view.setting

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import by.rudkouski.widget.R
import by.rudkouski.widget.repository.SettingRepository.getSettingsByWidgetId
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
        val settings = getSettingsByWidgetId(widgetId)
        if (settings != null) {
            setContentView(R.layout.setting_activity)
            val toolbar: Toolbar = findViewById(R.id.toolbar_setting)
            setSupportActionBar(toolbar)
            val settingsView: ListView = findViewById(R.id.settings)
            val adapter = SettingsViewAdapter(this, settings)
            settingsView.adapter = adapter
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}