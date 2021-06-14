package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.example.projecto_02.R
import com.google.android.gms.location.LocationResult
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotResponse
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location.FusedLocation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location.OnLocationChangedListener
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.NavigationManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.SplashScreenViewModel


class SplashScreenFragment : PermissionedFragment(REQUEST_CODE), OnLocationChangedListener {
    lateinit var viewModel: SplashScreenViewModel
    lateinit var pref: SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    private var listPark = listOf<ParkingLotResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("ONSTART","SPLASH12")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        viewModel = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        Log.i("ONSTART","SPLASH")
        viewModel.registerListener()
        val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        Log.i("connectividade", isConnected.toString())

        if (isConnected){
            viewModel.getParkingLotsConnection()
            viewModel.getParkingBikeLotsConnection()

        }else{
            viewModel.getParkingLotsNoConnection()
            viewModel.getParkingBikeLotsNoConnection()
        }

        viewModel.updateVeiculos()
        viewModel.updateLugar()

        val background = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(2500)
                    //fragmentManager?.let { NavigationManager.goToParquesFragment(it) }
                }catch (e : Exception) {
                    e.printStackTrace()
                }
                super.run()
            }
        }
        background.start()
        super.onRequestPermissions(
            activity?.baseContext!!, arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.hide() // retira a tollbar da pag
    }

    override fun onRequestPermissionSucces() {
        Log.i("Mapa", "teste")
        FusedLocation.registerListener(this)
        fragmentManager?.let { NavigationManager.goToParquesFragment(it) }
    }

    override fun onRequestPermissionFailure() {
        Log.i("localizacao_parques", "erro")
    }

    override fun onLocationChanged(locastionResult: LocationResult) {
        Log.i("location", "locationUpdate")
    }


}