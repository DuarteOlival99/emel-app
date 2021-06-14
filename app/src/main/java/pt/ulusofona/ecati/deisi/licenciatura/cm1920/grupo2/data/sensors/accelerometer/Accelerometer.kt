package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.preference.PreferenceManager
import android.util.Log
import kotlinx.android.synthetic.main.fragment_opcoes.*
import kotlin.math.sqrt


class Accelerometer private constructor(context: Context) : SensorEventListener{

    private val TAG = Accelerometer::class.java.simpleName
    private var mShakeTimestamp: Long = 0
    private var mShakeCount = 0
    private var sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    companion object {

        private const val SHAKE_THRESHOLD_GRAVITY = 2.7f
        private const val SHAKE_SLOP_TIME_MS = 500
        private const val SHAKE_COUNT_RESET_TIME_MS = 3000
        private var listener: OnShakeListener? = null
        private var instance: Accelerometer? = null
        private var shakeEnable = true

        fun registerListener (listener: OnShakeListener) {
            Companion.listener = listener
        }

        fun unregisterListener() {
            listener = null
        }

        fun notifyListeners(shake: Int) {
            Log.i("SHAKE_SHAKE", shake.toString())
            listener?.onShake(shake)
        }

        fun start(context: Context) {

            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            pref.apply {
                shakeEnable = getBoolean("SHAKE", true)
            }

            if(shakeEnable){
                instance = if(instance == null) Accelerometer(context) else instance
                instance?.start()
            }
        }

    }

    //Inicializa a captura de valores por parte do sensor
    private fun start() {
        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onAccuracyChanged(
        sensor: Sensor,
        accuracy: Int
    ) {
        // ignore
    }

    override fun onSensorChanged(event: SensorEvent) {




            Log.i(TAG,"onSensorChanged: X: ${event.values[0]} Y: ${event.values[1]} Z: ${event.values[2]}")
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            // gForce will be close to 1 when there is no movement.
            val gForce: Float =
                sqrt(gX * gX + gY * gY + gZ * gZ)
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }

                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0
                }
                mShakeTimestamp = now
                mShakeCount++
                notifyListeners(mShakeCount)
                Log.i(TAG,"Shake = ${mShakeCount}")
            }
    }



}