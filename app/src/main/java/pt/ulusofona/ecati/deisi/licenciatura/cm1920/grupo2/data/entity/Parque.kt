package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Parque(var idParque: String,
             var nome: String,
             var activo: Int ,
             var idEntidade: Int ,
             var capacidadeMax: Int ,
             var ocupacao: Int ,
             var dataAtualizacao: String,
             var latitude: String,
             var longitude: String,
             var tipo: String,
             var distancia: Float = 0.0F
) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "Parque(idParque='$idParque', nome='$nome', activo=$activo, idEntidade=$idEntidade, capacidadeMax=$capacidadeMax, ocupacao=$ocupacao, dataAtualizacao='$dataAtualizacao', latitude='$latitude', longitude='$longitude', tipo='$tipo', distancia=$distancia, uuid='$uuid')"
    }

}