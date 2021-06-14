package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.repositories

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.list.ListStorage
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.room.dao.OperationDao
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotBikeResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.services.ParkingLotsService
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.APIKEY
import retrofit2.Retrofit

class OperationRepository(private val local: OperationDao, private val remote: Retrofit) {

    private val storage = ListStorage.getInstance()
    private var result = false

    //Parques

    fun getParkingLots() {
        val service = remote.create(ParkingLotsService::class.java)
        val apiKey = APIKEY

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getParkingLots(apiKey)
            if (response.isSuccessful) {
                Log.i("PARKINGLOTS", response.body().toString())
                val listaParques : List<ParkingLotResponse>? = response.body()
                //Log.i("listaParques", listaParques.toString())

                //storage.updateListapark(listaParques!!)
                updateListapark(listaParques)
                result = false
                }else {
                result = true
                Log.i("PARKINGLOTS FAIL", response.errorBody().toString())
            }
        }
    }

    suspend fun updateListapark(list: List<ParkingLotResponse>?) {
        Log.i("updateParquesRepository", list.toString())
        val lista = local.getAllParques()
        var listParque = mutableListOf<Parque>()
        if (list != null) {
            for (parque in list){
                val parkAdd = Parque(
                    parque.idParque,
                    parque.nome,
                    parque.activo,
                    parque.idEntidade,
                    parque.capacidadeMax,
                    parque.ocupacao,
                    parque.dataAtualizacao,
                    parque.latitude,
                    parque.longitude,
                    parque.tipo)
                //extensions.metroToKm(extensions.calculaDistanciaLatLng(location.latitude, location.longitude, parque.latitude.toDouble(), parque.longitude.toDouble())
                listParque.add(parkAdd)
                var result = false
                for ( p in lista) {
                    if( p.idParque == parkAdd.idParque) {
                        parkAdd.uuid = p.uuid
                        local.updateParque(parkAdd)
                        //local.delete(p)
                        //local.insert(parkAdd)
                        result = true
                        break
                    }}
                if (!result) {
                    local.insert(parkAdd)
                }
            }}
        storage.updateParque(listParque)
    }

    fun updateParksConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Api", "entrou Repository")
            getParkingLots()
            val listParques = local.getAllParques()
            storage.updateParque(listParques)
        }
    }

    fun updateParksNoConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Dao", "entrou Repository")
            val listParques = local.getAllParques()
            storage.updateParque(listParques)
        }
    }

    //Bicicletas

    fun getParkingBikeLots() {
        Log.i("Connection", "Bikes2")
        val service = remote.create(ParkingLotsService::class.java)
        val apiKey = APIKEY
        Log.i("Connection", "Bikes3")
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Connection", "Bikes4")
            val response = service.getParkingBikeLots(apiKey)
            Log.i("Connection", "Bikes5")
            if (response.isSuccessful) {
                Log.i("ParkingBikesLOTS", response.body().toString())

                updateBikes(response.body())


            }else {
                Log.i("ParkingLOTS FAIL", response.errorBody().toString())
            }
        }
    }

    suspend fun updateBikes (list: ParkingLotBikeResponse?) {
        //val lista = local.getBikes()
        //Log.i("updateBikesRepository", lista.toString())
        val lista = local.getAllBikes()
        val listBikes = mutableListOf<Bikes>()

        for ( feature in list!!.features) {
            val bikeAdd = Bikes ( feature.properties.designComercial,
                feature.properties.idExpl,
                feature.properties.numBikes,
                feature.properties.numDocas,
                feature.properties.estado,
                feature.geometry.coordinates[0][1],
                feature.geometry.coordinates[0][0]
            )
            listBikes.add(bikeAdd)
            var result = false
            for ( bike in lista ) {
                if (bike.idExpl == bikeAdd.idExpl) {
                    bikeAdd.uuid = bike.uuid
                    local.updateBike(bikeAdd)
                    result = true
                    break
                }}
            if (!result) {
                local.insertBike(bikeAdd)
            }}
        storage.updateBikes(listBikes)
        Log.i("listBikes", listBikes.toString())
    }

    fun updateBikesConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Api", "entrou Repository")
            getParkingBikeLots()
            val listBikes = local.getAllBikes()
            storage.updateBikes(listBikes as MutableList<Bikes>)
        }
    }

    fun updateBikesNoConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Dao", "entrou Repository")
            val listBikes = local.getAllBikes()
            storage.updateBikes(listBikes as MutableList<Bikes>)
        }
    }



    //Veiculos

    fun getVeiculos() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Get_Veiculos", "get")
            val listVeiculos = local.getAllVeiculos() // recebe a lista de veiculos da DataBase
            storage.updateVeiculos(listVeiculos) // insere a lista recebida na lista storageVeiculos do ListStorage
        }
    }

    fun updateVeiculo(veiculo: Veiculo) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Update_veiculo", "update")
            Log.i("Update_veiculo", veiculo.toString())
            local.updateVeiculo(veiculo)
            Log.i("Update_veiculo", local.getAllVeiculos().toString())
        }
    }

    fun updateListVeiculos() {
        CoroutineScope(Dispatchers.IO).launch {
            for ( veiculo in local.getAllVeiculos()) {
                local.deleteVeiculo(veiculo)
            }
            for (veiculo in storage.getAllVeiculos()) {
                local.insertVeiculo(veiculo)
            }
        }
    }

    fun updateVeiculoEspecifico(uuid: CharSequence?, marcaFinal: String, modeloFinal: String, matriculaFinal: String, dataMatriculoFinal: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("updateVeiculoEspecifico", uuid.toString())
//            Log.i("updateVeiculoEspecifico", local.getByVeiculoId(uuid.toString()).toString())
            local.updateVeiculoMarca(uuid.toString(), marcaFinal)
            local.updateVeiculoModelo(uuid.toString(), modeloFinal)
            local.updateVeiculoMatricula(uuid.toString(), matriculaFinal)
            local.updateVeiculoDataMatricula(uuid.toString(), dataMatriculoFinal)
            Log.i("updateVeiculoEspecifico", local.getAllVeiculos().toString())
        }
    }

    fun insertVeiculo(veiculo: Veiculo){
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Insert_Veiculos", "insert")
            local.insertVeiculo(veiculo)
        }
    }

    fun deleteVeiculo(veiculo: Veiculo) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Delete_Veiculos", "Delete")
            local.deleteVeiculo(veiculo)
        }
    }


    //Lugares

    fun insertLugar(lugar: Lugares) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("insert_lugar", "Insert")
            local.insertLugar(lugar)
        }
    }

    fun updateLugar() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("Update_lugar", "Get")
            val lugar : Lugares? = local.getLugar()
            Log.i("Update_lugar", lugar.toString())
            if (lugar != null) {
                storage.insertLugar(lugar!!)
            }else {
                Log.i("Update_lugar", lugar.toString())
            }

        }
    }



}