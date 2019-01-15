package sample

import tornadofx.*

actual class DataProvider {
    actual companion object {
        actual fun getUnit(): String {
            return DEFAULT_UNIT
        }
        actual fun getCity(): String {
            return ""
        }
        actual fun setData(response: String) {

        }
    }
}
actual class DataHelper {
    actual companion object {
        actual fun parseData(response: String): WeatherForecast {
            return WeatherForecast(1.2, 1.2, 1.2, 1.2,  1.2)
        }
    }
}
actual class HttpClient {
    actual companion object {
        actual fun httpGET(url: String) {
        }
    }
}

/********************************* APP *********************************/
class MyApp: App(MyView::class)

class MyView: View() {
    override val root = vbox {
        button("Press me " + DataProvider.getUnit())
        label("Waiting")
    }
}

actual fun startApplication() {
    launch<MyApp>()
}
actual fun initViewElements() {

}