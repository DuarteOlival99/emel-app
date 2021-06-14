package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.room.dao.OperationDao
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.remote.responses.ParkingLotBikeResponse

@Database(entities = arrayOf(Parque::class, Veiculo::class, Lugares::class, Bikes::class), version = 8)
abstract class GetMeSpotDatabase : RoomDatabase() {

    abstract fun operationDao() : OperationDao

    companion object {
        private var instance: GetMeSpotDatabase? = null

        fun getInstance(applicationContext: Context): GetMeSpotDatabase {
            synchronized(this) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        applicationContext,
                        GetMeSpotDatabase::class.java,
                        "getMeSpot_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return instance as GetMeSpotDatabase
            }
        }
    }



}