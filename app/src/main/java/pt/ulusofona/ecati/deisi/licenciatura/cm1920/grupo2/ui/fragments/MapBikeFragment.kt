package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.example.projecto_02.R
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_map_bicicleta.*
import kotlinx.android.synthetic.main.fragment_map_bicicleta.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Bikes
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location.FusedLocation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location.OnLocationChangedListener
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.Extensions
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.MapBikeViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.SplashScreenViewModel

class MapBikeFragment : PermissionedFragment(REQUEST_CODE),
    OnMapReadyCallback, OnLocationChangedListener {

    lateinit var viewModel: SplashScreenViewModel
    lateinit var viewModelMapBike: MapBikeViewModel
    private val extensions = Extensions()
    private var map: GoogleMap? = null
    private var zoom = true
    private var location: Location? = null
    lateinit var listBikes : List<Bikes>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map_bicicleta, container, false)
        viewModel = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        viewModelMapBike = ViewModelProviders.of(this).get(MapBikeViewModel::class.java)
        view.map_bike_view.onCreate(savedInstanceState)
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
        viewModel.registerListener()
        val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        Log.i("connectividade", isConnected.toString())

        if (isConnected){
            viewModel.getParkingBikeLotsConnection()
            viewModelMapBike.listBikesConnection()
        }else{
            viewModel.getParkingBikeLotsNoConnection()
            listBikes = viewModelMapBike.listBikesNoConnection()
        }
        Log.i("Connection", "Connection")
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelMapBike = ViewModelProviders.of(this).get(MapBikeViewModel::class.java)
        FusedLocation.start(activity!!.applicationContext)
    }

    override fun onRequestPermissionSucces() {
        Log.i("Mapa", "teste")
        FusedLocation.registerListener(this)
        map_bike_view.getMapAsync(this)
        map_bike_view.onResume()
        //zoomMyLocation()
    }

    override fun onRequestPermissionFailure() {
        //implementar msg de erro
        Log.i("localizacao", "erro")
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
        markerAllBikes()
        mapInfoWindow()
    }

    private fun markerAllBikes() {
        listBikes = viewModelMapBike.listBikesNoConnection()
        Log.i("mapa_listBikes", listBikes.toString())

        for ( bike in listBikes ) {
            if ( bike.estado == "active" ) {
                adicionaMarker(bike)
            }}
    }

    private fun adicionaMarker(bike: Bikes) {
        val marker = MarkerOptions()
            .position(LatLng(bike.latitude, bike.longitude))
            .title(bike.nome)
            .snippet(criaDescricaoParque(bike))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        val m = map!!.addMarker(marker)
        Log.i("marker_adicionado", marker.toString())
    }

    fun criaDescricaoParque(bike : Bikes) : String {
        var descricao : String = ""
        descricao += getString(R.string.bicicletas_discponiveis) + bike.numBikes + "\n"
        descricao += getString(R.string.bicicletas_total) + bike.numDocas + "\n"

        return descricao
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

    private fun mapInfoWindow(){
        map!!.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
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

    override fun onLocationChanged(locastionResult: LocationResult) {
        val location = locastionResult.lastLocation
        this.location = location
        if (this.location == null) {
            Log.i("muda = null", "teste")
            location.let { this.location = it }
            zoomMyLocation()
        } else if ((this.location!!.latitude != location.latitude) ||
            (this.location!!.longitude != location.longitude)
        ) {
            Log.i("muda", "teste")
            location.let { this.location = it }
            markerAllBikes()
            zoomMyLocation()
        } else {
            Log.i("igual", "teste")
        }
        Log.i("localizacaoMap", location.toString())
    }




}