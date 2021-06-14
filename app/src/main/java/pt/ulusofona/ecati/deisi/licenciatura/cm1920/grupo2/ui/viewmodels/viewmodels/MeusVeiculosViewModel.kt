package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Veiculo
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.room.GetMeSpotDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.RetrofitBuilder
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.repositories.OperationRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.listeners.OnVeiculoAdd
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.MeusVeiculosLogic

class MeusVeiculosViewModel (application: Application) : AndroidViewModel(application) {

    private val storage = GetMeSpotDatabase.getInstance(application).operationDao()
    private val repository = OperationRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))
    private val meusVeiculosLogic = MeusVeiculosLogic(repository)
    var listVeiculos = mutableListOf<Veiculo>()
    private var listener: OnVeiculoAdd? = null

    //Observable ...
    private fun notifyOnVeiculoAdd(){
        listener?.onVeiculoAdd(listVeiculos)
    }

    fun registerListener(listener: OnVeiculoAdd){
        this.listener = listener
        listener.onVeiculoAdd(listVeiculos)
    }

    fun unregisterListener() {
        listener = null
    }
    //Observable


    fun getVeiculos() : MutableList<Veiculo>{
        return meusVeiculosLogic.getAllVeiculos().toMutableList()
        //notifyOnVeiculoAdd() // chama o Observable

    }

    fun deleteVeiculo(veiculo: Veiculo, position: Int){
        meusVeiculosLogic.deleteVeiculo(veiculo, position)
        //notifyOnVeiculoAdd()
    }

    fun updateVeiculo(veiculo: Veiculo, position: Int){
        meusVeiculosLogic.updateVeiculo(veiculo, position)
        notifyOnVeiculoAdd()
    }

    fun newVeiculo(veiculo: Veiculo) {
        meusVeiculosLogic.insertVeiculo(veiculo)
        notifyOnVeiculoAdd()
    }

    fun newPositionVeiculo(position: Int, veiculo: Veiculo) {
        meusVeiculosLogic.insertPositionVeiculo(position,veiculo)
        notifyOnVeiculoAdd()
    }

    fun updateVeiculoEspecifico(
        uuid: CharSequence?,
        marcaFinal: String,
        modeloFinal: String,
        matriculaFinal: String,
        dataMatriculoFinal: String,
        posicao: Int
    ) {
        meusVeiculosLogic.updateVeiculoEspecifico(uuid, marcaFinal, modeloFinal, matriculaFinal, dataMatriculoFinal, posicao)
    }


}