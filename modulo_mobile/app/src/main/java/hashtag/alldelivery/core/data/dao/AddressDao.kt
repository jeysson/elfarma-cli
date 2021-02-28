package hashtag.alldelivery.core.data.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.*
import hashtag.alldelivery.core.models.Address
import io.reactivex.Observable

@Dao
interface AddressDao {
    @Query("SELECT * FROM address")
    fun getAll():LiveData<List<Address>>

    @Query("SELECT * FROM address WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): LiveData<List<Address>>

    @Query("SELECT * FROM address WHERE id = :id")
    fun loadById(id: Int): Address

    @Query("SELECT * FROM address WHERE address LIKE :address AND " +
            "number = :number LIMIT 1")
    fun findByName(address: String, number: String): Address

    @Query("SELECT * FROM address WHERE padrao = 1 LIMIT 1")
    fun firstAddress(): LiveData<Address>

    @Insert
    fun insert(vararg address: Address)

    @Delete
    fun delete(address: Address)

    @Delete
    fun deleteAll(address: Address?)

    @Update
    fun update(address: Address)

    @Query("UPDATE address SET padrao = 0")
    fun setDefault()
}