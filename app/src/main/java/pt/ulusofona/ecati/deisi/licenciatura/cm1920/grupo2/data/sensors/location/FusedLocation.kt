package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.list.ListStorage
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments.MapFragment


class FusedLocation(context: Context) : LocationCallback() {


    private val TAG = FusedLocation::class.java.simpleName
    // intervalos de tempo em que a localizacao é verificada, 20 segundos
    private val TIME_BETWEEN_UPDATES = 2 * 1000L
    // Este atributo é utilizado para configurar os pedidos de localizacao
    private var locationRequest: LocationRequest? = null
    //Este atributo sera utilizado para acedermos à API da Fused location
    private var client = FusedLocationProviderClient(context)
    private val storage = ListStorage.getInstance()

    init {
        //configurar a precisao e os tempos entre atualizacoes da localizacao
        locationRequest = LocationRequest()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = TIME_BETWEEN_UPDATES

        //instanciar o objeto que permite definir as configuracoes
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
            .build()

        //Aplicar as configuracoes ao servico de localizacao
        LocationServices.getSettingsClient(context)
            .checkLocationSettings(locationSettingsRequest)
    }

    companion object {

        private var listener: OnLocationChangedListener? = null
        private var instance: FusedLocation? = null

        fun registerListener (listener: OnLocationChangedListener) {
            Companion.listener = listener
        }

        fun unregisterListener() {
            listener = null
        }

        fun notifyListeners (locationResult: LocationResult) {
            Log.i("localizacao", "localizacao_update")
            listener?.onLocationChanged(locationResult)
        }

        fun start (context: Context) {
            instance = if (instance == null) FusedLocation(context) else instance
            instance?.startLocationUpdates(context)
        }

    }

    private fun startLocationUpdates(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                 context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        client.requestLocationUpdates(locationRequest, this, Looper.myLooper())
    }

    override fun onLocationResult(locationResult: LocationResult?) {
        Log.i(TAG, locationResult?.lastLocation.toString())
        Log.i(TAG, "localizacao")
        storage.updateLocalizacao(locationResult!!.lastLocation)
        notifyListeners(locationResult)
        super.onLocationResult(locationResult)
    }

}