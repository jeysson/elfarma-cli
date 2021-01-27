package hashtag.alldelivery.core.repository

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class BaseRepository {

    protected fun <T> runOnBackground(obs: Observable<T>): Observable<T> {
        return obs.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

}
