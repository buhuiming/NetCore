package com.bhm.sdk.demo.activity

import com.bhm.sdk.demo.activity.TestCall

/**
 * @author Buhuiming
 * @description:
 * @date :2022/9/21 15:55
 */
object Test {
    fun test(call: TestCall.() -> Unit) {
        val ca = TestObj()
//        ca.call()
        ca.apply(call)
        ca.onTestMethod1()
    }
}