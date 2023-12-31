package com.paradise.common.helper

import androidx.lifecycle.MutableLiveData

interface SttHelper {
    fun initSttHelper()

    var sttResult: MutableLiveData<String>
    var sttState: MutableLiveData<Boolean>
    fun startSTT()
    fun stopSttHelper()
    fun releaseSttHelper()
}