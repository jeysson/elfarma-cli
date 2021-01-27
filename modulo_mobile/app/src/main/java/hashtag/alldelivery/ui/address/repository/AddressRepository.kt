package hashtag.alldelivery.ui.address.repository

import androidx.lifecycle.LiveData
import hashtag.alldelivery.core.data.dao.AddressDao
import hashtag.alldelivery.core.models.Address

class AddressRepository(private val addressDao: AddressDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allAddress: LiveData<List<Address>> = addressDao.getAll()

    suspend fun insert(address: Address) {
        addressDao.insert(address)
    }

    fun getAll(): LiveData<List<Address>> {
        return addressDao.getAll()
    }

    fun delete(address: Address) {
        addressDao.delete(address)
    }

    fun update(address: Address) {
        addressDao.update(address)
    }

    fun firstAddress(): Address{
       return addressDao.firstAddress()
    }
}