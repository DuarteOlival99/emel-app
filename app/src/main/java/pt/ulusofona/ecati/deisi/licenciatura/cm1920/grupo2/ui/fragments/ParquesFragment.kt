package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.example.projecto_02.R
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.fragment_parques.*
import kotlinx.android.synthetic.main.parque_item.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.accelerometer.Accelerometer
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.accelerometer.OnShakeListener
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location.FusedLocation
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.location.OnLocationChangedListener
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.adapters.ParquesAdapter
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.listeners.OnParquesAdd
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.Extensions
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.ParqueDetailViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.ParquesViewModel
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.SplashScreenViewModel
import java.util.*

class ParquesFragment : PermissionedFragment(REQUEST_CODE), OnParquesAdd, OnLocationChangedListener, OnShakeListener {
    private val TAG = MeusVeiculosFragment::class.java.simpleName
    lateinit var viewModel: ParquesViewModel
    lateinit var viewModelParques: SplashScreenViewModel
    lateinit var viewModelParqueDetailViewModel: ParqueDetailViewModel
    private var listParques = listOf<Parque>()
    lateinit var pref: SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    lateinit var location : Location
    val extensions = Extensions()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.i("onCreateView", "Parques")
        val view = inflater.inflate(R.layout.fragment_parques, container, false)
        viewModel = ViewModelProviders.of(this).get(ParquesViewModel::class.java)
        viewModelParques = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        viewModelParqueDetailViewModel = ViewModelProviders.of(this).get(ParqueDetailViewModel::class.java)
        ButterKnife.bind(this, view)
        return view

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ParquesViewModel::class.java)
        viewModelParqueDetailViewModel = ViewModelProviders.of(this).get(ParqueDetailViewModel::class.java)
        FusedLocation.start(activity!!.applicationContext)
        Accelerometer.start(activity!!.applicationContext)

    }

    override fun onStart() {
        Log.i("OnStart", "OnStartParques")
        pref = PreferenceManager.getDefaultSharedPreferences(context)
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

            } else {
                view!!.setBackgroundColor(resources.getColor(R.color.backgroundLight))
            }
        }
        (activity as AppCompatActivity).supportActionBar!!.show() // volta a adicionar a toolbar a pag
        val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        FusedLocation.registerListener(this)

        Log.i("connectividade", isConnected.toString())

        if (isConnected){
            viewModelParques.getParkingLotsConnection()
        }else{
            Toast.makeText(context, getText(R.string.sem_conexao), Toast.LENGTH_LONG).show()
            Log.i("SemNet", "usa o que foi buscar no splash")
            Log.i("1", "1")
        }
        viewModel.updateDistance()
        Log.i("2", "2")
        listParques = viewModel.getParks()
        Log.i("3", "3")
        viewModel.registerListener(this)
        Accelerometer.registerListener(this)
        pref = PreferenceManager.getDefaultSharedPreferences(context)
        atualizaListaParques()
        swipeToMap()

        super.onStart()
    }

    override fun onRequestPermissionSucces() {
        Log.i("Mapa", "teste")
        FusedLocation.registerListener(this)
        viewModel.updateDistance()
        atualizaListaParques()
    }

    override fun onRequestPermissionFailure() {
        Log.i("localizacao_parques", "erro")
    }

    fun validarLocalizacao() {
        //implementar msg de erro
        if (!viewModel.getPossibleLocation()) {
            Log.i("localizacao_parques", "erro")
            val builder = AlertDialog.Builder(context!!)
            builder.setMessage(getString(R.string.popup_parques_message))
                .setPositiveButton(getString(R.string.ok),
                    DialogInterface.OnClickListener { dialog, id ->
                        // User set ok to dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()
        }
    }

    override fun onDestroy() {
        Log.i("onDestroy", "evocado")
        viewModel.unregisterListener()
        super.onDestroy()
    }

    
    @Optional
    @OnClick( R.id.fabFilters)
    fun onClickFilters(view: View){

        fragmentManager?.let { FilterDialogFragment().show(childFragmentManager, "filters") }

    }


    fun atualizaListaParques() {
        pref.apply {
            val lotacao = getString("ESTADOPARQUE", "Todos")
            val tipoParque = getString("TIPOPARQUE", "Todos")
            var distancia = getInt("DISTANCIA", 0)

            distancia /= 10

            validarLocalizacao()
            listaParques(tipoParque!!, lotacao!!, distancia)
            Log.i("ListaParques2", "$lotacao $tipoParque $distancia")
        }
    }




    fun listaParques(tipoParque: String, lotacao: String, distancia: Int) {
        viewModel.updateDistance()
        listParques = viewModel.getParques(tipoParque, lotacao, distancia)
        desenhaParquesAdapter()
    }

    fun desenhaParquesAdapter() {
        list_parques?.layoutManager = LinearLayoutManager(activity as Context)
        list_parques?.adapter =
            ParquesAdapter(
                viewModelParqueDetailViewModel,
                activity as Context,
                R.layout.parque_item,
                listParques
            )
    }

    override fun onParquesAdd(list: List<Parque>) {
        list.let { listParques = it }
    }

    private fun swipeToMap(){

        var mapIcon: Drawable = ContextCompat.getDrawable(activity as Context, R.drawable.ic_map_parques)!!
        var swipeBackground : ColorDrawable = ColorDrawable(Color.parseColor("#b0e0e6")) // define o background para azul claro


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                //  Toast.makeText(context, "Fui para o mapa", Toast.LENGTH_SHORT).show()

                pref = PreferenceManager.getDefaultSharedPreferences(context)
                pref.apply {
                    val tipoParque = getString("TIPOPARQUE", "Todos")
                    val lotacao = getString("ESTADOPARQUE", "Todos")
                    var distancia = getInt("DISTANCIA", 0)
                    distancia /= 10
                    listaParques(tipoParque!!,lotacao!!, distancia)
                }

                val gmmIntentUri = Uri.parse("google.navigation:q=${viewHolder.itemView.lugares_latitude.text},${viewHolder.itemView.lugares_longitude.text}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                desenhaIconsEBackgroundSwipe(viewHolder,mapIcon, swipeBackground, dX, dY, c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(list_parques)
    }

    fun desenhaIconsEBackgroundSwipe(viewHolder: RecyclerView.ViewHolder, Icon: Drawable, swipeBackground : ColorDrawable, dX: Float, dY: Float, c: Canvas) {

        val itemView = viewHolder.itemView // escolhe o item como sendo o card onde são definidos os elementos
        val iconMargin = ( itemView.height - Icon.intrinsicHeight ) / 2

        if(dX < 0) { //swipe left
            swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
            Icon.setBounds(itemView.right - iconMargin - Icon.intrinsicWidth, itemView.top + iconMargin, itemView.right - iconMargin, itemView.bottom - iconMargin)
        }else{ //swipe rigth
            swipeBackground.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
            Icon.setBounds(itemView.left + iconMargin, itemView.top + iconMargin, itemView.left + iconMargin + Icon.intrinsicWidth, itemView.bottom - iconMargin)
        }

        swipeBackground.draw(c) // desenha o background
        c.save()

        if (dX < 0) { //left
            c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        }else{ //rigth
            c.clipRect(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
        }
        Icon.draw(c) // desenha o icon delete
        c.restore()
    }

    override fun onLocationChanged(locastionResult: LocationResult) {
        location = locastionResult.lastLocation
        viewModel.updateDistance()
        if ( viewModel.getFirstTime() ) {
            atualizaListaParques()
        }
        Log.i("localizacaoParquesLogic", location.toString())
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

            atualizaListaParques()
            Toast.makeText(context, getText(R.string.limpar_filtros), Toast.LENGTH_LONG).show()
        }
    }


}