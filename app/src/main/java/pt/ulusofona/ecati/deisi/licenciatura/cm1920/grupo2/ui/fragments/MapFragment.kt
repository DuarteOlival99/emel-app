package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import butterknife.OnClick
import butterknife.Optional
import com.example.projecto_02.R
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.accelerometer.Accelerometer
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.accelerometer.OnShakeListener
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location.FusedLocation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location.OnLocationChangedListener
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.Extensions
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.MapViewModel

const val REQUEST_CODE = 100

class MapFragment : PermissionedFragment(REQUEST_CODE),
    OnMapReadyCallback, OnLocationChangedListener, OnShakeListener {

    private val extensions = Extensions()
    private var map: GoogleMap? = null
    private var zoom = true
    private var location: Location? = null
    lateinit var viewModel: MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        view.map_view.onCreate(savedInstanceState)
        return view
    }

    override fun onStart() {
        super.onRequestPermissions(
            activity?.baseContext!!, arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        if (!viewModel.getPossibleLocation()) {
            Log.i("localizacao_parques", "erro")
            val builder = AlertDialog.Builder(context)
            builder.setMessage(getString(R.string.popup_parques_message))
                .setPositiveButton(getString(R.string.ok),
                    DialogInterface.OnClickListener { dialog, id ->
                        // User set ok to dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()
        }
        Accelerometer.registerListener(this)
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        FusedLocation.start(activity!!.applicationContext)
        Accelerometer.start(activity!!.applicationContext)
    }

    override fun onRequestPermissionSucces() {
        Log.i("Mapa", "teste")
        FusedLocation.registerListener(this)
        map_view.getMapAsync(this)
        map_view.onResume()
        zoomMyLocation()
    }

    override fun onRequestPermissionFailure() {
        //implementar msg de erro
        Log.i("localizacao", "erro")
    }

    override fun onShake(count: Int) {
        Log.i("SHAKE", count.toString())
        if (count > 0 ){
            val prefe = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = prefe.edit()

            editor
                .putString("TIPOPARQUE", "Todos")
                .putString("ESTADOPARQUE", "Todos")
                .putInt("DISTANCIA", 0)
                .apply()

            map!!.clear()
            adicionaMarkerParques()
            Log.i("SHAKE_MAPA", count.toString())
            Toast.makeText(context, getText(R.string.limpar_filtros), Toast.LENGTH_LONG).show()
        }
    }


    fun adicionaMarkerParques() {
        viewModel.updateDistance()
        var listPark = listOf<Parque>()
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.apply {
            val lotacao = getString("ESTADOPARQUE", "Todos")
            val tipoParque = getString("TIPOPARQUE", "Todos")
            var distancia = getInt("DISTANCIA", 0)
            distancia /= 10
            listPark = viewModel.listaParques(tipoParque!!, lotacao!!, distancia)
        }

        for (parque in listPark){

            val lotacao = (parque.ocupacao * 100.00) / parque.capacidadeMax
            val iconRed: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            val iconGreen: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            val iconOrange: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)

            val iconSuperficieRed : BitmapDescriptor? = bitmapDescriptorFromVector(context as Activity, R.drawable.ic_superficie_red)
            val iconSuperficieGreen : BitmapDescriptor? = bitmapDescriptorFromVector(context as Activity, R.drawable.ic_superficie_green)
            val iconSuperficieOrange : BitmapDescriptor? = bitmapDescriptorFromVector(context as Activity, R.drawable.ic_superficie_orange)

            val iconEstruturaRed : BitmapDescriptor? = bitmapDescriptorFromVector(context as Activity, R.drawable.ic_estrutura_red)
            val iconEstruturaGreen : BitmapDescriptor? = bitmapDescriptorFromVector(context as Activity, R.drawable.ic_estrutura_green)
            val iconEstruturaOrange : BitmapDescriptor? = bitmapDescriptorFromVector(context as Activity, R.drawable.ic_estrutura_orange)

            val lstValues: List<String> = parque.tipo.split("'\'").map { it -> it.trim() }

            when (lstValues[0]) {
                "Estrutura" -> {
                    when {
                        parque.ocupacao == parque.capacidadeMax -> {
                            adicionaMarker(LatLng(parque.latitude.toDouble() , parque.longitude.toDouble()), parque.nome, criaDescricaoParque(parque, lotacao), iconEstruturaRed!!)
                        }
                        lotacao > 90.0 -> {
                            adicionaMarker(LatLng(parque.latitude.toDouble() , parque.longitude.toDouble()), parque.nome, criaDescricaoParque(parque, lotacao), iconEstruturaOrange!!)
                        }
                        else -> {
                            adicionaMarker(LatLng(parque.latitude.toDouble() , parque.longitude.toDouble()), parque.nome, criaDescricaoParque(parque, lotacao), iconEstruturaGreen!!)
                        }
                        //termina o when
                    }}
                "Superfície" -> {
                    when {
                        parque.ocupacao == parque.capacidadeMax -> {
                            adicionaMarker(LatLng(parque.latitude.toDouble() , parque.longitude.toDouble()), parque.nome, criaDescricaoParque(parque, lotacao), iconSuperficieRed!!)
                        }
                        lotacao > 90.0 -> {
                            adicionaMarker(LatLng(parque.latitude.toDouble() , parque.longitude.toDouble()), parque.nome, criaDescricaoParque(parque, lotacao), iconSuperficieOrange!!)
                        }
                        else -> {
                            adicionaMarker(LatLng(parque.latitude.toDouble() , parque.longitude.toDouble()), parque.nome, criaDescricaoParque(parque, lotacao), iconSuperficieGreen!!)
                        }
                        //termina o when
                    }}
                else -> {
                    when {
                        parque.ocupacao == parque.capacidadeMax -> {
                            adicionaMarker(LatLng(parque.latitude.toDouble() , parque.longitude.toDouble()), parque.nome, criaDescricaoParque(parque, lotacao), iconRed)
                        }
                        lotacao > 90.0 -> {
                            adicionaMarker(LatLng(parque.latitude.toDouble() , parque.longitude.toDouble()), parque.nome, criaDescricaoParque(parque, lotacao), iconOrange)
                        }
                        else -> {
                            adicionaMarker(LatLng(parque.latitude.toDouble() , parque.longitude.toDouble()), parque.nome, criaDescricaoParque(parque, lotacao), iconGreen)
                        }
                        //termina o when
                    }}}
            //termina o for
        }

    }

    fun criaDescricaoParque(park : Parque, lotacao : Double) : String {
        var descricao : String = ""
        var lotacaoString = ""

//        if(park.activo == 1){
//            descricao += "Estado do parque: Activo" + "\n"
//        }else {
//            descricao += "Estado do parque: Desativo" + "\n"
//        }
//        when {
//            lotacao == 100.0 -> {
//                lotacaoString = "Lotado"
//            }
//            lotacao > 90.0 -> {
//                lotacaoString = "Potencialmente lotado"
//            }
//            else -> {
//                lotacaoString = "Livre"
//            }
//        }
//        descricao += "Lotação: ${lotacaoString}" + "\n"

        descricao += getString(R.string.park_lugares_ocupados) + park.ocupacao + "\n"
        descricao += getString(R.string.park_lugares_existentes)+ park.capacidadeMax + "\n\n"
        //descricao += "Tipo de parque: ${park.tipo}" + "\n"
        //descricao += "Distancia: ${park.distancia} Km" + "\n" + "\n"
        descricao += getString(R.string.ultima_atualizacao) + park.dataAtualizacao + "\n"

        //Log.i("descricao", descricao)
        return descricao

    }


    override fun onMapReady(map: GoogleMap?) {
        Log.i("teste", "teste1")
        Log.i("teste", location.toString())
        this.map = map
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        this.map!!.isMyLocationEnabled = true
        zoomMyLocation()
        //fazer um for para percorrer todos os parques e com latitude e longitude adicionar um
        //marker e o titulo como nome do parque
        adicionaMarkerParques()
        mapInfoWindow()
    }

    private fun markerComIcon(){
        //exemplo de criacao de marker com um drawable vector
//        var markerOrange = MarkerOptions()
//            .position(LatLng(38.9064, -9.0527))
//            .title("teste_icon")
//            .icon(bitmapDescriptorFromVector(context as Activity, R.drawable.ic_local_parking_orange))
//        this.map!!.addMarker( markerOrange)

    }

    private fun adicionaMarker(position: LatLng, title: String, snippet: String, icon: BitmapDescriptor) {

        /*exemplo de chamada da funcao
            //criacao de marcador default com cor diferente
            val iconGreen: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                //chama a funcao
                adicionaMarker(LatLng(38.9061, -9.0522),"teste1",
                    "snippet" + "\n" + "snippet1"+ "\n" + "snippet2" + "\n" + "snippet3", iconGreen)
        */

        val marker = MarkerOptions()
            .position(position)
            .title(title)
            .snippet(snippet)
            .icon(icon)

        val m = map!!.addMarker(marker)
//        m.hideInfoWindow()
//        m.showInfoWindow()
        Log.i("marker adicionado", marker.toString())

    }

    fun redesenhaMapa() {
        map_view.getMapAsync(this)
        map_view.onResume()
        zoomMyLocation()
    }

    private fun mapInfoWindow(){
        map!!.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker?): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View? {
                val info = LinearLayout(context)
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(context)
                title.setTextColor(Color.BLACK)
                title.gravity = Gravity.CENTER
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(context)
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet
                info.addView(title)
                info.addView(snippet)
                return info
            }
        })
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun zoomMyLocation() {
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

    private fun zoomMyLocationOnChanged() {
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
                //.zoom(Constants.MAX_ZOOM) // Sets the zoom to current zoom map
                .bearing(90f) // Sets the orientation of the camera to east
                .tilt(40f) // Sets the tilt of the camera to 30 degrees
                .build() // Creates a CameraPosition from the builder
            map!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    override fun onLocationChanged(locastionResult: LocationResult) {
        val location = locastionResult.lastLocation
        this.location = location
        if (this.location == null) {
            Log.i("muda = null", "teste")
            location.let { this.location = it }
            viewModel.updateDistance()
            zoomMyLocation()
        } else if ((this.location!!.latitude != location.latitude) ||
            (this.location!!.longitude != location.longitude)
        ) {
            Log.i("muda", "teste")
            location.let { this.location = it }
            viewModel.updateDistance()
            //adicionaMarkerParques()
            zoomMyLocation()
        } else {
            Log.i("igual", "teste")
        }
        Log.i("localizacaoMap", location.toString())
    }

}