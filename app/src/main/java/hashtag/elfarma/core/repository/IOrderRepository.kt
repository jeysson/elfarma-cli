package hashtag.elfarma.core.repository

import hashtag.elfarma.core.models.Message
import hashtag.elfarma.core.models.Order
import io.reactivex.Observable

interface IOrderRepository {

    fun checkoutOrder(order: Order): Observable<Message>

    fun getOrder(id: Int): Observable<Message>

    fun getOrderHistory(user: Int, page: Int, total: Int): Observable<Message>

    fun getOrdersWaitingEvaluate(user: Int): Observable<Message>

    fun saveEvaluate(order: Order): Observable<Message>
}