package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.adapters

import android.content.Context
import android.os.BatteryManager
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projecto_02.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.parque_item.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.NavigationManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.ParqueDetailViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*


class ParquesAdapter(private var viewModel: ParqueDetailViewModel, private val context: Context, private val layout: Int, private var
parques: List<Parque>) : RecyclerView.Adapter<ParquesAdapter.ParquesViewHolder>(),
    OnMapReadyCallback {

    private var map: GoogleMap? = null

    class ParquesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nome: TextView =            view?.nome_parque_pop
        var estado: TextView? =         view?.estado_parque_text
        var atualizacao: TextView? =    view?.atualizacao_parque_text
        var distancia: TextView =       view?.distanc_parque_text
        var localizacao: TextView =     view?.localizacao_parque_text
        var abertura: TextView? =       view?.abertura_parque_text
        var imageEstado: TextView? =    view?.estado_parque
        var tipo: TextView? =           view?.tipo_parque_text
        var lugares: TextView? =        view?.lugares_parque_text
        var latitude : TextView? =      view?.lugares_latitude
        var longitude : TextView? =     view?.lugares_longitude


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParquesViewHolder {
        return ParquesViewHolder(
            LayoutInflater.from(context).inflate(layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return parques.size
    }

    override fun onBindViewHolder(holder: ParquesViewHolder, position: Int) {
        holder.latitude!!.text = parques[position].latitude
        holder.longitude!!.text = parques[position].longitude

        holder.nome.text = parques[position].nome
        holder.atualizacao?.text = parques[position].dataAtualizacao

        val decimal = BigDecimal(parques[position].distancia.toDouble()).setScale(2, RoundingMode.HALF_EVEN)
        holder.distancia.text = decimal.toString()

        holder.localizacao.text = parques[position].nome
        if (parques[position].activo == 1) {
            holder.abertura?.text = context.getString(R.string.aberto)
        }else {
            holder.abertura?.text = context.getString(R.string.fechado)
        }
        holder.lugares?.text = "" + (parques[position].capacidadeMax - parques[position].ocupacao)
        //Forma de traduzir o tipo de parque
        if( parques[position].tipo == "Estrutura") {
            holder.tipo?.text = context.getString(R.string.estrutura_popup)
        }else {
            holder.tipo?.text = context.getString(R.string.superfice_popup)
        }
        val percentagemOcupada = (parques[position].ocupacao * 100.00) / parques[position].capacidadeMax
        var imagem = R.drawable.ic_camera_green

        when {
            parques[position].ocupacao >= parques[position].capacidadeMax -> {
                Log.i("lotado", parques[position].toString())
                holder.estado?.text = context.getString(R.string.lotado)
                holder.imageEstado?.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_camera_red),null,null,null)
                imagem = R.drawable.ic_camera_red
            }
            percentagemOcupada > 90.00 -> {
                holder.estado?.text = context.getString(R.string.potencialmente_lotado)
                holder.imageEstado?.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_camera_laranja),null,null,null)
                imagem = R.drawable.ic_camera_laranja
            }
            else -> {
                holder.imageEstado?.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_camera_green),null,null,null)
                holder.estado?.text = context.getString(R.string.livre)
            }
        }

        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.apply {
            val dark = getBoolean("NOTURNO", false)
            val darkAuto = getBoolean("DARK", true)
            val c = Calendar.getInstance()
            //entre as 20 e 7
            val hour = c.get(Calendar.HOUR_OF_DAY)
            //recebe bateria
            val bm =
                context!!.getSystemService(Context.BATTERY_SERVICE) as BatteryManager?
            val batLevel = bm!!.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            if(dark || ((((hour in 20..24) || (hour in 0..7)) || batLevel <= 20) && darkAuto) ) {
                holder.itemView.estado_parque_text.setTextColor(context.resources.getColor(R.color.textDark))
                holder.itemView.atualizacao_parque_text.setTextColor(context.resources.getColor(R.color.textDark))
                holder.itemView.distanc_parque_text.setTextColor(context.resources.getColor(R.color.textDark))
                holder.itemView.localizacao_parque_text.setTextColor(context.resources.getColor(R.color.textDark))
                holder.itemView.abertura_parque_text?.setTextColor(context.resources.getColor(R.color.textDark))
                holder.itemView.distance_parque_text_km?.setTextColor(context.resources.getColor(R.color.textDark))
                holder.itemView.tipo_parque_text.setTextColor(context.resources.getColor(R.color.textDark))
                holder.itemView.lugares_parque_text?.setTextColor(context.resources.getColor(R.color.textDark))

            } else {
                holder.itemView.estado_parque_text.setTextColor(context.resources.getColor(R.color.textLight))
                holder.itemView.atualizacao_parque_text.setTextColor(context.resources.getColor(R.color.textLight))
                holder.itemView.distanc_parque_text.setTextColor(context.resources.getColor(R.color.textLight))
                holder.itemView.localizacao_parque_text.setTextColor(context.resources.getColor(R.color.textLight))
                holder.itemView.abertura_parque_text?.setTextColor(context.resources.getColor(R.color.textLight))
                holder.itemView.distance_parque_text_km?.setTextColor(context.resources.getColor(R.color.textLight))
                holder.itemView.tipo_parque_text.setTextColor(context.resources.getColor(R.color.textLight))
                holder.itemView.lugares_parque_text?.setTextColor(context.resources.getColor(R.color.textLight))
            }
        }


        holder.itemView.cardViewParque.setOnClickListener {
            val fm : FragmentManager = (context as AppCompatActivity).supportFragmentManager
//            val cfm : FragmentManager = (context as Fragment).childFragmentManager
//
//            fm?.let { DetailsDialogFragment(parques[position].nome, holder.distancia.text.toString(), parques[position].activo, parques[position].tipo, parques[position].capacidadeMax,
//                parques[position].ocupacao, parques[position].dataAtualizacao, holder.estado?.text.toString(), imagem).show(cfm, "Details") }
            viewModel.insertDetailsPark(parques[position])
            Log.i("INSERIParque", parques[position].toString())
            Log.i("PEDIParque", viewModel.getParkDetails().toString())

            NavigationManager.goToDetalhesFragment(fm)



        }

    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        val posisiabsen = LatLng(40.626401, 22.948352) ////your lat lng
        this.map!!.addMarker(MarkerOptions().position(posisiabsen).title("teste"))
        this.map!!.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen))
        this.map!!.uiSettings.isZoomControlsEnabled = true
        this.map!!.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)
        Log.i("mapa", "teste")
    }

}