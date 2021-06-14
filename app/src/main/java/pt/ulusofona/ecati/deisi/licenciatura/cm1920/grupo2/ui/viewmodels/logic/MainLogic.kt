package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic

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
import retrofit2.Retrofit

class MainLogic() {

    private val storage = ListStorage.getInstance()
    private val parquesLogic = ParquesLogic()

    fun getParkMeNow() : Parque? {
        parquesLogic.updateDistance()
        CoroutineScope(Dispatchers.IO).apply {
            val list = storage.getAllParques().sortedBy { it.distancia.toInt() }
            for ( parque in  list) {
                if( (parque.ocupacao < parque.capacidadeMax) ) {
                    return parque
                }
            }
        }
        return null
    }

}