package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic

import android.location.Location
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Lugares
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Veiculo
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.list.ListStorage
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.repositories.OperationRepository

class MeusLugaresLogic (private val repository: OperationRepository) {

    private val storage = ListStorage.getInstance()

    fun procuraVeiculo(matricula: String) : Veiculo {

        return storage.getSpecificVeiculo(matricula)

    }

    fun getLocation(): Location? {
        return storage.getLocation()
    }

    fun insertLugar(lugares: Lugares) {
        storage.insertLugar(lugares)
        repository.insertLugar(lugares)
    }

    fun getLugar(): Lugares? {
        return storage.getLugar()
    }


}