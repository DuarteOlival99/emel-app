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
import kotlinx.android.synthetic.main.fragment_contacts_bloqueio.view.horario
import kotlinx.android.synthetic.main.fragment_contacts_geral.*
import kotlinx.android.synthetic.main.fragment_contacts_geral.view.*
import java.util.*

class ContactosGeraisFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contacts_geral, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                view!!.telefone_gerais.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.phone_contactos_gerais.setTextColor(resources.getColor(R.color.textDark))
                view!!.fax.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.fax1.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.horario.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.horario1.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.horario2.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.horario3.setTextColor(resources.getColor(R.color.textTitleDark))

                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                view!!.setBackgroundColor(resources.getColor(R.color.backgroundLight))
                view!!.telefone_gerais.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.phone_contactos_gerais.setTextColor(resources.getColor(R.color.textLight))
                view!!.fax.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.fax1.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.horario.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.horario1.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.horario2.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.horario3.setTextColor(resources.getColor(R.color.textTitleLight))

                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }
        super.onStart()
    }

    @Optional
    @OnClick(R.id.phone_contactos_gerais)
    fun onClickPhoneGeral(view : View){

        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${phone_contactos_gerais.text}")
        startActivity(intent)

    }

}