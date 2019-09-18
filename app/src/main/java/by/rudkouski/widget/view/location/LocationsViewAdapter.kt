package by.rudkouski.widget.view.location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Location

class LocationsViewAdapter(private val context: Context,
                           private val locationItemClickListener: OnLocationItemClickListener,
                           private val locations: List<Location>, private val selectedLocationId: Int) : BaseAdapter(), View.OnClickListener {

    override fun onClick(view: View) {
        val locationId = getItemId(view.tag as Int).toInt()
        locationItemClickListener.onLocationItemClick(view, locationId)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val tempConvertView = convertView ?: LayoutInflater.from(context).inflate(R.layout.location_item, parent, false)
        val view = tempConvertView as LocationItemView
        view.updateLocationItemView(getItem(position), getItemId(position) == selectedLocationId.toLong())
        view.setOnClickListener(this)
        view.tag = position
        return view
    }

    override fun getItem(position: Int) = locations[position]

    override fun getItemId(position: Int) = getItem(position).id.toLong()

    override fun getCount() = locations.size

    interface OnLocationItemClickListener {
        fun onLocationItemClick(view: View, locationId: Int)
    }
}