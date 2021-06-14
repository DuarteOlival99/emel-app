package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments

import android.app.AlertDialog
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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.example.projecto_02.R
import kotlinx.android.synthetic.main.fragment_contacts_bloqueio.*
import kotlinx.android.synthetic.main.fragment_contacts_bloqueio.view.*
import kotlinx.android.synthetic.main.list_veiculos.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Veiculo
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.adapters.VeiculosBloqueadoAdapter
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.MeusVeiculosViewModel
import java.util.*

class ContactosBlockVehiclesFragment : Fragment(){

    private lateinit var viewModel: MeusVeiculosViewModel
    private var listMeusVeiculos = mutableListOf<Veiculo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contacts_bloqueio, container, false)
        viewModel = ViewModelProviders.of(this).get(MeusVeiculosViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MeusVeiculosViewModel::class.java)
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
                view!!.telefone.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.phone_bloqueio_veiculo.setTextColor(resources.getColor(R.color.textDark))
                view!!.horario.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.horario_text.setTextColor(resources.getColor(R.color.textTitleDark))
                view!!.veiculo_contacto_bloqueado.setTextColor(resources.getColor(R.color.textDark))

                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                view!!.setBackgroundColor(resources.getColor(R.color.backgroundLight))
                view!!.telefone.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.phone_bloqueio_veiculo.setTextColor(resources.getColor(R.color.textLight))
                view!!.horario.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.horario_text.setTextColor(resources.getColor(R.color.textTitleLight))
                view!!.veiculo_contacto_bloqueado.setTextColor(resources.getColor(R.color.textLight))

                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }

        listMeusVeiculos = viewModel.getVeiculos()
        super.onStart()
    }


    @Optional
    @OnClick(R.id.phone_bloqueio_veiculo)
    fun onClickPhone(view : View){

        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${phone_bloqueio_veiculo.text}")
        startActivity(intent)

    }

    @Optional
    @OnClick(R.id.veiculo_contacto_bloqueado)
    fun onClickBloqueado(view: View) {

        if(listMeusVeiculos.size > 0) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.list_veiculos, null)

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
                    //muda background para dark mode
                    dialogView.setBackgroundColor(resources.getColor(R.color.backgroundDark))
                    //muda titulo para dark mode
                    dialogView.popup_titulo_bloqueado.setTextColor(resources.getColor(R.color.textTitleDark))
                }else{
                    //muda background para ligth mode
                    dialogView.setBackgroundColor(resources.getColor(R.color.backgroundLight))
                    //muda titulo para ligth mode
                    dialogView.popup_titulo_bloqueado.setTextColor(resources.getColor(R.color.textTitleLight))
                }
            }
            val mBuilder = AlertDialog.Builder(context)
                .setView(dialogView)

            dialogView.list_veiculos.layoutManager = LinearLayoutManager(activity as Context)
            dialogView.list_veiculos.adapter =
                VeiculosBloqueadoAdapter(
                    viewModel,
                    activity as Context,
                    R.layout.veiculo_expression_bloqueio,
                    listMeusVeiculos
                )
            val mAlertDialog = mBuilder.show()
        }else {
            val number = 3838
            val text = "Rebocado "

            val uri = Uri.parse("smsto:$number")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            intent.putExtra("sms_body", text)
            startActivity(intent)
        }




    }

}