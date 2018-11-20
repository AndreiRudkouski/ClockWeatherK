package by.rudkouski.clockWeatherK.view.forecast

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.rudkouski.clockWeatherK.R
import by.rudkouski.clockWeatherK.database.DBHelper.Companion.INSTANCE
import by.rudkouski.clockWeatherK.entity.Forecast
import java.util.*
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.YEAR

class ForecastFragment @SuppressLint("ValidFragment")
private constructor() : Fragment() {

    private val dbHelper = INSTANCE

    companion object {
        fun newInstance(widgetId: Int): ForecastFragment {
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
        val forecastRecycler = view.findViewById<RecyclerView>(R.id.forecast_recycler_view)
        forecastRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        if (context != null && arguments != null) {
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                val widgetId = arguments!!.getInt(EXTRA_APPWIDGET_ID)
                val widget = dbHelper.getWidgetById(widgetId)
                val forecasts = checkForecastDates(dbHelper.getForecastsByLocationId(widget.location.id))
                val adapter = ForecastAdapter(forecasts)
                forecastRecycler.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun checkForecastDates(forecasts: List<Forecast>): List<Forecast> {
        val correctForecasts = ArrayList<Forecast>()
        for (forecast in forecasts) {
            if (isForecastDateCorrect(forecast.date)) {
                correctForecasts.add(forecast)
            }
        }
        return correctForecasts
    }

    private fun isForecastDateCorrect(date: Date): Boolean {
        val forecastDate = Calendar.getInstance()
        forecastDate.time = date
        val currentDate = Calendar.getInstance()
        return forecastDate.get(YEAR) == currentDate.get(YEAR)
            && forecastDate.get(DAY_OF_YEAR) >= currentDate.get(DAY_OF_YEAR)
    }
}