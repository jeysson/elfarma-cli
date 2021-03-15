package hashtag.alldelivery.ui.order

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import hashtag.alldelivery.core.models.BusinessEvent
import hashtag.alldelivery.core.models.Order
import hashtag.alldelivery.core.repository.IOrderRepository
import hashtag.alldelivery.core.utils.SingleLiveEvent

class OrderViewModel(private val _orderRep: IOrderRepository) : ViewModel() {

    var eventErro = SingleLiveEvent<String>()
    var eventOrder = SingleLiveEvent<Order>()

    fun checkoutOrder(order: Order){
        _orderRep.checkoutOrder(order).subscribe({
            if(it.code == 200){
                var o =  Gson().fromJson(Gson().toJson(it.data), Order::class.java)
                eventOrder.postValue(o)
            }
            else
                eventErro.postValue(it.message)
        },{
            eventErro.postValue(it.message)
        })
    }

}