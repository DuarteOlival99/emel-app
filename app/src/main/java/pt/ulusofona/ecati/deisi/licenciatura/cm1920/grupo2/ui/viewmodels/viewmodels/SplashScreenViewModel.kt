package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Lugares
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.room.GetMeSpotDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.RetrofitBuilder
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.repositories.OperationRepository
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.listeners.OnLugaresAdd
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.ParquesLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.SplashScreenLogic

const val ENDPOINT = "https://emel.city-platform.com/opendata/"

class SplashScreenViewModel ( application: Application ) : AndroidViewModel (application) {

    private val storage = GetMeSpotDatabase.getInstance(application).operationDao()
    private val repository = OperationRepository(storage, RetrofitBuilder.getInstance(ENDPOINT))
    private val splashScreenLogic = SplashScreenLogic(repository)
    private val parquesLogic = ParquesLogic()

    var listLugares = mutableListOf<Lugares>()
    private var listener: OnLugaresAdd? = null

    fun getParkingLotsConnection() {
        splashScreenLogic.getParkingLotsConnection()
    }

    fun getParkingBikeLotsConnection() {
        Log.i("Connection", "Bikes")
        splashScreenLogic.getParkingBikeLotsConnection()
    }

    fun getParkingLotsNoConnection() {
        splashScreenLogic.getParkingLotsNoConnection()
    }

    fun getParkingBikeLotsNoConnection() {
        splashScreenLogic.getParkingBikeLotsNoConnection()
    }

    fun getAllParks(): List<ParkingLotResponse> {
       return splashScreenLogic.getAllParques()
    }

    //Observable ...
    private fun notifyOnLugaresAdd(){
        listener?.onLugaresAdd(listLugares)
    }

    fun registerListener(){
        this.listener = listener
//        listener.onLugaresAdd(listLugares)
    }

    fun unregisterListener() {
        listener = null
    }

    fun updateVeiculos() {
        splashScreenLogic.updateVeiculos()
    }

    fun updateLugar() {
        splashScreenLogic.updateLugar()
    }

    fun getPossibleLocation () : Boolean {
        return parquesLogic.getPossibleLocation()
    }


    //Observable
}