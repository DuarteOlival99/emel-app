package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.domain.project

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import com.example.projecto_02.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.NavigationManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.MainViewModel
import java.util.*


class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener{
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var viewModel : MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getText(R.string.parques).toString()
        Log.i(TAG, "o metodo onCreate foi invocado")
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        //NavigationManager.goToSplashScreen(supportFragmentManager)
        Log.i(TAG, "o metodo onCreate foi invocado1")
        setSupportActionBar(toolbar)
        setupDrawerMenu()
        if (!screenRotated(savedInstanceState)) {
            Log.i(TAG, "o metodo onCreate foi invocado2")

            NavigationManager.goToSplashScreen(supportFragmentManager)
        }
    }

    private fun screenRotated(savedInstanceState: Bundle?) : Boolean {
        return savedInstanceState != null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        var inflater : MenuInflater = menuInflater

        inflater.inflate(R.menu.park_me_now_menu, menu)

        val item: MenuItem? = menu?.findItem(R.id.park_me_now)

        item?.actionView?.findViewById<Button>(R.id.appCompatTextView)?.setOnClickListener {

            val parque = viewModel.getParkMeNow()
            if( parque != null) {
                Log.i("ParkMeNow", parque.toString())
                val gmmIntentUri = Uri.parse("google.navigation:q=${parque.latitude},${parque.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)

            }else {
                Toast.makeText(this, "Park me now -> Não existe nenhum parque livre", Toast.LENGTH_SHORT).show()
            }

        }

        return super.onCreateOptionsMenu(menu)
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setupDrawerMenu()
        when(item.itemId) {
            R.id.nav_list -> {
                title = getText(R.string.parques).toString()
                NavigationManager.goToParquesFragment(
                    supportFragmentManager
                )
            }
            R.id.nav_meus_lugares -> {
                title = getText(R.string.meus_lugares).toString()
                NavigationManager.goToMeusLugaresFragment(
                    supportFragmentManager
                )
//                val intent = Intent(this, MyCameraActivity::class.java)
//                startActivity(intent)
            }
            R.id.nav_map -> {
                title = getText(R.string.map).toString()
                NavigationManager.goToMapFragment(
                    supportFragmentManager
                )
            }
            R.id.nav_map_bike -> {
                title = getText(R.string.map_bike).toString()
                NavigationManager.goToMapBicicletaFragment(
                    supportFragmentManager
                )
            }
            R.id.nav_meus_veiculos -> {
                title = getText(R.string.meus_veiculos).toString()
                NavigationManager.goToMeusVeiculosFragment(
                    supportFragmentManager
                )
            }
            R.id.postos_atendimento -> {
                title = getText(R.string.postos_de_atendimento).toString()
                NavigationManager.goToContactsFragmentAtendimento(
                    supportFragmentManager
                )
            }
            R.id.contactos_gerais -> {
                title = getText(R.string.contactos_gerais).toString()
                NavigationManager.goToContactsFragmentGeral(
                    supportFragmentManager
                )
            }
            R.id.bloqueio_veiculos -> {
                title = getText(R.string.bloqueio_de_veiculos).toString()
                NavigationManager.goToContactsFragmentBlock(
                    supportFragmentManager
                )
            }
            R.id.definicoes -> {
                title = getText(R.string.definicoes).toString()
                NavigationManager.goToOpcoes(
                    supportFragmentManager
                )
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }




    private fun setupDrawerMenu() {
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        nav_drawer.setNavigationItemSelectedListener(this)
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        pref.apply {
            val dark = getBoolean("NOTURNO", false)
            val darkAuto = getBoolean("DARK", true)
            val c = Calendar.getInstance()
            //entre as 20 e 7
            val hour = c.get(Calendar.HOUR_OF_DAY)
            //recebe bateria
            val bm =
                getSystemService(Context.BATTERY_SERVICE) as BatteryManager?
            val batLevel = bm!!.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            if(dark || ((((hour in 20..24) || (hour in 0..7)) || batLevel <= 20) && darkAuto) ) {
                nav_drawer.setBackgroundColor(resources.getColor(R.color.drawerDark))
                //nav_drawer.itemTextColor = ColorStateList.valueOf(resources.getColor(R.color.textTitleDark))
            } else {
                nav_drawer.setBackgroundColor(resources.getColor(R.color.drawerLight))
                //nav_drawer.itemTextColor = ColorStateList.valueOf(resources.getColor(R.color.textTitleLight))
            }
        }
        drawer.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }
        if (supportFragmentManager.backStackEntryCount == 1){
            finish()
        }
        super.onBackPressed()
    }

}
