package pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.local.room.dao

import androidx.room.*
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Bikes
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Lugares
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Parque
import pt.ulusofona.ecati.deisi.licenciatura.cm1920.grupo2.data.entity.Veiculo

@Dao
interface OperationDao {

    //Parque

    @Insert
    suspend fun insert(parque: Parque)

    @Update
    suspend fun updateParque(parque: Parque)

    @Delete
    suspend fun delete(parque: Parque)

    @Query("SELECT * FROM parque")
    suspend fun getAllParques(): List<Parque>

    @Query("SELECT * FROM parque WHERE uuid - :uuid")
    suspend fun getById(uuid: String): Parque

    //Veiculos

    @Insert
    suspend fun insertVeiculo(veiculo: Veiculo)

    @Update
    suspend fun updateVeiculo(veiculo: Veiculo)

    @Query("UPDATE Veiculo SET marca = :marca WHERE uuid = :uuid") //update da marca
    suspend fun updateVeiculoMarca(uuid: String, marca: String)

    @Query("UPDATE Veiculo SET modelo = :modelo WHERE uuid = :uuid") //update do modelo
    suspend fun updateVeiculoModelo(uuid: String, modelo: String)

    @Query("UPDATE Veiculo SET matricula = :matricula WHERE uuid = :uuid") //update da matricula
    suspend fun updateVeiculoMatricula(uuid: String, matricula: String)

    @Query("UPDATE Veiculo SET dataMatricula = :dataMatricula WHERE uuid = :uuid") //update da data de matricula
    suspend fun updateVeiculoDataMatricula(uuid: String, dataMatricula: String)

    @Delete
    suspend fun deleteVeiculo(veiculo: Veiculo)

    @Query("SELECT * FROM veiculo")
    suspend fun getAllVeiculos(): List<Veiculo>

    @Query("SELECT * FROM veiculo WHERE uuid - :uuid")
    suspend fun getByVeiculoId(uuid: String): Veiculo

    //Lugares

    @Insert
    suspend fun insertLugar(lugar: Lugares)

    @Update
    suspend fun updateLugar(lugar: Lugares)

    @Delete
    suspend fun deleteLugar(lugar: Lugares)

    @Query("SELECT * FROM lugares")
    suspend fun getLugar(): Lugares

    // Bikes

    @Insert
    suspend fun insertBike(bikes : Bikes)

    @Update
    suspend fun updateBike(bikes: Bikes)

    @Query("SELECT * FROM bikes")
    suspend fun getAllBikes() : List<Bikes>


}