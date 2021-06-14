package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Lugares
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Veiculo
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.room.GetMeSpotDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.RetrofitBuilder
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.repositories.OperationRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.listeners.OnLugaresAdd
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.MeusLugaresLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.MeusVeiculosLogic

class MeusLugaresViewModel (application: Application) : AndroidViewModel(application) {

    private val storage = GetMeSpotDatabase.getInstance(application).operationDao()
    private val repository = OperationRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))
    private val meusLugaresLogic = MeusLugaresLogic(repository)
    var listLugares = mutableListOf<Lugares>()
    private var listener: OnLugaresAdd? = null

    //Observable ...
    private fun notifyOnLugaresAdd(){
        listener?.onLugaresAdd(listLugares)
    }

    fun registerListener(listener: OnLugaresAdd){
        this.listener = listener
        listener.onLugaresAdd(listLugares)
    }

    fun unregisterListener() {
        listener = null
    }
    //Observable


    fun procuraVeiculo(matricula : String) : Veiculo {
        return meusLugaresLogic.procuraVeiculo(matricula)
    }

    fun getLocation() : Location? {
        return meusLugaresLogic.getLocation()
    }

    fun insertLugar(lugares: Lugares) {
        meusLugaresLogic.insertLugar(lugares)
    }

    fun getLugar(): Lugares? {
        return meusLugaresLogic.getLugar()
    }


}