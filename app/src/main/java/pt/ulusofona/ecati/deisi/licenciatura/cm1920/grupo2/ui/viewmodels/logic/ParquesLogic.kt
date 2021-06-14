package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic

import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationResult
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.list.ListStorage
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location.OnLocationChangedListener
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.Extensions
import java.math.RoundingMode
import java.text.DecimalFormat

class ParquesLogic : OnLocationChangedListener {
    private val storage = ListStorage.getInstance()
    private val extensions = Extensions()
    private lateinit var location : Location


    fun updateDistance(){
        if(storage.getLocation() != null) {
            location = storage.getLocation()!!
            for (park in storage.getAllParques()){
                val locationPark = Location(park.nome)
                locationPark.latitude = park.latitude.toDouble()
                locationPark.longitude = park.longitude.toDouble()
                park.distancia = extensions.metroToKm(extensions.calculaDistanciaLocation(location, locationPark))
            }
        }

    }

    fun getAllParqueFilter(tipoParque: String, lotacao: String, distancia: Int) : List<Parque> {

        var streamList: List<Parque> = storage.getAllParques()

        //Significa que esta ativo o filtro se não for todos
        if (tipoParque != "Todos") {
            streamList = getTipoParque(streamList, tipoParque)
        }

        if (lotacao != "Todos") {
            when (lotacao.toLowerCase()) {
                "lotado" -> streamList = getLotadosParque(streamList)
                "livre" -> streamList = getFreeParque(streamList)
                "parcial" -> streamList = getPotencialmenteLotadosParque(streamList)
            }
        }

        when (distancia) {
            0 -> streamList = streamList.sortedBy { it.distancia }
            distancia -> streamList = getAllParqueAscending(distancia.toString(), streamList)
        }


        return streamList
    }

    fun getAllParqueDescending() : List<Parque> {

        val list = storage.getAllParques().sortedByDescending { it.distancia.toInt() }

        return list
    }


    fun getAllParqueAscending(distancia: String, lista: List<Parque> = storage.getAllParques()) : List<Parque> {
        var finalList = mutableListOf<Parque>()

        val list = lista.sortedBy { it.distancia}

        for (ele in list) {
            if (ele.distancia <= distancia.toFloat()) {
                finalList.add(ele)
            }else {
                break
            }
        }
        //Log.i("lista", lista.toString())

        return finalList
    }

    fun getFreeParque(lista: List<Parque> = storage.getAllParques()): List<Parque> {
        var freeParque = mutableListOf<Parque>()
         for (parque in lista) {
            val percentage = (parque.ocupacao * 100.00) / parque.capacidadeMax

            if (percentage <= 90.00) {
                freeParque.add(parque)
            }
        }
        return freeParque
    }

    fun getTipoParque(lista: List<Parque> = storage.getAllParques(), tipo: String): List<Parque> {
        var tipoParque = mutableListOf<Parque>()

        for (parque in lista) {

            if (parque.tipo == tipo) {
                tipoParque.add(parque)
            }
        }
        return tipoParque
    }

    fun getPotencialmenteLotadosParque(lista: List<Parque> = storage.getAllParques()): List<Parque> {

        var potencialmenteLotadoParque = mutableListOf<Parque>()
        for (parque in lista) {
            val percentage = (parque.ocupacao * 100.00) / parque.capacidadeMax

            if (percentage >= 90.00 && percentage < 100.00) {
                potencialmenteLotadoParque.add(parque)
            }
        }
        return potencialmenteLotadoParque

    }

    fun getLotadosParque(lista: List<Parque> = storage.getAllParques()): List<Parque> {

        var LotadoParque = mutableListOf<Parque>()
        for (parque in lista) {
            val percentage = (parque.ocupacao * 100.00) / parque.capacidadeMax

            if (percentage == 100.00) {
                LotadoParque.add(parque)
            }
        }
        return LotadoParque

    }

    override fun onLocationChanged(locastionResult: LocationResult) {
        location = locastionResult.lastLocation
        Log.i("localizacaoParquesLogic", location.toString())
    }

    fun getParks(): List<Parque> {
        return storage.getListPark()
    }

    fun getPossibleLocation(): Boolean {
        return storage.getPossibleLocation()
    }

    fun getFirstTime(): Boolean {
        return storage.getFirstTime()
    }

}