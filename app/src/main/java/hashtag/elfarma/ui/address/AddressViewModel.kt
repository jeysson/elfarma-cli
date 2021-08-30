package hashtag.elfarma.ui.address

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import hashtag.elfarma.core.data.AppDatabase
import hashtag.elfarma.core.models.Address
import hashtag.elfarma.core.repository.AddressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddressViewModel(application: Application): AndroidViewModel(application) {
    private val repository: AddressRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allAddress: LiveData<List<Address>>

    init {
        val addressDao = AppDatabase.getDatabase(application).addressDao()
        repository = AddressRepository(addressDao)
        allAddress = repository.allAddress
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(address: Address) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(address)
    }

    fun getAllAsync(): LiveData<List<Address>>{
        return repository.getAllAsync()
    }

    fun getAll(): List<Address>{
        return repository.getAll()
    }

    fun delete(address: Address) = viewModelScope.launch (Dispatchers.IO){
        repository.delete(address)
    }

    fun update(address: Address)  = viewModelScope.launch (Dispatchers.IO){
        repository.update(address)
    }

    fun firstAddress(): Address{
        return repository.firstAddress()
    }

    fun firstAddressAsync(): LiveData<Address>{
        return repository.firstAddressAsync()
    }

    fun loadById(id: Int): Address {
        return repository.loadById(id)
    }

    fun defaultAddress(address: Address)= viewModelScope.launch (Dispatchers.IO){
        repository.setDefaulAddress(address)
    }
}