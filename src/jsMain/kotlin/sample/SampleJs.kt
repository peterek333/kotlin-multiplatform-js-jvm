package sample

import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Json

const val CITY_ID = "city"
const val SUBMIT_ID = "submit"

var cityInput: HTMLInputElement? = null
var submit: HTMLButtonElement? = null

actual class DataProvider {
    actual companion object {
        actual fun getUnit(): String {
            return document.querySelector("input[name=\"unit\"]:checked")?.id ?: DEFAULT_UNIT
        }
        actual fun getCity(): String {
            return cityInput?.value ?: EMPTY_CITY_NAME
        }
        actual fun setData(response: String) {
            console.log("response", response)
            val weatherForecast = DataHelper.parseData(response)

        }
    }
}
actual class DataHelper {
    actual companion object {
        actual fun parseData(response: String): WeatherForecast {
            val json: Json = JSON.parse(response) as Json
            val temp: Double = js("""json["main"]["temp"]""") as Double
            val pressure: Double = js("""json["main"]["pressure"]""") as Double
            val humidity: Double =  js("""json["main"]["humidity"]""") as Double
            val windSpeed: Double =  js("""json["wind"]["speed"]""") as Double
            val windDeg: Double =  js("""json["wind"]["deg"]""") as Double

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
        console.log("unit", DataProvider.getUnit())
        initViewElements()
        initListeners()
    }

}
actual fun initViewElements() {
    cityInput = document.getElementById(CITY_ID) as HTMLInputElement
    submit = document.getElementById(SUBMIT_ID) as HTMLButtonElement
}
fun initListeners() {
    submit?.onclick = {
        refreshData()
    }
}


/** HELPERS **/
fun <T> parseFromJson(json: Json, vararg path: String): T {
    var actualPath = json
    console.log("actualPath", actualPath)
    for (step in path) {
        console.log("actualPath=$step", actualPath)
        var actualPath2 = actualPath[step]//.unsafeCast<Json>()
        console.log("actualPath=$step", actualPath2)
    }
    return actualPath.unsafeCast<T>()
}