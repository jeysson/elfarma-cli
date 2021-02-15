package hashtag.alldelivery.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hashtag.alldelivery.core.models.Address

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

    @Query("SELECT * FROM address LIMIT 1")
    fun firstAddress(): Address

    @Insert
    fun insert(vararg address: Address)

    @Delete
    fun delete(address: Address)

    @Delete
    fun deleteAll(address: Address?)

    @Update
    fun update(address: Address)
}