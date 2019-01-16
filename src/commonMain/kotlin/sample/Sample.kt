package sample

const val EMPTY_CITY_NAME = ""
val DEFAULT_UNIT = Units.METRIC
val DEFAULT_SELECTED_VIEW = WeatherApi.ONE_DAY

expect class DataProvider() {
    companion object {
        fun getUnit(): Units
        fun getCity(): String
    }
}

expect class DataHelper() {
    companion object {
        fun parseData(response: String): WeatherForecast
    }
}

expect class HttpClient() {
    companion object {
        fun httpGET(url: String)
    }
}

class UIHelper {
    companion object {
        fun getTemperatureUnit(): String {
            return if (isMetric()) {
                "C"
            } else {
                "F"
            }
        }
        fun getWindSpeedUnit(): String {
            return if (isMetric()) {
                "km/h"
            } else {
                "mph"
            }
        }
        private fun isMetric(): Boolean {
            return DataProvider.getUnit() == DEFAULT_UNIT
        }
    }
}
expect fun startApplication()
expect fun showTodayWeather(weatherForecast: WeatherForecast)
expect fun showSixDaysForecast(weatherForecast: WeatherForecast)

/******************** IMPLEMENTED LOGIC ********************/
const val WEATHER_CORE_URL = "https://api.openweathermap.org/data/2.5/"
const val APPID = "3e675f42cb009cd3b9f5b2c9c4ac630a"

var selectedView = DEFAULT_SELECTED_VIEW

enum class Units(val unitName: String) {
    METRIC("metric"),
    IMPERIAL("imperial");

    companion object {
        fun toEnum(unitName: String): Units {
            for (unit in Units.values()) {
                if (unitName == unit.unitName) {
                    return unit
                }
            }
            return DEFAULT_UNIT
        }
    }
}
enum class WeatherApi(val url: String) {
    ONE_DAY("weather"),
    //SIX_DAYS("forecast") //forecast for more than 1 day is in the paid subscription
    SIX_DAYS("weather")
}
class WeatherForecast(
    val temperature: Double,
    val pressure: Double,
    val humidity: Double,
    val windSpeed: Double,
    val windDegree: Double
)
fun prepareUrl(endpoint: WeatherApi, city: String, unit: Units): String {
    var params = "q=$city&APPID=$APPID&units=${unit.unitName}"
    return WEATHER_CORE_URL + endpoint.url + '?' + params
}
fun getWeatherData(endpoint: WeatherApi) {
    HttpClient.httpGET(
        prepareUrl(endpoint, DataProvider.getCity(), DataProvider.getUnit()))
}
fun setData(response: String) {
    val weatherForecast = DataHelper.parseData(response)
    showWeatherOnView(weatherForecast)
}
fun refreshData() {
    when (selectedView) {
        WeatherApi.ONE_DAY -> getWeatherData(WeatherApi.ONE_DAY)
        WeatherApi.SIX_DAYS -> getWeatherData(WeatherApi.SIX_DAYS)
    }
}
fun showWeatherOnView(weatherForecast: WeatherForecast) {
    when (selectedView) {
        WeatherApi.ONE_DAY -> showTodayWeather(weatherForecast)
        WeatherApi.SIX_DAYS -> showSixDaysForecast(weatherForecast)
    }
}
/******************** START APPLICATION ********************/
fun main(args: Array<String>) {
    startApplication()
}