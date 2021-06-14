package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils

import android.app.Application
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.accelerometer.Accelerometer
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location.FusedLocation


class CalculatorApplication : Application()  {

    override fun onCreate() {
        super.onCreate()
        FusedLocation.start(this)
        Accelerometer.start(this)
        //Light.start(this)
        //Baterry.start(this)
    }

}