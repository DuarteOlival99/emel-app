package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import org.xml.sax.Locator
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Lugares
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.RetrofitBuilder
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.listeners.OnLugaresAdd
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.MapLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.ParquesLogic
import kotlin.math.log

class MapViewModel : ViewModel(){

    private val mapLogic = MapLogic(RetrofitBuilder.getInstance(ENDPOINT))
    private val parquesLogic = ParquesLogic()

    fun updateParks(list: List<ParkingLotResponse>) {
        mapLogic.updateParques(list)
    }

    fun getListParks() : List<Parque>{
        return mapLogic.getParks()
    }

    fun getLocation() : Location? {
        return mapLogic.getLocation()
    }

    fun updateDistance() {
        mapLogic.updateDistance()
    }

    fun listaParques(tipoParque: String, lotacao: String, distancia: Int): List<Parque> {
        return parquesLogic.getAllParqueFilter(tipoParque, lotacao, distancia)
    }

    fun getPossibleLocation () : Boolean {
        return parquesLogic.getPossibleLocation()
    }




}