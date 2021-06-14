package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import org.xml.sax.Locator
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Bikes
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Lugares
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.room.GetMeSpotDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.RetrofitBuilder
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.repositories.OperationRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.listeners.OnLugaresAdd
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.MapBikeLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.MapLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.ParquesLogic
import kotlin.math.log

class MapBikeViewModel ( application: Application) : AndroidViewModel(application){

    private val storage = GetMeSpotDatabase.getInstance(application).operationDao()
    private val repository = OperationRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))
    private val mapLogic = MapBikeLogic(repository)

    fun listBikesNoConnection () : List<Bikes> {
        return mapLogic.listBikesNoConnection()
    }

    fun listBikesConnection() {
        return mapLogic.listBikesConnection()
    }

}