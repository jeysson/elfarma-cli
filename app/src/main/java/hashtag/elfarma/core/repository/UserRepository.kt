package hashtag.elfarma.core.repository

import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.core.models.*
import hashtag.elfarma.core.network.OrderApi
import hashtag.elfarma.core.network.UserApi
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header

class UserRepository(private val dataSrouce: UserApi): IUserRepository, BaseRepository()  {

    override fun update(user: User): Observable<Message> {
        return runOnBackground(dataSrouce.update("Bearer " + AllDeliveryApplication.USER?.token, user))
    }
}