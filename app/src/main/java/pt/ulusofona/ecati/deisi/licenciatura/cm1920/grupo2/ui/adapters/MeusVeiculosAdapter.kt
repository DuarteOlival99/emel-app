package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.adapters

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.BatteryManager
import android.preference.PreferenceManager
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projecto_02.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.new_veiculo_pop_up.*
import kotlinx.android.synthetic.main.new_veiculo_pop_up.view.*
import kotlinx.android.synthetic.main.veiculo_expression.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Veiculo
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.MeusVeiculosViewModel
import java.util.*

class MeusVeiculosAdapter (private var viewModel: MeusVeiculosViewModel, private val context: Context, private val layout: Int, private var
meusVeiculos: MutableList<Veiculo>) :
    RecyclerView.Adapter<MeusVeiculosAdapter.MeusVeiculosViewHolder>(){

    var removedPosition: Int = 0
    var removedItem: Veiculo = Veiculo("", "", "", "")
    private val requestSendSms = 2


    class MeusVeiculosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val uuid: TextView = view.veiculo_uuid_text
        val marca: TextView = view.veiculo_marca_text
        val matricula: TextView = view.veiculo_matricula_text
        val modelo: TextView = view.veiculo_modelo_text
        val data_matricula: TextView = view.veiculo_data_matricula_text
        val nome : TextView = view.nome_veiculo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeusVeiculosViewHolder {
            return MeusVeiculosViewHolder(
                LayoutInflater.from(context).inflate(layout, parent, false)
            )
    }

    override fun getItemCount(): Int {
      return meusVeiculos.size
    }

    override fun onBindViewHolder(holder: MeusVeiculosViewHolder, position: Int) {
        holder.uuid.text = meusVeiculos[position].uuid
        holder.marca.text = meusVeiculos[position].marca
        holder.matricula.text = meusVeiculos[position].matricula
        holder.modelo.text = meusVeiculos[position].modelo
        holder.data_matricula.text = meusVeiculos[position].dataMatricula
        holder.nome.text = holder.marca.text.toString() + " " + holder.modelo.text.toString()

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
               holder.itemView.veiculo_marca_text.setTextColor(context.resources.getColor(R.color.textDark))
                holder.itemView.veiculo_modelo_text.setTextColor(context.resources.getColor(R.color.textDark))
                holder.itemView.veiculo_matricula_text.setTextColor(context.resources.getColor(R.color.textDark))
                holder.itemView.veiculo_data_matricula_text.setTextColor(context.resources.getColor(R.color.textDark))

            } else {
                holder.itemView.veiculo_marca_text.setTextColor(context.resources.getColor(R.color.textLight))
                holder.itemView.veiculo_modelo_text.setTextColor(context.resources.getColor(R.color.textLight))
                holder.itemView.veiculo_matricula_text.setTextColor(context.resources.getColor(R.color.textLight))
                holder.itemView.veiculo_data_matricula_text.setTextColor(context.resources.getColor(R.color.textLight))
            }
        }



            val item = holder.itemView

        val info: Veiculo = meusVeiculos[position]
        val posicao: Int = position

        Log.i("Adapter", meusVeiculos.toString())

        //quando carrega no card para mostrar o resto da informacao
        item.cardView.setOnClickListener {
            extendeCard(holder)
        }
        //quando carrega na seta para mostrar o resto da informacao
        item.arrowBtn?.setOnClickListener {
            extendeCard(holder)
        }
        //quando carrega no icon de delete
        item.delete.setOnClickListener {
            meusVeiculos.removeAt(posicao)//remove da lista no adapter
            viewModel.deleteVeiculo(info, posicao)//remove da base de dados local
            notifyItemRemoved(posicao)

            Log.i("Adapter", "teste")
            var result = true

            Snackbar.make(
                holder.itemView,
                "Veiculo com a matricula ${info.matricula} apagado",
                Snackbar.LENGTH_LONG
            ).setAction(R.string.undo) {
                result = false
                meusVeiculos.add(posicao, info)//remove da lista no adapter
                viewModel.newPositionVeiculo(posicao, info)//remove da base de dados local
                notifyItemInserted(posicao)
                Log.i("Adapter1", "teste")

            }.show()


        }

        //quando carrega no botao de edit
        item.edit.setOnClickListener {
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
                    dialogView.setBackgroundColor(context.resources.getColor(R.color.backgroundDark))
                    //muda titulo para dark mode
                    dialogView.popup_titulo.setTextColor(context.resources.getColor(R.color.textTitleDark))
                    //muda campo marca para dark mode
                    dialogView.campo_marca.campo_marca_edit_text.setHintTextColor(context.resources.getColor(R.color.textTitleDark))
                    dialogView.campo_marca.campo_marca_edit_text.setTextColor(context.resources.getColor(R.color.textTitleDark))
                    //muda campo matricula para dark mode
                    dialogView.campo_matricula.campo_matricula_edit_text.setHintTextColor(context.resources.getColor(R.color.textTitleDark))
                    dialogView.campo_matricula.campo_matricula_edit_text.setTextColor(context.resources.getColor(R.color.textTitleDark))
                    //muda campo modelo para dark mode
                    dialogView.campo_modelo.campo_modelo_edit_text.setHintTextColor(context.resources.getColor(R.color.textTitleDark))
                    dialogView.campo_modelo.campo_modelo_edit_text.setTextColor(context.resources.getColor(R.color.textTitleDark))
                    //muda campo data de matriculapara dark mode
                    dialogView.campo_data_matricula.campo_data_matricula_edit_text.setHintTextColor(context.resources.getColor(R.color.textTitleDark))
                    dialogView.campo_data_matricula.campo_data_matricula_edit_text.setTextColor(context.resources.getColor(R.color.textTitleDark))
                } else {
                    //muda background para Light mode
                    dialogView.setBackgroundColor(context.resources.getColor(R.color.backgroundLight))
                    //muda titulo para Light mode
                    dialogView.popup_titulo.setTextColor(context.resources.getColor(R.color.textTitleLight))
                    //muda campo marca para Light mode
                    dialogView.campo_marca.campo_marca_edit_text.setHintTextColor(context.resources.getColor(R.color.textTitleLight))
                    dialogView.campo_marca.campo_marca_edit_text.setTextColor(context.resources.getColor(R.color.textTitleLight))
                    //muda campo matricula para Light mode
                    dialogView.campo_matricula.campo_matricula_edit_text.setHintTextColor(context.resources.getColor(R.color.textTitleLight))
                    dialogView.campo_matricula.campo_matricula_edit_text.setTextColor(context.resources.getColor(R.color.textTitleLight))
                    //muda campo modelo para Light mode
                    dialogView.campo_modelo.campo_modelo_edit_text.setHintTextColor(context.resources.getColor(R.color.textTitleLight))
                    dialogView.campo_modelo.campo_modelo_edit_text.setTextColor(context.resources.getColor(R.color.textTitleLight))
                    //muda campo data de matriculapara Light mode
                    dialogView.campo_data_matricula.campo_data_matricula_edit_text.setHintTextColor(context.resources.getColor(R.color.textTitleLight))
                    dialogView.campo_data_matricula.campo_data_matricula_edit_text.setTextColor(context.resources.getColor(R.color.textTitleLight))
                }
            }

            val mBuilder = AlertDialog.Builder(context)
                .setView(dialogView)

            val id = meusVeiculos[position].uuid
            dialogView.campo_marca.campo_marca_edit_text.setText(meusVeiculos[position].marca)
            dialogView.campo_modelo.campo_modelo_edit_text.setText(meusVeiculos[position].modelo)
            dialogView.campo_matricula.campo_matricula_edit_text.setText(meusVeiculos[position].matricula)
            dialogView.campo_data_matricula.campo_data_matricula_edit_text.setText(meusVeiculos[position].dataMatricula)

            val mAlertDialog = mBuilder.show()

            mAlertDialog.campo_data_matricula_edit_text.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                //create dialog
                val datePicker = DatePickerDialog(
                    context,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        Log.i("data", "" + dayOfMonth + "/" + (month + 1) + "/" + year)
                        mAlertDialog.campo_data_matricula_edit_text.setText("" + dayOfMonth + "/" + (month + 1) + "/" + year)
                    },
                    year,
                    month,
                    day
                )
                datePicker.show()
            }

            mAlertDialog.button_cancel.setOnClickListener {
                mAlertDialog.dismiss()
            }

            mAlertDialog.button_confirmar.setOnClickListener {
                meusVeiculos[position].marca =
                    mAlertDialog.campo_marca.campo_marca_edit_text.text.toString()
                meusVeiculos[position].modelo =
                    mAlertDialog.campo_modelo.campo_modelo_edit_text.text.toString()
                meusVeiculos[position].matricula =
                    mAlertDialog.campo_matricula.campo_matricula_edit_text.text.toString()
                meusVeiculos[position].dataMatricula =
                    mAlertDialog.campo_data_matricula.campo_data_matricula_edit_text.text.toString()

                viewModel.updateVeiculo(meusVeiculos[position], position)
                mAlertDialog.dismiss()
            }
        }

        item.veiculo_bloqueado.setOnClickListener {
            val number = 3838
            val text = "Rebocado ${meusVeiculos[position].matricula}"

            val uri = Uri.parse("smsto:$number")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            intent.putExtra("sms_body", text)
            context.applicationContext.startActivity(intent)
        }

    }

    private fun extendeCard(holder : MeusVeiculosViewHolder){
        if(holder.itemView.expandableView.visibility == View.GONE){
            holder.itemView.expandableView.visibility = View.VISIBLE;
            holder.itemView.arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_up);
        } else {
            holder.itemView.expandableView.visibility = View.GONE;
            holder.itemView.arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down);
        }
    }


}