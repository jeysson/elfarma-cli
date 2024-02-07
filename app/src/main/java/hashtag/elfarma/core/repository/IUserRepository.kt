package hashtag.elfarma.core.repository

import hashtag.elfarma.core.models.*
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header

interface IUserRepository {

    fun update(user: User) : Observable<Message>
}