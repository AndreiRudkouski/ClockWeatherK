package by.rudkouski.widget.view.forecast

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Forecast
import by.rudkouski.widget.repository.ForecastRepository.getForecastsByLocationId
import by.rudkouski.widget.repository.WidgetRepository.getWidgetById
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.OffsetDateTime.now
import java.util.*

class ForecastFragment @SuppressLint("ValidFragment")
private constructor() : Fragment() {

    private val forecasts = ArrayList<Forecast>()

    companion object {
        fun newForecastFragmentInstance(widgetId: Int): ForecastFragment {
            val fragment = ForecastFragment()
            val bundle = Bundle()
            bundle.putInt(EXTRA_APPWIDGET_ID, widgetId)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.forecast_fragment, container, false)
        setDataToForecastRecyclerView(view)
        return view
    }

    private fun setDataToForecastRecyclerView(view: View) {
        if (context != null && arguments != null) {
            val widgetId = arguments!!.getInt(EXTRA_APPWIDGET_ID)
            val forecastRecycler = view.findViewById<RecyclerView>(R.id.forecast_recycler_view)
            forecastRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            val adapter = ForecastAdapter(widgetId, forecasts)
            forecastRecycler.adapter = adapter
            val widget = getWidgetById(widgetId)
            if (widget != null) {
                if (forecasts.isNotEmpty()) forecasts.clear()
                forecasts.addAll(checkWeatherDates(getForecastsByLocationId(widget.locationId)))
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun checkWeatherDates(forecasts: List<Forecast>?): List<Forecast> {
        val correctForecasts = ArrayList<Forecast>()
        if (forecasts != null) {
            for (forecast in forecasts) {
                if (isWeatherDateCorrect(forecast.date)) {
                    correctForecasts.add(forecast)
                }
            }
        }
        return correctForecasts
    }

    private fun isWeatherDateCorrect(date: OffsetDateTime): Boolean {
        val currentDate = now(date.offset)
        return (date.year == currentDate.year && date.dayOfYear >= currentDate.dayOfYear) || date.year > currentDate.year
    }
}