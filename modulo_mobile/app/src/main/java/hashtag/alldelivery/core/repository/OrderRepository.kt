package hashtag.alldelivery.core.repository

import hashtag.alldelivery.core.models.Message
import hashtag.alldelivery.core.models.Order
import hashtag.alldelivery.core.network.OrderApi
import io.reactivex.Observable

class OrderRepository(private val dataSrouce: OrderApi): IOrderRepository, BaseRepository() {

    override fun checkoutOrder(order: Order): Observable<Message> {
        return  runOnBackground(dataSrouce.checkoutOrder(order))
    }
}