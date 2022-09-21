package com.bhm.sdk.demo.activity

/**
 * @author Buhuiming
 * @description:
 * @date :2022/9/21 16:00
 */
class TestObj : TestCall {

    private var _testMethod1: (() -> Unit)? = null

    private var _testMethod2: (() -> Int)? = null

    private var _testMethod3: ((str: String) -> Unit)? = null

    private var _testMethod4: ((str: String) -> String)? = null

    private var _testMethod5: (() -> Any)? = null

    fun testMethod1(testMethod: () -> Unit) {
        _testMethod1 = testMethod
    }

    fun testMethod2(testMethod: () -> Int) {
        _testMethod2 = testMethod
    }

    fun testMethod3(testMethod: (str: String) -> Unit) {
        _testMethod3 = testMethod
    }

    fun testMethod4(testMethod: (str: String) -> String) {
        _testMethod4 = testMethod
    }

    fun testMethod5(testMethod: () -> Any) {
        _testMethod5 = testMethod
    }

    override fun onTestMethod1() {
        _testMethod1?.invoke()
    }

    override fun onTestMethod2(): Int {
        return _testMethod2?.invoke()?: 0
    }

    override fun onTestMethod3(m: String) {
        _testMethod3?.invoke(m)
    }

    override fun onTestMethod4(m: String): String? {
        return _testMethod4?.invoke(m)
    }

    override fun onTestMethod5(): Any? {
        return _testMethod5?.invoke()
    }
}