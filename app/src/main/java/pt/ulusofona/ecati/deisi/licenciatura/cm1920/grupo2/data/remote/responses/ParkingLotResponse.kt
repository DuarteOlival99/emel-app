package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses

import com.google.gson.annotations.SerializedName


data class ParkingLotResponse (

    @SerializedName("id_parque")
    var idParque: String,

    var nome: String,

    var activo: Int ,

    @SerializedName("id_entidade")
    var idEntidade: Int ,

    @SerializedName("capacidade_max")
    var capacidadeMax: Int ,

    var ocupacao: Int ,

    @SerializedName("data_ocupacao")
    var dataAtualizacao: String,

    var latitude: String,

    var longitude: String,

    var tipo: String
)

