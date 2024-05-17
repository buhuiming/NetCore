/*
 * Copyright (c) 2022-2032 buhuiming
 * 不能修改和删除上面的版权声明
 * 此代码属于buhuiming编写，在未经允许的情况下不得传播复制
 */
package com.bhm.network.define

/**
 * @description 日志等级
 * @author Buhuiming
 * @date 2024/05/17/ 16:01
 */
sealed class HttpLogLevel {
    object Debug : HttpLogLevel()
    object Info : HttpLogLevel()
    object Warn : HttpLogLevel()
    object Error : HttpLogLevel()
}