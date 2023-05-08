package com.bhm.network.define

import java.io.IOException

//后台返回非OK_CODE时，抛出此异常
@Suppress("ConvertSecondaryConstructorToPrimary")
class ResultException : IOException {
    var code = 0
    override var message: String? = null
    var realJson: String? = null //原来的json

    constructor(code: Int, message: String?, realJson: String?) : super(message) {
        this.code = code
        this.message = message
        this.realJson = realJson
    }
}

const val OK_CODE = 200 //成功返回的code
const val MESSAGE_KEY = "message" //默认key
const val CODE_KEY = "code" //默认key
const val DATA_KEY = "data" //默认key