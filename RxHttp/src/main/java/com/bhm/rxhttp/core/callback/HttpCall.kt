package com.bhm.rxhttp.core.callback

import io.reactivex.rxjava3.core.Observable

/**
 * @author Buhuiming
 * @date :2022/9/21 9:18
 */
interface HttpCall<E : Any, T : Any> {
    fun callHttp(api: T): Observable<E>?
}