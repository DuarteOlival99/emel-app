package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.logic

import android.util.Log
import com.example.projecto_02.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.list.ListStorage

class DetailsLogic {

    private val storage = ListStorage.getInstance()

    fun getDetailPark() : Parque {
        val detailPark = storage.getParkDetail()
        Log.i("PEDI", "Detalhes" + detailPark)
        return detailPark
    }

    fun insertDetail(parque: Parque) {
        Log.i("INSERI", "Detalhes" + parque)
        storage.insertParkDetail(parque)
    }

    fun getIntImage(): Int {
        val parque = storage.getParkDetail()
        val percentagemOcupada = (parque.ocupacao * 100.00) / parque.capacidadeMax
        var imagem = R.drawable.ic_camera_green

        when {
            parque.ocupacao >= parque.capacidadeMax -> {
                imagem = R.drawable.ic_camera_red
            }
            percentagemOcupada > 90.00 -> {

                imagem = R.drawable.ic_camera_laranja
            }
            else -> {
                imagem = R.drawable.ic_camera_green
            }
        }
        return imagem
    }

    fun getIntEstado(): Int {
        val parque = storage.getParkDetail()
        val percentagemOcupada = (parque.ocupacao * 100.00) / parque.capacidadeMax
        var estado = R.string.livre
        when {
            parque.ocupacao >= parque.ocupacao -> {
               estado = R.string.potencialmente_lotado
            }
            percentagemOcupada > 90.00 -> {
                estado = R.string.potencialmente_lotado
            }
            else -> {
                estado = R.string.livre
            }
        }
        return estado
    }

}