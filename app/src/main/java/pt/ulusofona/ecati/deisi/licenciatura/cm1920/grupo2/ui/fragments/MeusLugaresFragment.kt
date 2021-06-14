package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.example.projecto_02.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Lugares
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils.NavigationManager
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.viewmodels.viewmodels.MeusLugaresViewModel
import java.io.ByteArrayOutputStream
import java.util.*

class MeusLugaresFragment : Fragment(){
    private lateinit var viewModel: MeusLugaresViewModel
    private var listMeusLugares = listOf<Lugares>()
    lateinit var currentPhotoPath: String
    private var imageView: ImageView? = null
    private lateinit var lugar : Lugares

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_meus_lugares, container, false)
        imageView = view!!.findViewById<View>(R.id.imageView1) as ImageView
        viewModel = ViewModelProviders.of(this).get(MeusLugaresViewModel::class.java)
        ButterKnife.bind(this, view)
        return view

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MeusLugaresViewModel::class.java)
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
        if(viewModel.getLugar() != null) {
            lugar = viewModel.getLugar()!!
            imageView!!.setImageBitmap(getImage(lugar.image!!))
        }

        super.onStart()
    }

    override fun onDestroy() {
        viewModel.unregisterListener()
        super.onDestroy()
    }

    @Optional
    @OnClick( R.id.fabNewLugar)
    fun onClickNewLugar(view: View){
        if (checkSelfPermission( activity!!,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.CAMERA),
                MeusLugaresFragment.MY_CAMERA_PERMISSION_CODE
            )
        } else {
            val cameraIntent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, MeusLugaresFragment.CAMERA_REQUEST)
        }

    }

    @Optional
    @OnClick( R.id.fab_map)
    fun onClickgoToMap(view: View){
       if (this::lugar.isInitialized){
           if(lugar.latitude != null && lugar.longitude != null){
               val gmmIntentUri = Uri.parse("geo:0,0?q=${lugar.latitude},${lugar.longitude}")
               val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
               mapIntent.setPackage("com.google.android.apps.maps")
               startActivity(mapIntent)
           }else {
               val builder = AlertDialog.Builder(context!!)
               builder.setMessage(getString(R.string.sem_localizacao))
                   .setPositiveButton(getString(R.string.ok),
                       DialogInterface.OnClickListener { dialog, id ->
                           // User set ok to dialog
                       })
               // Create the AlertDialog object and return it
               builder.create()
               builder.show()
           }
       }else {
           val builder = AlertDialog.Builder(context!!)
           builder.setMessage(getString(R.string.sem_foto))
               .setPositiveButton(getString(R.string.ok),
                   DialogInterface.OnClickListener { dialog, id ->
                       // User set ok to dialog
                   })
           // Create the AlertDialog object and return it
           builder.create()
           builder.show()
       }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, getText(R.string.com_permissao), Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            } else {
                Toast.makeText(context,getText(R.string.sem_permissao), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val photo = data!!.extras["data"] as Bitmap
            imageView!!.setImageBitmap(photo)
            val location = viewModel.getLocation()
            if (location == null ) {
                val lugar = Lugares(getBytes(photo), null, null)
                viewModel.insertLugar(lugar)
            }else {
                val lugar = Lugares(getBytes(photo), location!!.latitude, location!!.longitude)
                viewModel.insertLugar(lugar)
            }
            popUpBikeMap()
        }
    }

    private fun popUpBikeMap() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(getString(R.string.popup_bicicleta_message))
            .setPositiveButton(getString(R.string.sim),
                DialogInterface.OnClickListener { dialog, id ->
                    fragmentManager?.let { NavigationManager.goToMapBicicletaFragment(it) }
                })
            .setNegativeButton(getString(R.string.nao),
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }

    // convert from bitmap to byte array
    fun getBytes(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream)
        return stream.toByteArray()
    }

    // convert from byte array to bitmap
    fun getImage(image: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    companion object {
        private const val CAMERA_REQUEST = 1888
        private const val MY_CAMERA_PERMISSION_CODE = 100
    }


    }