package by.rudkouski.widget.view.weather

import android.content.Context
import android.graphics.Point
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import by.rudkouski.widget.app.App
import by.rudkouski.widget.entity.Weather


class HourWeatherAdapter(private val hourWeathers: List<Weather>) :
    RecyclerView.Adapter<HourWeatherAdapter.HourWeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourWeatherViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(by.rudkouski.widget.R.layout.hour_weather_item, parent, false)
        view.layoutParams.width = getViewWidth()
        return HourWeatherAdapter.HourWeatherViewHolder(view)
    }

    override fun getItemCount() = hourWeathers.size

    override fun onBindViewHolder(holder: HourWeatherViewHolder, position: Int) {
        val hourWeather = hourWeathers[position]
        (holder.itemView as HourWeatherItemView).updateHourWeatherItemView(hourWeather)
    }

    private fun getViewWidth(): Int {
        val service = App.appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()
        service.defaultDisplay.getSize(size)
        return size.x / 6
    }

    class HourWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}