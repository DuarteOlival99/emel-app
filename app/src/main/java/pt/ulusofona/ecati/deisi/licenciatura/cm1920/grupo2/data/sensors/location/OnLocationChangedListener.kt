package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location

import com.google.android.gms.location.LocationResult

interface OnLocationChangedListener {

    fun onLocationChanged(locastionResult: LocationResult)

}