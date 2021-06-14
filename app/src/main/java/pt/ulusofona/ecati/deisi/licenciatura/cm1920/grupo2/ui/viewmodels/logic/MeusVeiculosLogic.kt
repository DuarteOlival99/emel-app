package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic

import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Veiculo
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.list.ListStorage
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.repositories.OperationRepository

class MeusVeiculosLogic (private val repository: OperationRepository) {

    private val storage = ListStorage.getInstance()

    fun insertVeiculo(veiculo: Veiculo) {
        storage.insertVeiculo(veiculo)
        repository.insertVeiculo(veiculo)
    }

    fun insertPositionVeiculo(position: Int, veiculo: Veiculo) {
            storage.insertPositionVeiculo(
                position,
                veiculo
            )
        repository.updateListVeiculos()
    }

    fun deleteVeiculo(veiculo: Veiculo, position: Int){
        storage.deleteVeiculo(position)
        repository.deleteVeiculo(veiculo)
    }

    fun updateVeiculo(veiculo: Veiculo, position: Int){
        storage.updateVeiculo(veiculo, position)
        repository.updateVeiculo(veiculo)
    }

    fun getAllVeiculos() : List<Veiculo> {
        return storage.getAllVeiculos()
    }

    fun updateVeiculoEspecifico(
        uuid: CharSequence?,
        marcaFinal: String,
        modeloFinal: String,
        matriculaFinal: String,
        dataMatriculoFinal: String,
        posicao: Int
    ) {
        storage.updateVeiculoEspecifico(uuid, marcaFinal, modeloFinal, matriculaFinal, dataMatriculoFinal, posicao)
        repository.updateVeiculoEspecifico(uuid, marcaFinal, modeloFinal, matriculaFinal, dataMatriculoFinal)
    }


}