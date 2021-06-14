package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projecto_02.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.domain.project.MainActivity
import java.lang.Exception

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_splash_screen)

        val background = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(2500)
                    val intent = Intent(baseContext, MainActivity::class.java)
                    startActivity(intent)
                }catch (e : Exception) {
                    e.printStackTrace()
                }
                super.run()
            }
        }

        background.start()
    }
}
