package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic

import android.location.Location
import android.preference.PreferenceManager
import android.util.Log
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.services.ParkingLotsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Bikes
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.list.ListStorage
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.request.ParkingLot
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.repositories.OperationRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.APIKEY
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.Extensions
import retrofit2.Retrofit

class MapBikeLogic(private val repository: OperationRepository) {

    private val storage = ListStorage.getInstance()

    fun listBikesNoConnection(): List<Bikes> {
        return storage.getAllBikes()
    }

    fun listBikesConnection() {
        repository.updateBikesConnection()
    }

}