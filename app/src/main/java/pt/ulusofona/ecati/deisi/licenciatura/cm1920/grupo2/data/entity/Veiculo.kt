package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Veiculo(
    var marca: String,
    var matricula: String,
    var modelo: String,
    var dataMatricula: String
) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()
    override fun toString(): String {
        return "Veiculo(marca='$marca', matricula='$matricula', modelo='$modelo', dataMatricula='$dataMatricula', uuid='$uuid')"
    }


}