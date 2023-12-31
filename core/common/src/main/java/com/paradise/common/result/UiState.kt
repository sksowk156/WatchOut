package com.paradise.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class UiState<out T> { // out 키워드가 있으면 ResponseStatae<T의 자식클래스> 또한 ResponseState<T>의 자식클래스로 인식한다. -> Covariant
    object Uninitialized : UiState<Nothing>()
    object Loading : UiState<Nothing>() // 어떤 데이터를 호출할 때도 값이 없는 것이기 때문에 싱글톤으로 구현
    data class Success<out T>(val data: T) : UiState<T>()
    data class Fail<out T>(val message: String?) : UiState<T>() // 데이터 전송 실패시
    data class Error<out T>(val exception: Throwable) : UiState<T>() // 예외 발생시
}

fun <T> Flow<T>.asResult(): Flow<UiState<T>> {
    return this.map<T, UiState<T>> {
        if (it == null) {
            UiState.Fail("데이터 없음")
        } else {
            UiState.Success(it)
        }
    }.onStart { emit(UiState.Loading) }.catch { emit(UiState.Error(it)) }
}