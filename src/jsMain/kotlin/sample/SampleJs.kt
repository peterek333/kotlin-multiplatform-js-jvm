package sample

import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.addClass
import kotlin.dom.removeClass
import kotlin.js.Json

const val ACTIVE_CLASS = "active"

const val CITY_ID = "city"
const val SUBMIT_ID = "submit"
const val DATA_PLACE_ID = "dataPlace"
const val ONE_DAY_BTN_ID = "oneDayBtn"
const val SIX_DAYS_BTN_ID = "sixDaysBtn"

var cityInput: HTMLInputElement? = null
var submit: HTMLButtonElement? = null
var dataPlace: HTMLDivElement? = null
var oneDayBtn: HTMLElement? = null
var sixDaysBtn: HTMLElement? = null

actual class DataProvider {
    actual companion object {
        actual fun getUnit(): Units {
            val unitId = document.querySelector("input[name=\"unit\"]:checked")?.id
            return if (unitId != null) {
                Units.toEnum(unitId)
            } else {
                DEFAULT_UNIT
            }
        }
        actual fun getCity(): String {
            return cityInput?.value ?: EMPTY_CITY_NAME
        }
        actual fun setData(response: String) {
            val weatherForecast = DataHelper.parseData(response)
            showWeatherOnView(weatherForecast)
        }
    }
}
actual class DataHelper {
    actual companion object {
        actual fun parseData(response: String): WeatherForecast {
            val json: Json = JSON.parse(response) as Json
            val temp: Double = js("""json["main"]["temp"]""") as Double
            val pressure: Double = js("""json["main"]["pressure"]""") as Double
            val humidity: Double = js("""json["main"]["humidity"]""") as Double
            val windSpeed: Double = js("""json["wind"]["speed"]""") as Double
            val windDeg: Double = js("""json["wind"]["deg"]""") as Double

            return WeatherForecast(temp, pressure, humidity, windSpeed, windDeg)
        }
    }
}
actual class HttpClient {
    actual companion object {
        actual fun httpGET(url: String) {
            val xmlHttpRequest = XMLHttpRequest()
            xmlHttpRequest.onreadystatechange = {
                if (xmlHttpRequest.readyState == 4.toShort() && xmlHttpRequest.status == 200.toShort())
                    DataProvider.setData(xmlHttpRequest.responseText)
            }
            xmlHttpRequest.open("GET", url)
            xmlHttpRequest.send(null)
        }
    }
}

actual fun startApplication() {
    window.onload = {
        initViewElements()
        initListeners()
    }

}
actual fun initViewElements() {
    cityInput = document.getElementById(CITY_ID) as HTMLInputElement
    submit = document.getElementById(SUBMIT_ID) as HTMLButtonElement
    dataPlace = document.getElementById(DATA_PLACE_ID) as HTMLDivElement
    oneDayBtn = document.getElementById(ONE_DAY_BTN_ID) as HTMLElement
    sixDaysBtn = document.getElementById(SIX_DAYS_BTN_ID) as HTMLElement
}
fun initListeners() {
    submit?.onclick = {
        refreshData()
    }
    oneDayBtn?.onclick = {
        selectedView = WeatherApi.ONE_DAY
        oneDayBtn?.addClass(ACTIVE_CLASS)
        sixDaysBtn?.removeClass(ACTIVE_CLASS)
        refreshData()
    }
    sixDaysBtn?.onclick = {
        selectedView = WeatherApi.SIX_DAYS
        sixDaysBtn?.addClass(ACTIVE_CLASS)
        oneDayBtn?.removeClass(ACTIVE_CLASS)
        refreshData()
    }
}
fun showWeatherOnView(weatherForecast: WeatherForecast) {
    when (selectedView) {
        WeatherApi.ONE_DAY -> showTodayWeather(weatherForecast)
        WeatherApi.SIX_DAYS -> showSixDaysForecast(weatherForecast)
    }
}

fun showTodayWeather(weatherForecast: WeatherForecast) {
    dataPlace?.innerHTML = """
        <div class="row">
            <div class="center-col">
                <div class="card" style="width: 20rem;">
                    <div class="card-body">
                        <div class="card-t">${weatherForecast.temperature}&deg;${UIHelper.getTemperatureUnit()}</div>
                        <div class="card-sub">Today</div>
                        <p class="card-text">
                            Pressure: <span class="font-weight-bold">${weatherForecast.pressure}hPa</span><br/>
                            Humidity: <span class="font-weight-bold">${weatherForecast.humidity}%</span><br/>
                            Wind speed: <span class="font-weight-bold">${weatherForecast.windSpeed} ${UIHelper.getWindSpeedUnit()}</span><br/>
                            Wind degrees: <span class="font-weight-bold">${weatherForecast.windDegree}&deg;</span>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    """.trimIndent()
}

fun showSixDaysForecast(weatherForecast: WeatherForecast) {
    var innerHtml = ""
    for (row in 1..2) {
        var cols = ""
        for (col in 1..3) {
            val date = js("""new Date().toLocaleDateString("en-US")""")
            cols += """
                <div class="col-4">
                    <div class="card">
                        <div class="card-body">
                            <div class="card-t">${weatherForecast.temperature + col}&deg;${UIHelper.getTemperatureUnit()}</div>
                            <div class="card-sub">$date</div>
                            <p class="card-text">
                                Pressure: <span class="font-weight-bold">${weatherForecast.pressure - (row * col)}hPa</span><br/>
                                Humidity: <span class="font-weight-bold">${weatherForecast.humidity}%</span><br/>
                                Wind speed: <span class="font-weight-bold">${weatherForecast.windSpeed} ${UIHelper.getWindSpeedUnit()}</span><br/>
                                Wind degrees: <span class="font-weight-bold">${weatherForecast.windDegree}&deg;</span>
                            </p>
                        </div>
                    </div>
                </div>
            """.trimIndent()
        }
        innerHtml += """<div class="row">$cols</div>"""
    }
    dataPlace?.innerHTML = innerHtml
}
