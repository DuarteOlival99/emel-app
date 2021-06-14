package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.example.projecto_02.R
import kotlinx.android.synthetic.main.fragment_contacts_postos.*
import kotlinx.android.synthetic.main.fragment_contacts_postos.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.accelerometer.Accelerometer
import java.util.*

class ContactsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contacts_postos, container, false)
        ButterKnife.bind(this, view)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Accelerometer.start(activity!!.applicationContext)
    }

    override fun onStart() {

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
                view!!.loja_laranjeiras.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.loja_laranjeiras_endereco.setTextColor(resources.getColor(R.color.textDark))
                view!!.loja_laranjeiras_horario.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.loja_laranjeiras_horario1.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.loja_saldanha.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.loja_saldanha_endereco.setTextColor(resources.getColor(R.color.textDark))
                view!!.loja_saldanha_horario.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.loja_lisboa.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.loja_lisboa_endereco.setTextColor(resources.getColor(R.color.textDark))
                view!!.loja_lisboa_horario.setTextColor(resources.getColor(R.color.textTitleDark))
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                view!!.setBackgroundColor(resources.getColor(R.color.backgroundLight))
                view!!.loja_laranjeiras.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.loja_laranjeiras_endereco.setTextColor(resources.getColor(R.color.textLight))
                view!!.loja_laranjeiras_horario.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.loja_laranjeiras_horario1.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.loja_saldanha.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.loja_saldanha_endereco.setTextColor(resources.getColor(R.color.textLight))
                view!!.loja_saldanha_horario.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.loja_lisboa.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.loja_lisboa_endereco.setTextColor(resources.getColor(R.color.textLight))
                view!!.loja_lisboa_horario.setTextColor(resources.getColor(R.color.textTitleLight))
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }
        super.onStart()
    }

    @Optional
    @OnClick( R.id.loja_saldanha_endereco)
    fun onClickLojaSaldanha(view: View){
        val gmmIntentUri: Uri = Uri.parse("geo:0,0?q=${loja_saldanha_endereco.text}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    @Optional
    @OnClick( R.id.loja_laranjeiras_endereco)
    fun onClickLojaLaranjeiras(view: View){
        val gmmIntentUri: Uri = Uri.parse("geo:0,0?q=${loja_laranjeiras_endereco.text}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    @Optional
    @OnClick( R.id.loja_lisboa_endereco)
    fun onClickLojaLisboa(view: View){
        val gmmIntentUri: Uri = Uri.parse("geo:0,0?q=${loja_lisboa_endereco.text}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }




}
