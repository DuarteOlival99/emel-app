package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Veiculo
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.list.ListStorage
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.repositories.OperationRepository

class SplashScreenLogic(private val repository: OperationRepository) {

    private val storage = ListStorage.getInstance()

    fun getAllParques(): List<ParkingLotResponse> {
        return storage.getListParkk()
    }

    fun getParkingLotsConnection() {
        repository.updateParksConnection()
    }

    fun getParkingBikeLotsConnection() {
        Log.i("Connection", "Bikes1")
        repository.updateBikesConnection()
    }

    fun getParkingBikeLotsNoConnection() {
        repository.updateBikesNoConnection()
    }


    fun getParkingLotsNoConnection() {
        repository.updateParksNoConnection()
    }

    fun updateVeiculos() {
        repository.getVeiculos()
    }

    fun updateLugar() {
        repository.updateLugar()
    }



}