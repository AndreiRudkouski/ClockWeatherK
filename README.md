# ClockWeather_Widget
## Description
This is a widget for Android OS. It helps you to know current weather conditions and hour-by-hour and day-by-day forecasts out to seven days for the chosen location. There are some predefined cities and the current location which is periodically determined by GPS.

The weather information is updated from time to time if the network is available otherwise the weather is updated as soon as it's possible. Also, you can update the weather manually. The widget uses information from *https://darksky.net*

There are some settings by which you can change the widget theme (dark or gray), time intervals for updating location and weather and so on.

All information is available in two languages English and Russian.

## The widget deploy
1.  Register on *https://darksky.net* and get your api key

2.	Find the .gradle folder in your home directory. Usually, it can be found at:

  >Windows: C:/Users/Your_Username/.gradle

  >Mac: /Users/Your_Username/.gradle
  
  >Linux: /home/Your_Username/.gradle
  
Inside it, there would be a file named `gradle.properties` (just create it if there isn’t any). After that, add your key to the file as a property with name **dark_sky_api_key**. The file after adding the key might look something like:
>dark_sky_api_key="your-api-key"

3.	Build the widget
