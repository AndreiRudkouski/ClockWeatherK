package by.rudkouski.widget.view.setting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Setting

class SettingsViewAdapter(private val context: Context, private val settings: List<Setting>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val tempConvertView = convertView ?: LayoutInflater.from(context).inflate(R.layout.setting_item, parent, false)
        val view = tempConvertView as SettingItemView
        view.updateSettingItemView(getItem(position))
        return view
    }

    override fun getItem(position: Int) = settings[position]

    override fun getItemId(position: Int) = getItem(position).id.toLong()

    override fun getCount() = settings.size
}