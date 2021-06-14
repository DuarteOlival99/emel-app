package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels

import androidx.lifecycle.ViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.RetrofitBuilder
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Lugares
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.listeners.OnLugaresAdd
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.DetailsLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.MainLogic
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic.SplashScreenLogic


class ParqueDetailViewModel : ViewModel() {

    private val parqueDetailLogic = DetailsLogic()

    fun insertDetailsPark(parque: Parque) {
        parqueDetailLogic.insertDetail(parque)
    }

    fun getParkDetails(): Parque {
        return parqueDetailLogic.getDetailPark()
    }

    fun getIntImage(): Int {
        return parqueDetailLogic.getIntImage()
    }

    fun getIntEstado(): Int {
        return parqueDetailLogic.getIntEstado()
    }


}