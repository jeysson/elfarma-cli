package hashtag.elfarma.ui.address.repository

import androidx.lifecycle.LiveData
import hashtag.elfarma.core.data.dao.AddressDao
import hashtag.elfarma.core.models.Address

class AddressRepository(private val addressDao: AddressDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allAddress: LiveData<List<Address>> = addressDao.getAllAsync()

    suspend fun insert(address: Address) {
        addressDao.insert(address)
    }

    fun getAllAsync(): LiveData<List<Address>> {
        return addressDao.getAllAsync()
    }

    fun getAll(): List<Address> {
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

    fun firstAddressAsync(): LiveData<Address>{
        return addressDao.firstAddressAsync()
    }

    fun loadById(id: Int): Address{
        return addressDao.loadById(id)
    }
}