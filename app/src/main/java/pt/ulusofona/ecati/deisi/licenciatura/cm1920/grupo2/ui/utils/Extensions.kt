package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng

class Extensions {

    fun LocationToLatLng(location: Location) : LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    fun calculaDistanciaLocation(location1: Location , location2: Location): Float {

        //medir distancia entre casa e universidade
        //1 maneira com localizacoes do tipo Location
        val distancia = location1.distanceTo(location2)
        metroToKm(distancia)
        return distancia
    }

    fun calculaDistanciaLatLng(LatLng1Latitude: Double , LatLng1Longitude: Double, LatLng2Latitude: Double , LatLng2Longitude: Double): Float {

        //2 maneira com localizacoes so com os LatLng ou seja so latitude e longitude
        val results = FloatArray(1)
        Location.distanceBetween(
            LatLng1Latitude, LatLng1Longitude,
            LatLng2Latitude, LatLng2Longitude,
            results
        )
        results to Float
        return metroToKm(results[0])
    }

    fun metroToKm (distance: Float) : Float {

        if (distance > 1000.0f) {
            val dist: String = "${distance / 1000.0f} KM"
            return distance / 1000.0f
        }else {
            return distance / 1000.0f
        }

    }

}