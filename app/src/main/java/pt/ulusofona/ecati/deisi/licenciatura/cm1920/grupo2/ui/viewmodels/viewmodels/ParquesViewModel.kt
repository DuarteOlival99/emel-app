package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels

import androidx.lifecycle.ViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.listeners.OnParquesAdd
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.ParquesLogic
import java.util.*

//const val ENDPOINT = "https://emel.city-platform.com/opendata/"

class ParquesViewModel : ViewModel(){

    private val parquesLogic = ParquesLogic()
    var listParques = mutableListOf<Parque>()
    private var listener: OnParquesAdd? = null

    //Observable ...
    private fun notifyOnParquesAdd(){
        listener?.onParquesAdd(listParques)
    }

    fun registerListener(listener: OnParquesAdd){
        this.listener = listener
        listener.onParquesAdd(listParques)
    }

    fun unregisterListener() {
        listener = null
    }
    //Observable

    fun getParks() : List<Parque> {
        return parquesLogic.getParks()
    }

    fun getParques(tipoParque: String, lotacao: String, distancia: Int): List<Parque> {
        return parquesLogic.getAllParqueFilter(tipoParque, lotacao, distancia)
    }

    fun getPossibleLocation () : Boolean {
        return parquesLogic.getPossibleLocation()
    }

    fun updateDistance() {
        parquesLogic.updateDistance()
    }


    fun getDescendingParques(): List<Parque> {
        return parquesLogic.getAllParqueDescending()
        //notifyOnVeiculoAdd() // chama o Observable
    }

    fun getFreeParques(): List<Parque> {
        return parquesLogic.getFreeParque()
    }

    fun getPotencialmenteLotadosParques(): List<Parque> {
        return parquesLogic.getPotencialmenteLotadosParque()
    }

    fun getLotadosParques(): List<Parque> {
        return parquesLogic.getLotadosParque()
    }

    fun getTipoParques(tipo: String): List<Parque> {
        return parquesLogic.getTipoParque(tipo = tipo)
    }

    fun getFirstTime(): Boolean {
        return parquesLogic.getFirstTime()
    }


}