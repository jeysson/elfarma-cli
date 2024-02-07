package hashtag.elfarma.ui.order

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hashtag.elfarma.core.models.Order
import hashtag.elfarma.core.models.OrderHistory
import hashtag.elfarma.core.repository.IOrderRepository
import hashtag.elfarma.core.utils.SingleLiveEvent
import java.time.ZoneId

class OrderViewModel(private val _orderRep: IOrderRepository) : ViewModel() {

    var eventErro = SingleLiveEvent<String>()
    var eventOrder = SingleLiveEvent<Order>()
    var eventOrders = SingleLiveEvent<ArrayList<Order>>()
    var eventLoading = SingleLiveEvent<Boolean>()
    var eventOrderHistory = SingleLiveEvent<ArrayList<Any>>()
    var eventRedirect = SingleLiveEvent<Boolean>()
    var adapter : OrderHistoryAdapter? = null
    var adapterEvaluate: OrderEvaluateAdapter? = null

    fun checkoutOrder(order: Order){
        _orderRep.checkoutOrder(order).subscribe({
            if(it.code == 200){
                val o = Order()
                o.id = (it.data as Double).toInt()
                eventOrder.postValue(o)
            }
            else
                eventErro.postValue(it.message)
        },{
            eventErro.postValue(it.message)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getOrderHistory(user: Int, page: Int, total: Int) {
        eventLoading.postValue(true)
        _orderRep.getOrderHistory(user, page, total).subscribe({
            if(it.code == 200) {
                val type = object : TypeToken<List<OrderHistory>>() {}.type
                var list = Gson().fromJson<ArrayList<OrderHistory>>(Gson().toJson(it.data), type)


                var arr = ArrayList<Any>()
                //
                var nlist = list.groupBy { p ->
                    p.date?.toInstant()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toLocalDate()
                }
                nlist.forEach {
                    arr.add(it.key!!)
                    arr.addAll(it.value)
                }
                //
                adapter?.addItens(arr)
                adapter?.notifyDataSetChanged()
                //
                eventOrderHistory.postValue(arr)

                eventLoading.postValue(false)
            }
            else
                eventErro.postValue(it.message)
        },
        {
            eventErro.postValue(it.message)
        })
    }

    fun getOrder(id: Int) {
        eventLoading.postValue(true)
        _orderRep.getOrder(id).subscribe({
            if(it.code == 200){
                val type = object: TypeToken<Order>() {}.type
                var obj =  Gson().fromJson<Order>(Gson().toJson(it.data), type)

                eventOrder.postValue(obj)
                eventLoading.postValue(false)
            }
            else
                eventErro.postValue(it.message)
        },
            {
                eventErro.postValue(it.message)
            })
    }


    fun getOrdersWaitingEvaluate(user: Int){
        eventLoading.postValue(true)
        _orderRep.getOrdersWaitingEvaluate(user).subscribe({
            if(it.code == 200) {
                val type = object : TypeToken<List<Order>>() {}.type
                var list = Gson().fromJson<ArrayList<Order>>(Gson().toJson(it.data), type)

                adapterEvaluate?.addItens(list)
                adapterEvaluate?.notifyDataSetChanged()
                //
                eventOrders.postValue(list)

                eventLoading.postValue(false)
            }
            else
                eventErro.postValue(it.message)
        },
            {
                eventErro.postValue(it.message)
            })
    }

    fun saveEvaluate(order: Order){

        _orderRep.saveEvaluate(order).subscribe({
              eventOrder.postValue(null)
        },{
            eventErro.postValue(it.message)
        })
    }

    fun getLastOrder(id: Int){
        _orderRep.getLastOrder(id).subscribe({
            eventOrder.postValue(null)
        },{
            eventErro.postValue(it.message)
        })
    }
}