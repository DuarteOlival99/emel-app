package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Lugares(
    var image: ByteArray?,
    var latitude: Double?,
    var longitude: Double?
) {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()
    override fun toString(): String {
        return "Lugares(image=${image?.contentToString()}, latitude=$latitude, longitude=$longitude, uuid='$uuid')"
    }

}