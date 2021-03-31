package hashtag.alldelivery.core.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hashtag.alldelivery.core.data.dao.AddressDao
import hashtag.alldelivery.core.models.Address
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AddressRepository(private val addressDao: AddressDao){

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

    fun firstAddress(): LiveData<Address> {
        return addressDao.firstAddress()
    }

    fun loadById(id: Int): Address{
        return addressDao.loadById(id)
    }

    fun setDefaulAddress(address: Address){
        addressDao.setDefault()
        addressDao.update(address)
    }
}