package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.list

import android.location.Location
import android.util.Log
import android.widget.ImageView
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Lugares
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Veiculo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Bikes
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.Extensions


class ListStorage private constructor() {

    private lateinit var parkDetail: Parque
    private var storageParque = listOf<Parque>()
    private var storageVeiculo = mutableListOf<Veiculo>()
    //private var storageLugares = mutableListOf<Lugares>()
    private var MeuLugar : Lugares? = null
    private var storageBikes = mutableListOf<Bikes>()

    private var location : Location? = null
    private var locationPossible = -1

    private var firstTime = 0

    // private val veiculoDefault: Veiculo = Veiculo("Opel", "xx-00-xx", "Astra", "19/03/2008")
    // private val veiculoDefault1: Veiculo = Veiculo("Ford", "xx-00-yy", "Mustang", "20/03/2008")

    // private val lugarDefault: Lugares = Lugares("Parque de Estacionamento", "Opel", "xx-00-xx", "6", "10")


    private var listPark = listOf<ParkingLotResponse>()

    private val extensions = Extensions()

    companion object {

        private var instance: ListStorage? = null

        fun getInstance() : ListStorage {
            synchronized(this) {
                if(instance == null) {
                    instance =
                        ListStorage()
                }
                return instance as ListStorage
            }
        }

    }

   fun getPossibleLocation() : Boolean {

       if (location == null) {
           if (locationPossible == 0) {
               locationPossible += 1
               return false
           } else {
               return true
           }
       } else {
           locationPossible = 0
           return true
       }
   }

    fun getParkDetail(): Parque {
        return parkDetail
    }

    fun insertParkDetail(parque: Parque) {
        parkDetail = parque
    }

    fun updateLocalizacao(lastLocation: Location?) {
        location = lastLocation!!
        Log.i("storage_Localizacao", location.toString())

        if(location != null) {
            for (park in getAllParques()){
                val locationPark = Location(park.nome)
                locationPark.latitude = park.latitude.toDouble()
                locationPark.longitude = park.longitude.toDouble()
                park.distancia = extensions.metroToKm(extensions.calculaDistanciaLocation(location!!, locationPark))
            }
        }

    }

    fun getLocation() : Location? {
        return location
    }

    fun updateListapark(list: List<ParkingLotResponse>) {

        var listParque = mutableListOf<Parque>()

        for (parque in list){

            val parkAdd = Parque(
                parque.idParque , parque.nome, parque.activo, parque.idEntidade, parque.capacidadeMax,
                parque.ocupacao, parque.dataAtualizacao, parque.latitude, parque.longitude,
                parque.tipo)
            //extensions.metroToKm(extensions.calculaDistanciaLatLng(location.latitude, location.longitude, parque.latitude.toDouble(), parque.longitude.toDouble())
            listParque.add(parkAdd)
        }

        storageParque = listParque

    }

    fun getListPark(): List<Parque> {
        return storageParque
    }

    fun getListParkk(): List<ParkingLotResponse> {
        return listPark
    }

    fun insertPositionVeiculo(position: Int, veiculo: Veiculo){
//        withContext(Dispatchers.IO){
//            //Thread.sleep(30000)
//            storageVeiculo.add(position, veiculo)
//            Log.i("listatesteAdiciona", storageVeiculo.toString())
//        }
        storageVeiculo.add(position, veiculo)
    }

    fun deleteVeiculo(position: Int){
        storageVeiculo.removeAt(position)
    }

    fun updateVeiculo(veiculo: Veiculo, position: Int){
        storageVeiculo[position] = veiculo
        Log.i("veiculo", storageVeiculo[position].toString())
    }

    fun updateParque(list: List<Parque>){
        storageParque = list
    }

    fun getAllParques() : List<Parque>{

        return storageParque
    }

    fun getSpecificVeiculo(matricula: String) : Veiculo {
        val v = Veiculo("", "", "", "")

        for (veiculo in storageVeiculo) {
            if (veiculo.matricula == matricula){
                return veiculo
            }
        }
        return v
    }

    //veiculos

    fun updateVeiculos(listVeiculos: List<Veiculo>) {
        storageVeiculo = listVeiculos as MutableList<Veiculo>
    }

    fun insertVeiculo(veiculo: Veiculo) {
        storageVeiculo.add(veiculo)
    }

    fun deleteVeiculo(veiculo: Veiculo) {
        storageVeiculo.remove(veiculo)
    }

    fun getAllVeiculos() : List<Veiculo> {
        return storageVeiculo
    }

    fun updateVeiculoEspecifico(
        uuid: CharSequence?,
        marcaFinal: String,
        modeloFinal: String,
        matriculaFinal: String,
        dataMatriculoFinal: String,
        posicao: Int
    ) {
        storageVeiculo[posicao].marca = marcaFinal
        storageVeiculo[posicao].modelo = modeloFinal
        storageVeiculo[posicao].matricula = matriculaFinal
        storageVeiculo[posicao].dataMatricula = dataMatriculoFinal
    }


    //Lugares

    fun insertLugar(lugares: Lugares) {
        MeuLugar = lugares
    }

    fun getLugar() : Lugares? {
        return MeuLugar
    }

    fun updateBikes(listBikes: MutableList<Bikes>) {
        storageBikes = listBikes
    }

    fun getAllBikes(): List<Bikes> {
        return storageBikes
    }

    fun getFirstTime(): Boolean {
        if (firstTime == 0) {
            firstTime += 1
           return true
        }
        return false
    }


}