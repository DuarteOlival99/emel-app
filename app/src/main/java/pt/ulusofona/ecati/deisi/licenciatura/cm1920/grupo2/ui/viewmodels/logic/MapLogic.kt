package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic

import android.location.Location
import android.preference.PreferenceManager
import android.util.Log
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.services.ParkingLotsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.list.ListStorage
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.request.ParkingLot
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.APIKEY
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.Extensions
import retrofit2.Retrofit

class MapLogic(private val retrofit: Retrofit) {

    private val storage = ListStorage.getInstance()
    private val extensions = Extensions()

    fun updateParques(list: List<ParkingLotResponse>) {
        storage.updateListapark(list)
    }

    fun getParks() : List<Parque> {
        return storage.getListPark()
    }

    fun getLocation() : Location? {
        return storage.getLocation()
    }

    fun updateDistance(){
        val location = storage.getLocation()

        if(location != null) {
            Log.i("distanciaUpdate", storage.getAllParques().toString())
            for (park in storage.getAllParques()){
                val locationPark = Location(park.nome)
                locationPark.latitude = park.latitude.toDouble()
                locationPark.longitude = park.longitude.toDouble()
                park.distancia = extensions.metroToKm(extensions.calculaDistanciaLocation(location, locationPark))
            }
        }

    }

}