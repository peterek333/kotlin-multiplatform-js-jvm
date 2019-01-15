package sample

const val DEFAULT_UNIT = "cel"
const val EMPTY_CITY_NAME = ""
val DEFAULT_SELECTED_VIEW = WeatherApi.ONE_DAY

expect class DataProvider() {
    companion object {
        fun getUnit(): String
        fun getCity(): String

        fun setData(response: String)
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

expect fun startApplication()
expect fun initViewElements()

/******************** IMPLEMENTED LOGIC ********************/
const val WEATHER_CORE_URL = "https://api.openweathermap.org/data/2.5/"
const val APPID = "3e675f42cb009cd3b9f5b2c9c4ac630a"

var selectedView = DEFAULT_SELECTED_VIEW

enum class WeatherApi(val url: String) {
    ONE_DAY("weather"),
    //FIVE_DAYS("forecast") //forecast for more than 1 day is in the paid subscription
    FIVE_DAYS("weather")
}
class WeatherForecast(
    val temperature: Double,
    val pressure: Double,
    val humidity: Double,
    val windSpeed: Double,
    val windDegree: Double
)
fun prepareUrl(endpoint: WeatherApi, city: String, unit: String): String {
    var params = "q=$city&APPID=$APPID"
    params += if (unit == DEFAULT_UNIT) {
        "&units=metric"
    } else {
        "&units=imperial"
    }
    return WEATHER_CORE_URL + endpoint.url + '?' + params
//    return "https://jsonplaceholder.typicode.com/todos/1"
}
fun getWeatherData(endpoint: WeatherApi) {
    HttpClient.httpGET(
        prepareUrl(endpoint, DataProvider.getCity(), DataProvider.getUnit()))
}
fun refreshData() {
    when (selectedView) {
        WeatherApi.ONE_DAY -> getWeatherData(WeatherApi.ONE_DAY)
        WeatherApi.FIVE_DAYS -> getWeatherData(WeatherApi.FIVE_DAYS)
    }
}
/******************** START APPLICATION ********************/
fun main(args: Array<String>) {
    startApplication()

}