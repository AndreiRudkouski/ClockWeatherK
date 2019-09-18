package by.rudkouski.widget.view.forecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Forecast

class ForecastAdapter(private val widgetId: Int, private val forecasts: List<Forecast>) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_item, parent, false)
        return ForecastViewHolder(view)
    }

    override fun getItemCount() = forecasts.size

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecasts[position]
        (holder.itemView as ForecastItemView).updateForecastItemView(widgetId, forecast)
    }

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}