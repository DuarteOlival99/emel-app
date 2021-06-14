package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.utils

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.projecto_02.R
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.ui.fragments.*

class NavigationManager {

    companion object{

        private fun placeFragment(fm: FragmentManager, fragment: Fragment) {
            Log.i("navigation", "navigation")
            val transition = fm.beginTransaction()
            transition.replace(R.id.frame, fragment)
            transition.addToBackStack(null)
            transition.commit()
        }

        private fun placeChildFragment(fm: FragmentManager, fragment: Fragment) {
            Log.i("navigation", "navigation")
            val transition = fm.beginTransaction()
            transition.replace(R.id.frame, fragment)
            transition.addToBackStack(null)
            transition.commit()
        }



        fun goToSplashScreen(fm: FragmentManager) {
            placeFragment(
                fm,
                SplashScreenFragment()
            )
        }

        fun goToMapFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                MapFragment()
            )
        }

        fun goToMapBicicletaFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                MapBikeFragment()
            )
        }

        fun goToMeusVeiculosFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                MeusVeiculosFragment()
            )
        }

        fun goToMeusLugaresFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                MeusLugaresFragment()
            )
        }

        fun goToContactsFragmentGeral(fm: FragmentManager ) {
            placeFragment(
                fm,

                ContactosGeraisFragment()

            )
        }

        fun goToContactsFragmentBlock(fm: FragmentManager ) {
            placeFragment(
                fm,

                ContactosBlockVehiclesFragment()
            )
        }

        fun goToContactsFragmentAtendimento(fm: FragmentManager ) {
            placeFragment(
                fm,

                ContactsFragment()
            )
        }

        fun goToParquesFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                ParquesFragment()
            )
        }


        fun goToDetalhesFragment(fm: FragmentManager) {
            placeFragment(
                fm,
                DetailsFragment()
            )
        }

        fun goToOpcoes(fm: FragmentManager) {
            placeFragment(
                fm,
                OpcoesFragment()
            )
        }

    }

}