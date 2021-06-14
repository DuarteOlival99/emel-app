package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments

import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.projecto_02.R
import kotlinx.android.synthetic.main.fragment_opcoes.*
import kotlinx.android.synthetic.main.fragment_opcoes.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.NavigationManager
import java.util.*


class OpcoesFragment : Fragment() {


    override fun onStart() {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.apply {
            val dark = getBoolean("NOTURNO", false)
            val shake = getBoolean("SHAKE", true)
            val darkAuto = getBoolean("DARK", true)
            switchButtonNoturno?.isChecked = dark
            switchButtonShake?.isChecked = shake
            switchButtondark?.isChecked = darkAuto

            val c = Calendar.getInstance()
            //entre as 20 e 7
            val hour = c.get(Calendar.HOUR_OF_DAY)
            //recebe bateria
            val bm =
                context!!.getSystemService(BATTERY_SERVICE) as BatteryManager?
            val batLevel = bm!!.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            if(dark || ((((hour in 20..24) || (hour in 0..7)) || batLevel <= 20) && darkAuto) ) {
                view!!.setBackgroundColor(resources.getColor(R.color.backgroundDark))
                view!!.textNoturno.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.textShake.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.textDark.setTextColor(resources.getColor(R.color.textTitleDark))

                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                view!!.setBackgroundColor(resources.getColor(R.color.backgroundLight))
                view!!.textNoturno.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.textShake.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.textDark.setTextColor(resources.getColor(R.color.textTitleLight))

                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }

        super.onStart()
    }

    @OnClick( R.id.switchButtonNoturno, R.id.switchButtonShake, R.id.switchButtondark)
    fun saveData(v: View) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()

        editor
            .putBoolean("NOTURNO", switchButtonNoturno.isChecked)
            .putBoolean("SHAKE", switchButtonShake.isChecked)
            .putBoolean("DARK", switchButtondark.isChecked)
            .apply()
        //val toast = makeText(context, "Opção Guardada", LENGTH_LONG).show()
    }

    private fun restartApp() {
        fragmentManager?.let { NavigationManager.goToOpcoes(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.fragment_opcoes, container, false)
            ButterKnife.bind(this, view)
            return view
    }

    companion object {
    }
}