package com.bhm.sdk.demo.activity

/**
 * @author Buhuiming
 * @description:
 * @date :2022/9/21 15:56
 */
interface TestCall {
    fun onTestMethod1()
    fun onTestMethod2(): Int
    fun onTestMethod3(m: String)
    fun onTestMethod4(m: String): String?
    fun onTestMethod5(): Any?
}