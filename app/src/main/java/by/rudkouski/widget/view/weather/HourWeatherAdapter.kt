package by.rudkouski.widget.view.weather

import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Weather


class HourWeatherAdapter(private val context: Context, private val widgetId: Int, private val hourWeathers: List<Weather>) : RecyclerView.Adapter<HourWeatherAdapter.HourWeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourWeatherViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.forecast_hour_weather_item, parent, false)
        view.layoutParams.width = getViewWidth()
        return HourWeatherViewHolder(view)
    }

    override fun getItemCount() = hourWeathers.size

    override fun onBindViewHolder(holder: HourWeatherViewHolder, position: Int) {
        val hourWeather = hourWeathers[position]
        (holder.itemView as HourWeatherItemView).updateHourWeatherItemView(widgetId, hourWeather)
    }

    private fun getViewWidth(): Int {
        val service = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()
        service.defaultDisplay.getSize(size)
        return size.x / 6
    }

    class HourWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}