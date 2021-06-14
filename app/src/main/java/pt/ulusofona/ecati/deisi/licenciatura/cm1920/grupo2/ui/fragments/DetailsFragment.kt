package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.BatteryManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.example.projecto_02.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.filter_pop_up_parque.view.nome_parque_popup_filtros
import kotlinx.android.synthetic.main.fragment_pop_up_parque.*
import kotlinx.android.synthetic.main.fragment_pop_up_parque.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.Extensions
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.ParqueDetailViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class DetailsFragment : Fragment(), OnMapReadyCallback {
    private val TAG = MeusVeiculosFragment::class.java.simpleName

    lateinit var preferences: SharedPreferences
    lateinit var location : Location
    private var map: GoogleMap? = null
    private var zoom = true
    private val extensions = Extensions()
    lateinit var viewModel: ParqueDetailViewModel
    private lateinit var detailParque: Parque

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pop_up_parque, container, false)
        ButterKnife.bind(this, view)
        preferences =  PreferenceManager.getDefaultSharedPreferences(context)
        view.popup_map.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ParqueDetailViewModel::class.java)
//        view.popup_map.getMapAsync(this)
//        view.popup_map.isFocusable = true

        Log.i("mapa1", "teste")
        //dialogView.popup_map.onResume()


        ButterKnife.bind(this,view)

        return view

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ParqueDetailViewModel::class.java)
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        detailParque = viewModel.getParkDetails()
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
                view!!.setBackgroundColor(resources.getColor(R.color.backgroundDark))
                view!!.localizacao_parque_text_pop.setTextColor(resources.getColor(R.color.textDark))
                view!!.distanc_parque_text_pop.setTextColor(resources.getColor(R.color.textDark))
                view!!.distanc_parque_text_km.setTextColor(resources.getColor(R.color.textDark))
                view!!.tipo_parque_text_pop.setTextColor(resources.getColor(R.color.textDark))
                view!!.abertura_parque_text_pop.setTextColor(resources.getColor(R.color.textDark))
                view!!.lugares_parque_text_pop.setTextColor(resources.getColor(R.color.textDark))
                view!!.estado_parque_text_pop.setTextColor(resources.getColor(R.color.textDark))
                view!!.atualizacao_parque_text_pop.setTextColor(resources.getColor(R.color.textDark))
            } else {
                view!!.setBackgroundColor(resources.getColor(R.color.backgroundLight))
                view!!.localizacao_parque_text_pop.setTextColor(resources.getColor(R.color.textLight))
                view!!.distanc_parque_text_pop.setTextColor(resources.getColor(R.color.textLight))
                view!!.distanc_parque_text_km.setTextColor(resources.getColor(R.color.textLight))
                view!!.tipo_parque_text_pop.setTextColor(resources.getColor(R.color.textLight))
                view!!.abertura_parque_text_pop.setTextColor(resources.getColor(R.color.textLight))
                view!!.lugares_parque_text_pop.setTextColor(resources.getColor(R.color.textLight))
                view!!.estado_parque_text_pop.setTextColor(resources.getColor(R.color.textLight))
                view!!.atualizacao_parque_text_pop.setTextColor(resources.getColor(R.color.textLight))
            }
        }
        view!!.nome_parque_popup_filtros.text = detailParque.nome
        view!!.localizacao_parque_text_pop.text = detailParque.nome
        val decimal = BigDecimal(detailParque.distancia.toDouble()).setScale(2, RoundingMode.HALF_EVEN)
        view!!.distanc_parque_text_pop.text = decimal.toString()
        if (detailParque.activo == 1) {
            view!!.abertura_parque_text_pop.text = getText(R.string.aberto).toString()
        }else {
            view!!.abertura_parque_text_pop.text = getText(R.string.fechado).toString()
        }
        if( detailParque.tipo == "Estrutura") {
            view!!.tipo_parque_text_pop.text = getText(R.string.estrutura_popup)
        }else {
            view!!.tipo_parque_text_pop.text = getText(R.string.superfice_popup)
        }

        if ((detailParque.capacidadeMax - detailParque.ocupacao) <= 0) {
            view!!.lugares_parque_text_pop.text = "0"
        }else {
            view!!.lugares_parque_text_pop.text = (detailParque.capacidadeMax - detailParque.ocupacao).toString()
        }

        view!!.atualizacao_parque_text_pop.text = detailParque.dataAtualizacao

        var imagem = viewModel.getIntImage()
        var estado = viewModel.getIntEstado()

        view!!.estado_parque_text_pop.text = getText(estado)

        view!!.estado_parque_pop.setCompoundDrawablesWithIntrinsicBounds( context!!.getDrawable(imagem) ,null,null,null )


//            dialogView.imageButtonDirections.setOnClickListener {
//                val fm : FragmentManager = (context as AppCompatActivity).supportFragmentManager
//                NavigationManager.goToMapFragment(fm)
//                mAlertDialog.dismiss()
//            }


        popup_map.getMapAsync(this)
        popup_map.onResume()
//        zoomMyLocation()
        super.onStart()
    }

    private fun zoomMyLocation() {
        val location = Location("localizacao")
        location.latitude = detailParque.latitude.toDouble()
        location.longitude = detailParque.longitude.toDouble()
        Log.i("Location", location.toString())
        if (location != null) {
            map!!.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    extensions.LocationToLatLng(location!!), 13f
                )
            )
            val cameraPosition = CameraPosition.Builder()
                .target(
                    extensions.LocationToLatLng(location!!)
                ) // Sets the center of the map to location user
                .zoom(17f) // Sets the zoom
                .bearing(90f) // Sets the orientation of the camera to east
                .tilt(40f) // Sets the tilt of the camera to 30 degrees
                .build() // Creates a CameraPosition from the builder
            map!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        zoomMyLocation()
        val position = LatLng(detailParque.latitude.toDouble(), detailParque.longitude.toDouble()) ////your lat lng
        this.map!!.addMarker(MarkerOptions().position(position).title(detailParque.nome))
//        this.map!!.moveCamera(CameraUpdateFactory.newLatLng(position))
        this.map!!.uiSettings.isZoomControlsEnabled = true
//        this.map!!.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)
        Log.i("mapa", "teste")
    }

    override fun onDestroy() {
        Log.i("onDestroy", "evocado")
        super.onDestroy()
    }
}