package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.BatteryManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.DialogFragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.projecto_02.R
import kotlinx.android.synthetic.main.filter_pop_up_parque.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.sensors.accelerometer.Accelerometer
import java.util.*


class FilterDialogFragment : DialogFragment() {


    var preferences: SharedPreferences? = null
    var distance : Double = 0.0
    private lateinit var seekBar: SeekBar
    lateinit var spinnerTipo: Spinner
    lateinit var spinnerEstado: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("onCreateView", "Filtros")
        val view = inflater.inflate(R.layout.filter_pop_up_parque, container, false)
        preferences =  PreferenceManager.getDefaultSharedPreferences(context)

        spinnerTipo = view!!.drop_down_tipo_parque
        val tiposPark = resources.getStringArray(R.array.tipo_parque)

        spinnerEstado = view!!.drop_down_estado_parque
        val estadosPark = resources.getStringArray(R.array.estado_parque)

        Log.i("TIPOSPARK", Arrays.toString(tiposPark))

        if (spinnerTipo != null) {
            val adapter = ArrayAdapter(context!!,
                android.R.layout.simple_spinner_item, tiposPark)
            spinnerTipo.adapter = adapter
            spinnerTipo.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View?, position: Int, id: Long) {
//                    Toast.makeText(context,
//                        "Drop down " +
//                                "" + languages[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        val tipoParkAtual = preferences!!.getString("TIPOPARQUE", "Todos")
        var indexTipo: Int = 0
        Log.i("tipoParkAtual", tipoParkAtual)
        when(tipoParkAtual) {
            "Todos" -> indexTipo = 0
            "Superfície" -> indexTipo = 1
            "Estrutura" -> indexTipo = 2
        }
        spinnerTipo.setSelection(indexTipo)

        if (spinnerEstado != null) {
            val adapter1 = ArrayAdapter(context!!,
                android.R.layout.simple_spinner_item, estadosPark)
            spinnerEstado.adapter = adapter1
            spinnerEstado.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View?, position: Int, id: Long) {
//                    Toast.makeText(context,
//                        "Drop down " +
//                                "" + languages[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        val estadosParkAtual = preferences!!.getString("ESTADOPARQUE", "Todos")
        Log.i("estadosRecebido", estadosParkAtual)
        var indexEstados: Int = 0
        when(estadosParkAtual) {
            "Todos" -> indexEstados = 0
            "livre" -> indexEstados = 1
            "parcial" -> indexEstados = 2
        }

        spinnerEstado.setSelection(indexEstados)


        seekBar = view.seek_bar
        Log.i("DISTANCIA", preferences!!.getInt("DISTANCIA",0).toString())
        seekBar.progress = preferences!!.getInt("DISTANCIA",0)
        if(seekBar.progress == 0){
            distance = 0.0
            view.info_seek_bar.text = getString(R.string.todos_tipos_parques)
        }else{
            distance = seekBar.progress.toDouble()
            distance /= 10.0
            view.info_seek_bar.text = "${getString(R.string.ate)} ${distance} Km"
        }
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress == 0){
                    distance = 0.0
                    view.info_seek_bar.text = getString(R.string.todos_tipos_parques)
                }else{
                    distance = progress.toDouble()
                    distance /= 10.0
                    view.info_seek_bar.text = "${getString(R.string.ate)} ${distance} Km"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        ButterKnife.bind(this,view)

        return view
    }

    override fun onDestroy() {
        Log.i("onDestroy", "evocadoFiltros")
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onStart() {
        Log.i("OnStart", "OnStartFiltros")
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.apply {
            Log.i("OnStart", "OnStartFiltros1")
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
                Log.i("OnStart", "OnStartFiltros2")
                view?.setBackgroundColor(resources.getColor(R.color.filtrosDark))
            } else {
                view?.setBackgroundColor(resources.getColor(R.color.filtrosLight))
            }
        }
        Log.i("OnStart", "OnStartFiltros3")
        Accelerometer.start(activity!!.applicationContext)
        super.onStart()
    }

    override fun onResume() {
        Log.i("onResume", "onResumeFiltros")
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = RelativeLayout.LayoutParams.MATCH_PARENT
        params?.height = RelativeLayout.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        Log.i("onResume", "onResumeFiltros1")
    }

    @OnClick(R.id.button_filtrar)
    fun onClickFilter(view: View) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        editor
            .putInt("DISTANCIA", (distance*10).toInt())
            .apply()
        Log.i("DISTANCIA", (distance*10).toInt().toString())

        //Tipo de parque
        Log.i("spinnerTipo", spinnerTipo.selectedItem.toString())
        when(spinnerTipo.selectedItem.toString()) {
            getText(R.string.todos_tipos_parques).toString() -> editor.putString("TIPOPARQUE", "Todos").apply()
            getText(R.string.superfice_popup).toString() -> editor.putString("TIPOPARQUE", "Superfície").apply()
            getText(R.string.estrutura_popup).toString() -> editor.putString("TIPOPARQUE", "Estrutura").apply()
        }

        //Estado do parque
        Log.i("spinnerEstado", spinnerEstado.selectedItem.toString())
        when(spinnerEstado.selectedItem.toString()) {
            getText(R.string.todos_tipos_parques).toString() -> editor.putString("ESTADOPARQUE", "Todos").apply()
            getText(R.string.livre).toString() -> editor.putString("ESTADOPARQUE", "livre").apply()
            getText(R.string.potencialmente_lotado).toString() -> editor.putString("ESTADOPARQUE", "parcial").apply()
        }

        dismiss()
    }

    override fun dismiss() {
        Log.i("dismiss", "dismissFiltros")
        val parentFrag: ParquesFragment = this.parentFragment as ParquesFragment
        parentFrag.atualizaListaParques()
        super.dismiss()
    }



}