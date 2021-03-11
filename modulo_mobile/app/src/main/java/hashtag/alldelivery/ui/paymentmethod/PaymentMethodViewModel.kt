package hashtag.alldelivery.ui.paymentmethod

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hashtag.alldelivery.core.models.PaymentMethod
import hashtag.alldelivery.core.repository.IStoreRepository

class PaymentMethodViewModel(private val _storeRep: IStoreRepository): ViewModel()  {

    var loadPayment: MutableLiveData<ArrayList<PaymentMethod>> = MutableLiveData()

    fun getPaymentMethods(loja: Int){
        _storeRep.getPaymentMethods(loja).subscribe({
            loadPayment.postValue(it)
        },{

        })
    }
}