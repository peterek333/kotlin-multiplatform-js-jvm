package sample

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.RadioButton
import javafx.scene.control.Tab
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import tornadofx.*

class WeatherAppView : View("Weather") {
    override val root: Pane by fxml("weatherAppView.fxml")

    val city: TextField by fxid()
    val metric: RadioButton by fxid()
    val imperial: RadioButton by fxid()
    val submit: Button by fxid()
    val oneDayTab: Tab by fxid()
    val sixDaysTab: Tab by fxid()
    val oneDayPlace: GridPane by fxid()
    val sixDaysPlace: GridPane by fxid()

    init {
        initListeners()

        ViewElementsProvider.setElements(city, metric, imperial, submit, oneDayPlace, sixDaysPlace)
    }

    private fun initListeners() {
        metric.onAction = EventHandler {
            metric.selectedProperty().set(true)
            imperial.selectedProperty().set(false)
        }
        imperial.onAction = EventHandler {
            metric.selectedProperty().set(false)
            imperial.selectedProperty().set(true)
        }
        submit.onAction = EventHandler {
            refreshData()
        }
        oneDayTab.onSelectionChanged = EventHandler {
            selectedView = WeatherApi.ONE_DAY
            refreshData()
        }
        sixDaysTab.onSelectionChanged = EventHandler {
            selectedView = WeatherApi.SIX_DAYS
            refreshData()
        }
    }
}
