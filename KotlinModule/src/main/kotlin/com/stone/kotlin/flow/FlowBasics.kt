package com.stone.kotlin.flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * 冷流：flow { } 里的代码在 collect 时才执行，每次 collect 都重新执行一遍。
 * map / filter 在上游和收集者之间变换数据。
 * flowOn 只影响它上面的代码，下面的操作和 collect 仍在调用方的协程里。
 */
fun filteredNumbers(): Flow<String> = flow {
    val thread = Thread.currentThread().name
    listOf(1, 2, 3).forEach { value ->
        emit(value to thread)
    }
}.flowOn(Dispatchers.Default)
    .filter { (value, _) -> value % 2 != 0 }
    .map { (value, thread) -> "上游 $thread 发出 ${value * 10}" }

/**
 * 上游的异常可以用 catch 接住，并往下游补一个值。
 * catch 接不住 collect 里面抛出的异常。
 */
fun failingFlow(): Flow<String> = flow {
    emit("第一条正常")
    error("上游出错")
}.catch { throwable ->
    emit("catch 接住：${throwable.message}")
}
