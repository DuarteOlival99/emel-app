package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotBikeResponse
import java.util.*

@Entity
data class Bikes (
    var nome : String,
    var idExpl : String,
    var numBikes : String,
    var numDocas : String,
    var estado : String,
    var latitude: Double,
    var longitude: Double
) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()

    override fun toString(): String {
        return "Bikes(nome='$nome', idExpl='$idExpl', numBikes='$numBikes', numDocas='$numDocas', estado='$estado', latitude=$latitude, longitude=$longitude, uuid='$uuid')"
    }


}