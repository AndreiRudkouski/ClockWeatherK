package by.rudkouski.widget.app

import by.rudkouski.widget.BuildConfig

object Constants {
    const val API_KEY = BuildConfig.ApiKey

    //Intent codes
    const val WIDGET_CLOCK_UPDATE_REQUEST_CODE = 1001
    const val OTHER_WEATHER_UPDATE_REQUEST_CODE = 1002
    const val CURRENT_WEATHER_UPDATE_REQUEST_CODE = 1003
    const val LOCATION_UPDATE_REQUEST_CODE = 1004
    const val REQUEST_PERMISSION_CODE = 12345

    //Actions
    private const val PREFIX = BuildConfig.APPLICATION_ID
    const val WIDGET_UPDATE_ACTION = PREFIX + "WIDGET_UPDATE"
    const val OTHER_WEATHER_UPDATE_ACTION = PREFIX + "WEATHER_UPDATE"
    const val CURRENT_WEATHER_UPDATE_ACTION = PREFIX + "CURRENT_WEATHER_UPDATE"
    const val LOCATION_UPDATE_ACTION = PREFIX + "LOCATION_UPDATE"
    const val LOCATION_ACTIVITY_UPDATE_WEATHER = PREFIX + "LOCATION_ACTIVITY_UPDATE"
}