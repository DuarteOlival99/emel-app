package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.services

import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.request.ParkingLot
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotBikeResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import retrofit2.Response
import retrofit2.http.*

interface ParkingLotsService {

    @GET("parking/lots")
    suspend fun getParkingLots(@Header("api_key") authorization: String): Response<List<ParkingLotResponse>>

    @GET("gira/station/list")
    suspend fun getParkingBikeLots(@Header("api_key") authorization: String): Response<ParkingLotBikeResponse>


}