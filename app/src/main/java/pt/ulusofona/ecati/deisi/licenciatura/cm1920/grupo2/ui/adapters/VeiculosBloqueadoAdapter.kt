package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.adapters

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import kotlinx.android.synthetic.main.veiculo_expression.view.nome_veiculo
import kotlinx.android.synthetic.main.veiculo_expression.view.veiculo_marca_text
import kotlinx.android.synthetic.main.veiculo_expression.view.veiculo_matricula_text
import kotlinx.android.synthetic.main.veiculo_expression_bloqueio.view.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Veiculo
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.MeusVeiculosViewModel
import java.util.*

class VeiculosBloqueadoAdapter (private var viewModel: MeusVeiculosViewModel, private val context: Context, private val layout: Int, private var
meusVeiculos: MutableList<Veiculo>) :
    RecyclerView.Adapter<VeiculosBloqueadoAdapter.MeusVeiculosViewHolder>(){

    class MeusVeiculosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val marca: TextView = view.veiculo_marca_bloqueio_text
        val matricula: TextView = view.veiculo_matricula_bloqueio_text
        val nome : TextView = view.nome_veiculo_bloqueio
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
        holder.marca.text = meusVeiculos[position].marca
        holder.matricula.text = meusVeiculos[position].matricula
        holder.nome.text = holder.marca.text.toString() + " " + meusVeiculos[position].modelo
        val item = holder.itemView

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
                holder.itemView.setBackgroundColor(context.resources.getColor(R.color.backgroundDark))
                //muda campo marca para dark mode
                holder.itemView.veiculo_marca_bloqueio_text.setTextColor(context.resources.getColor(R.color.textDark))
                //muda campo matricula para dark mode
                holder.itemView.veiculo_matricula_bloqueio_text.setTextColor(context.resources.getColor(R.color.textDark))
                //muda campo Verificar para dark mode
                holder.itemView.veiculo_bloqueado_verificar.setTextColor(context.resources.getColor(R.color.textDark))
            } else {
                //muda background para light mode
                //muda titulo para light mode
                //muda campo marca para light mode
                holder.itemView.veiculo_marca_bloqueio_text.setTextColor(context.resources.getColor(R.color.textLight))
                //muda campo matricula para light mode
                holder.itemView.veiculo_matricula_bloqueio_text.setTextColor(context.resources.getColor(R.color.textLight))
                //muda campo Verificar para light mode
                holder.itemView.veiculo_bloqueado_verificar.setTextColor(context.resources.getColor(R.color.textLight))
            }
        }

        item.veiculo_bloqueado_verificar.setOnClickListener {
                val number = 3838
                val text = "Rebocado ${meusVeiculos[position].matricula}"

                val uri = Uri.parse("smsto:$number")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                intent.putExtra("sms_body", text)
                context.applicationContext.startActivity(intent)
        }

    }


}