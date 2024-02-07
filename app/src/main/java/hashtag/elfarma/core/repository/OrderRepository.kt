package hashtag.elfarma.core.repository

import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.core.models.Message
import hashtag.elfarma.core.models.Order
import hashtag.elfarma.core.network.OrderApi
import io.reactivex.Observable

class OrderRepository(private val dataSrouce: OrderApi): IOrderRepository, BaseRepository() {

    override fun checkoutOrder(order: Order): Observable<Message> {
        return  runOnBackground(dataSrouce.checkoutOrder("Bearer " + AllDeliveryApplication.USER?.token, order))
    }

    override fun getOrder(id: Int): Observable<Message> {
        return runOnBackground(dataSrouce.getOrder("Bearer " + AllDeliveryApplication.USER?.token, id))
    }

    override fun getOrderHistory(user: Int, page: Int, total: Int): Observable<Message> {
        return  runOnBackground(dataSrouce.getOrderHistory("Bearer " + AllDeliveryApplication.USER?.token, user, page, total))
    }

    override fun getOrdersWaitingEvaluate(user: Int): Observable<Message> {
        return  runOnBackground(dataSrouce.getOrdersWaitingEvaluate("Bearer " + AllDeliveryApplication.USER?.token, user))
    }

    override fun saveEvaluate(order: Order): Observable<Message> {
        return  runOnBackground(dataSrouce.saveEvaluate("Bearer " + AllDeliveryApplication.USER?.token, order))
    }

    override fun getLastOrder(user: Int): Observable<Message> {
        return  runOnBackground(dataSrouce.getLastOrder("Bearer " + AllDeliveryApplication.USER?.token, user))
    }
}