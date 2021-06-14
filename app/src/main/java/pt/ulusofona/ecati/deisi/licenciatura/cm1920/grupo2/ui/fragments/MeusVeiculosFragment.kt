package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.BatteryManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.example.projecto_02.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_meus_veiculos.*
import kotlinx.android.synthetic.main.new_veiculo_pop_up.*
import kotlinx.android.synthetic.main.new_veiculo_pop_up.view.*
import kotlinx.android.synthetic.main.veiculo_expression.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Veiculo
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.adapters.MeusVeiculosAdapter
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.listeners.OnVeiculoAdd
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.MeusVeiculosViewModel
import java.util.*


class MeusVeiculosFragment : Fragment(), OnVeiculoAdd {
    private val TAG = MeusVeiculosFragment::class.java.simpleName
    private lateinit var viewModel: MeusVeiculosViewModel
    private var listMeusVeiculos = mutableListOf<Veiculo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_meus_veiculos, container, false)
        viewModel = ViewModelProviders.of(this).get(MeusVeiculosViewModel::class.java)
        listaMeusVeiculos()
        ButterKnife.bind(this, view)
        return view

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MeusVeiculosViewModel::class.java)

    }

    override fun onDestroyView() {
        Log.i("onDestroy", "foi evocado")
        super.onDestroyView()

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

            } else {
                view!!.setBackgroundColor(resources.getColor(R.color.backgroundLight))
            }
        }

        viewModel.registerListener(this)
        listaMeusVeiculos()
        swipeDeleteVeiculo()
        swipeEditVeiculo()



        super.onStart()
    }

    override fun onDestroy() {
        viewModel.unregisterListener()
        super.onDestroy()
    }


    override fun onVeiculoAdd(list: MutableList<Veiculo>) {
        list?.let { listMeusVeiculos = it }
        Log.i("onVeiculoAdd", listMeusVeiculos.toString())
        listaMeusVeiculos()
    }

    private fun swipeDeleteVeiculo(){

        var removedPosition: Int = 0
        var removedItem: Veiculo = Veiculo("", "", "", "")
        var deleteIcon: Drawable = ContextCompat.getDrawable(activity as Context, R.drawable.ic_delete_swipe)!!
        var swipeBackground : ColorDrawable = ColorDrawable(Color.parseColor("#C30C0C")) // define o background para vermelho

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                //Toast.makeText(context, "Veiculo removida", Toast.LENGTH_SHORT).show()

                removedItem = listMeusVeiculos[viewHolder.adapterPosition]
                removedPosition = viewHolder.adapterPosition

                viewModel.deleteVeiculo(removedItem, removedPosition)//remove da base de dados local
                listaMeusVeiculos()
                Snackbar.make(requireView(), "Veiculo com a matricula ${removedItem.matricula} apagado", Snackbar.LENGTH_LONG).setAction(getString(R.string.undo)) {
                    viewModel.newPositionVeiculo(removedPosition, removedItem)//adiciona de novo o veiculo na posicao que estava anteriormente
                    listaMeusVeiculos()//volta a desenhar a lista
                }.show()
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

                desenhaIconsEBackgroundSwipe(viewHolder,deleteIcon, swipeBackground, dX, dY, c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(list_meus_veiculos)

    }

    private fun swipeEditVeiculo(){

        var editIcon: Drawable = ContextCompat.getDrawable(activity as Context, R.drawable.ic_edit_swipe)!!
        var swipeBackground : ColorDrawable = ColorDrawable(resources.getColor(R.color.colorPrimary)) // define o background para vermelho

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {

               // Toast.makeText(context, "Editar veiculo", Toast.LENGTH_SHORT).show()
                val uuid = viewHolder.itemView.veiculo_uuid_text.text
                val marca = viewHolder.itemView.veiculo_marca_text.text
                val matricula =viewHolder.itemView.veiculo_matricula_text.text
                val modelo = viewHolder.itemView.veiculo_modelo_text.text
                val data_de_matricula =viewHolder.itemView.veiculo_data_matricula_text.text
                val posicao = viewHolder.adapterPosition


                //Log.i("editar", viewHolder.adapterPosition.toString())

                val dialogView = LayoutInflater.from(context).inflate(R.layout.new_veiculo_pop_up, null)

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
                        dialogView.popup_titulo.setTextColor(resources.getColor(R.color.textTitleDark))
                        //muda campo marca para dark mode
                        dialogView.campo_marca.campo_marca_edit_text.setHintTextColor(resources.getColor(R.color.textTitleDark))
                        dialogView.campo_marca.campo_marca_edit_text.setTextColor(resources.getColor(R.color.textTitleDark))
                        //muda campo matricula para dark mode
                        dialogView.campo_matricula.campo_matricula_edit_text.setHintTextColor(resources.getColor(R.color.textTitleDark))
                        dialogView.campo_matricula.campo_matricula_edit_text.setTextColor(resources.getColor(R.color.textTitleDark))
                        //muda campo modelo para dark mode
                        dialogView.campo_modelo.campo_modelo_edit_text.setHintTextColor(resources.getColor(R.color.textTitleDark))
                        dialogView.campo_modelo.campo_modelo_edit_text.setTextColor(resources.getColor(R.color.textTitleDark))
                        //muda campo data de matriculapara dark mode
                        dialogView.campo_data_matricula.campo_data_matricula_edit_text.setHintTextColor(resources.getColor(R.color.textTitleDark))
                        dialogView.campo_data_matricula.campo_data_matricula_edit_text.setTextColor(resources.getColor(R.color.textTitleDark))
                    } else {
                        //muda background para Light mode
                        dialogView.setBackgroundColor(resources.getColor(R.color.backgroundLight))
                        //muda titulo para Light mode
                        dialogView.popup_titulo.setTextColor(resources.getColor(R.color.textTitleLight))
                        //muda campo marca para Light mode
                        dialogView.campo_marca.campo_marca_edit_text.setHintTextColor(resources.getColor(R.color.textTitleLight))
                        dialogView.campo_marca.campo_marca_edit_text.setTextColor(resources.getColor(R.color.textTitleLight))
                        //muda campo matricula para Light mode
                        dialogView.campo_matricula.campo_matricula_edit_text.setHintTextColor(resources.getColor(R.color.textTitleLight))
                        dialogView.campo_matricula.campo_matricula_edit_text.setTextColor(resources.getColor(R.color.textTitleLight))
                        //muda campo modelo para Light mode
                        dialogView.campo_modelo.campo_modelo_edit_text.setHintTextColor(resources.getColor(R.color.textTitleLight))
                        dialogView.campo_modelo.campo_modelo_edit_text.setTextColor(resources.getColor(R.color.textTitleLight))
                        //muda campo data de matriculapara Light mode
                        dialogView.campo_data_matricula.campo_data_matricula_edit_text.setHintTextColor(resources.getColor(R.color.textTitleLight))
                        dialogView.campo_data_matricula.campo_data_matricula_edit_text.setTextColor(resources.getColor(R.color.textTitleLight))
                    }
                }

                val mBuilder = AlertDialog.Builder(context)
                    .setView(dialogView)

                dialogView.campo_marca.campo_marca_edit_text.setText(marca)
                dialogView.campo_modelo.campo_modelo_edit_text.setText(modelo)
                dialogView.campo_matricula.campo_matricula_edit_text.setText(matricula)
                dialogView.campo_data_matricula.campo_data_matricula_edit_text.setText(data_de_matricula)

                val mAlertDialog = mBuilder.show()

                mAlertDialog.campo_data_matricula_edit_text.setOnClickListener {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)

                    //create dialog
                    val datePicker = DatePickerDialog(context as Activity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                        Log.i("data", "" + dayOfMonth + "/" + (month + 1) + "/" + year )
                        mAlertDialog.campo_data_matricula_edit_text.setText("" + dayOfMonth + "/" + (month + 1) + "/" + year)

                    },year,month,day)

                    datePicker.show()
                }

                mAlertDialog.button_cancel.setOnClickListener {
                    listaMeusVeiculos()
                    mAlertDialog.dismiss()
                }
                mAlertDialog.button_confirmar.setOnClickListener {
                    val marcaFinal = mAlertDialog.campo_marca.campo_marca_edit_text.text.toString()
                    val modeloFinal = mAlertDialog.campo_modelo.campo_modelo_edit_text.text.toString()
                    val matriculaFinal = mAlertDialog.campo_matricula.campo_matricula_edit_text.text.toString()
                    val dataMatriculoFinal = mAlertDialog.campo_data_matricula.campo_data_matricula_edit_text.text.toString()

                    viewModel.updateVeiculoEspecifico(uuid, marcaFinal, modeloFinal, matriculaFinal, dataMatriculoFinal, posicao)
                    //Log.i("editar", posicao.toString())

                    listaMeusVeiculos()
                    mAlertDialog.dismiss()
                    //listaMeusVeiculos()
                }

                //listaMeusVeiculos()
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

                desenhaIconsEBackgroundSwipe(viewHolder ,editIcon, swipeBackground, dX, dY, c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(list_meus_veiculos)

    }



    @Optional
    @OnClick( R.id.fabNewVeiculo)
    fun onClickNewVeiculo(view: View){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.new_veiculo_pop_up, null)

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
                dialogView.popup_titulo.setTextColor(resources.getColor(R.color.textTitleDark))
                //muda campo marca para dark mode
                dialogView.campo_marca.campo_marca_edit_text.setHintTextColor(resources.getColor(R.color.textTitleDark))
                dialogView.campo_marca.campo_marca_edit_text.setTextColor(resources.getColor(R.color.textTitleDark))
                //muda campo matricula para dark mode
                dialogView.campo_matricula.campo_matricula_edit_text.setHintTextColor(resources.getColor(R.color.textTitleDark))
                dialogView.campo_matricula.campo_matricula_edit_text.setTextColor(resources.getColor(R.color.textTitleDark))
                //muda campo modelo para dark mode
                dialogView.campo_modelo.campo_modelo_edit_text.setHintTextColor(resources.getColor(R.color.textTitleDark))
                dialogView.campo_modelo.campo_modelo_edit_text.setTextColor(resources.getColor(R.color.textTitleDark))
                //muda campo data de matriculapara dark mode
                dialogView.campo_data_matricula.campo_data_matricula_edit_text.setHintTextColor(resources.getColor(R.color.textTitleDark))
                dialogView.campo_data_matricula.campo_data_matricula_edit_text.setTextColor(resources.getColor(R.color.textTitleDark))
            } else {
                //muda background para Light mode
                dialogView.setBackgroundColor(resources.getColor(R.color.backgroundLight))
                //muda titulo para Light mode
                dialogView.popup_titulo.setTextColor(resources.getColor(R.color.textTitleLight))
                //muda campo marca para Light mode
                dialogView.campo_marca.campo_marca_edit_text.setHintTextColor(resources.getColor(R.color.textTitleLight))
                dialogView.campo_marca.campo_marca_edit_text.setTextColor(resources.getColor(R.color.textTitleLight))
                //muda campo matricula para Light mode
                dialogView.campo_matricula.campo_matricula_edit_text.setHintTextColor(resources.getColor(R.color.textTitleLight))
                dialogView.campo_matricula.campo_matricula_edit_text.setTextColor(resources.getColor(R.color.textTitleLight))
                //muda campo modelo para Light mode
                dialogView.campo_modelo.campo_modelo_edit_text.setHintTextColor(resources.getColor(R.color.textTitleLight))
                dialogView.campo_modelo.campo_modelo_edit_text.setTextColor(resources.getColor(R.color.textTitleLight))
                //muda campo data de matriculapara Light mode
                dialogView.campo_data_matricula.campo_data_matricula_edit_text.setHintTextColor(resources.getColor(R.color.textTitleLight))
                dialogView.campo_data_matricula.campo_data_matricula_edit_text.setTextColor(resources.getColor(R.color.textTitleLight))
            }
        }

        val mBuilder = AlertDialog.Builder(context)
            .setView(dialogView)

        val mAlertDialog = mBuilder.show()

        mAlertDialog.campo_data_matricula_edit_text.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            //create dialog
            val datePicker = DatePickerDialog(context as Activity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                Log.i("data", "" + dayOfMonth + "/" + (month + 1) + "/" + year )
                mAlertDialog.campo_data_matricula_edit_text.setText("" + dayOfMonth + "/" + (month + 1) + "/" + year)

            },year,month,day)

            datePicker.show()
        }

        mAlertDialog.button_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }

        mAlertDialog.button_confirmar.setOnClickListener {

            val marca = mAlertDialog.campo_marca.campo_marca_edit_text.text.toString()
            val modelo = mAlertDialog.campo_modelo.campo_modelo_edit_text.text.toString()
            val matricula = mAlertDialog.campo_matricula.campo_matricula_edit_text.text.toString()
            val dataMatriculo = mAlertDialog.campo_data_matricula.campo_data_matricula_edit_text.text.toString()

            if(marca.isEmpty() || modelo.isEmpty() || matricula.isEmpty() || dataMatriculo.isEmpty()){
                if(marca.isEmpty()){
                    mAlertDialog.campo_marca_edit_text.error = "campo marca vazio"
                }
                if(modelo.isEmpty()){
                    mAlertDialog.campo_modelo_edit_text.error = "campo modelo vazio"
                }
                if(matricula.isEmpty()){
                    mAlertDialog.campo_matricula_edit_text.error = "campo matricula vazio"
                }else if(!isMatriculaRegex(matricula)){
                    mAlertDialog.campo_matricula_edit_text.error = "Matricula introduzida invalida"
                }
                if(dataMatriculo.isEmpty()){
                    mAlertDialog.campo_data_matricula_edit_text.error = "campo data matricula vazio"
                }
            }else{
                if(isMatriculaRegex(matricula)){
                    val veiculo : Veiculo = Veiculo(marca, matricula.toUpperCase(), modelo, dataMatriculo)
                    viewModel.newVeiculo(veiculo)
                    listaMeusVeiculos()
                    mAlertDialog.dismiss()
                }else {
                    mAlertDialog.campo_matricula_edit_text.error = "Matricula introduzida invalida"
                }
            }

        }
        //showPopup(view)
    }

    fun isMatriculaRegex(matricula: String): Boolean {
        val pattern1 = Regex("[a-zA-Z]{2}\\-\\d{2}\\-\\d{2}") //valida 1 matricula existente AA-00-00
        val pattern2 = Regex("\\d{2}\\-\\d{2}\\-[a-zA-Z]{2}")//valida 2 matricula existente 00-00-AA
        val pattern3 = Regex("\\d{2}\\-[a-zA-Z]{2}\\-\\d{2}")//valida 3 matricula existente 00-AA-00
        val pattern4 = Regex("[a-zA-Z]{2}\\-\\d{2}\\-[a-zA-Z]{2}")//valida 4 matricula existente AA-00-AA

        return pattern1.containsMatchIn(matricula.toString()) ||
                pattern2.containsMatchIn(matricula.toString()) ||
                pattern3.containsMatchIn(matricula.toString()) ||
                pattern4.containsMatchIn(matricula.toString())
    }

    fun showPopup(view: View) {
        val popupMenu = PopupMenu(activity as Context, view)
        popupMenu.setOnMenuItemClickListener {item ->
            when (item.itemId){
                R.id.postos_atendimento -> {
                    Toast.makeText(activity as Context, "1", Toast.LENGTH_LONG).show()
                    true
                }
                R.id.contactos_gerais -> {
                    Toast.makeText(activity as Context, "2", Toast.LENGTH_LONG).show()
                    true
                }
                R.id.bloqueio_veiculos -> {
                    Toast.makeText(activity as Context, "3", Toast.LENGTH_LONG).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.inflate(R.menu.menu_contacts)
        popupMenu.show()
    }

    fun listaMeusVeiculos() {
        listMeusVeiculos = viewModel.getVeiculos()
        Log.i("teste", listMeusVeiculos.toString())
        desenhaVeiculosAdapter()
    }

    fun desenhaVeiculosAdapter() {
        Log.i("desenhaVeiculosAdapter", listMeusVeiculos.toString())
        list_meus_veiculos?.layoutManager = LinearLayoutManager(activity as Context)
        list_meus_veiculos?.adapter =
            MeusVeiculosAdapter(
                viewModel,
                activity as Context,
                R.layout.veiculo_expression,
                listMeusVeiculos
            )
    }

    fun desenhaIconsEBackgroundSwipe(viewHolder: RecyclerView.ViewHolder, Icon: Drawable, swipeBackground : ColorDrawable, dX: Float, dY: Float,c: Canvas) {

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

}