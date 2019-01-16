package sample

import com.google.gson.Gson
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import okhttp3.*
import sample.ViewElementsProvider.Companion.city
import sample.ViewElementsProvider.Companion.imperial
import sample.ViewElementsProvider.Companion.metric
import sample.ViewElementsProvider.Companion.oneDayPlace
import sample.ViewElementsProvider.Companion.sixDaysPlace
import tornadofx.*
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDate.now

actual class DataProvider {
    actual companion object {
        actual fun getUnit(): Units {
            if (metric?.isSelected!!) {
                return Units.METRIC
            } else if (imperial?.isSelected!!) {
                return Units.IMPERIAL
            }
            return DEFAULT_UNIT
        }
        actual fun getCity(): String {
            return city?.text ?: EMPTY_CITY_NAME
        }
    }
}
actual class DataHelper {
    actual companion object {
        actual fun parseData(response: String): WeatherForecast {
            var gson = Gson()
            var parsedWeather: WeatherResponse = gson.fromJson(response, WeatherResponse::class.java)
            return WeatherForecast(parsedWeather.main.temp, parsedWeather.main.pressure, parsedWeather.main.humidity,
                parsedWeather.wind.speed,  parsedWeather.wind.deg)
        }
    }
}
actual class HttpClient {
    actual companion object {
        actual fun httpGET(url: String) {
            val httpClient = OkHttpClient()
            val request = Request.Builder().url(url).build()
//            GlobalScope.launch {
                httpClient.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        setData(response.body()?.string()!!)
                    }

                    override fun onFailure(call: Call, exception: IOException) {

                    }
                })
//            }
        }
    }
}

/********************************* APP *********************************/
class WeatherResponse(val main: Main, val wind: Wind) {
//    lateinit var main: Main
//    lateinit var main: Main

    data class Main(
        val temp: Double,
        val pressure: Double,
        val humidity: Double
    )
    data class Wind(
        val speed: Double,
        val deg: Double
    )
}
class ViewElementsProvider {
    companion object {
        var city: TextField? = null
        var metric: RadioButton? = null
        var imperial: RadioButton? = null
        var submit: Button? = null
        var oneDayPlace: GridPane? = null
        var sixDaysPlace: GridPane? = null

        fun setElements(city: TextField, metric: RadioButton, imperial: RadioButton,
                        submit: Button, oneDayPlace: GridPane, sixDaysPlace: GridPane) {
            this.city = city
            this.metric = metric
            this.imperial = imperial
            this.submit = submit
            this.oneDayPlace = oneDayPlace
            this.sixDaysPlace = sixDaysPlace
        }
    }
}
class MyApp: App(WeatherAppView::class)

actual fun startApplication() {
    launch<MyApp>()
}
actual fun showTodayWeather(weatherForecast: WeatherForecast) {
    Platform.runLater {
        oneDayPlace?.clear()
        val dataNode = createDataNode(weatherForecast, now())
        oneDayPlace?.add(dataNode, 0, 0)
    }
}
actual fun showSixDaysForecast(weatherForecast: WeatherForecast) {
    Platform.runLater {
        sixDaysPlace?.clear()
        var date = now()
        for (row in 0..2) {
            for (col in 0..1) {
                date = date.plusDays(1)
                val dataNode = createDataNode(weatherForecast, date)

                sixDaysPlace?.add(dataNode, col, row)
            }
        }
    }
}
fun createDataNode(weatherForecast: WeatherForecast, date: LocalDate): VBox {
    val vbox = VBox()
    vbox.alignment = Pos.CENTER
    vbox.prefHeight = 200.0
    vbox.prefWidth = 200.0

    val tempLabel = Label(weatherForecast.temperature.toString() + "°" + UIHelper.getTemperatureUnit())
    tempLabel.font = Font.font("System Bold", 32.0)
    vbox.addChildIfPossible(tempLabel)

    val dateLabel = Label(date.toString())
    dateLabel.font = Font.font(20.0)
    dateLabel.textFill = Paint.valueOf("#a1a1a1")
    vbox.addChildIfPossible(dateLabel)

    val pressLabel = Label("Pressure: " + weatherForecast.pressure + "hPa")
    vbox.addChildIfPossible(pressLabel)

    val humidityLabel = Label("Humidity: " + weatherForecast.humidity + "%")
    vbox.addChildIfPossible(humidityLabel)

    val windSpeedLabel = Label("Wind speed: " + weatherForecast.windSpeed + UIHelper.getWindSpeedUnit())
    vbox.addChildIfPossible(windSpeedLabel)

    val windDegreesLabel = Label("Wind degrees: " + weatherForecast.windDegree + "°")
    vbox.addChildIfPossible(windDegreesLabel)

    return vbox
}