package hashtag.elfarma.ui.paymentmethod

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hashtag.elfarma.core.models.PaymentMethod
import hashtag.elfarma.core.repository.IStoreRepository

class PaymentMethodViewModel(private val _storeRep: IStoreRepository): ViewModel()  {

    var loadPayment: MutableLiveData<ArrayList<PaymentMethod>> = MutableLiveData()

    fun getPaymentMethods(loja: Int){
        _storeRep.getPaymentMethods(loja).subscribe({
            loadPayment.postValue(it)
        },{

        })
    }
}