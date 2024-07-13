package com.trm.alarmist.core.common.model

sealed interface Initializable<out T>

data object Uninitialized : Initializable<Nothing>

data class Initialized<T>(val data: T) : Initializable<T>
