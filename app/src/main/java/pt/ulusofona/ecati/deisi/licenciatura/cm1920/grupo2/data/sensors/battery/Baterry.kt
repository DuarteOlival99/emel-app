package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.battery

import android.content.Context
import android.os.BatteryManager
import android.os.Handler
import android.util.Log

class Baterry private constructor(private val context: Context) : Runnable {

    private val TAG = Baterry::class.java.simpleName
    private val TIME_BETWEEN_UPDATES = 2000L

    companion object {

        private var instance: Baterry? = null
        private val handler = Handler()

        fun start (context: Context) {
            instance = if ( instance == null ) Baterry(context) else instance
            instance?.start()
        }

    }

    private fun start() {
        handler.postDelayed(this, TIME_BETWEEN_UPDATES)
    }

    private fun getBaterryCurrentNow() : Double {
        val manager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val value = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        //esta divisao é utilizada para converter micro amperes em amperes
        Log.i(TAG, value.toString())
        Log.i(TAG, ( value.toDouble() / 1000000 ).toString() )

        return if ( value != 0 && value != Int.MIN_VALUE) value.toDouble() / 1000000 else 0.0
    }

    override fun run() {
        val current = getBaterryCurrentNow()
        Log.i(TAG, current.toString())
        handler.postDelayed(this, TIME_BETWEEN_UPDATES)
    }

}