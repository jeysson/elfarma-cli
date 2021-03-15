package hashtag.alldelivery.core.repository

import hashtag.alldelivery.core.models.Message
import hashtag.alldelivery.core.models.Order
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface IOrderRepository {

    fun checkoutOrder(order: Order): Observable<Message>
}