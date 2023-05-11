package com.example.videotest2.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videotest2.livedatautils.LiveDataEvent
import com.example.videotest2.recordbutton.RecorderStateManager
import java.io.File

class HomeViewModel : ViewModel() {

    private var file: File? = null

    val updateTimeLiveData = MutableLiveData<LiveDataEvent<String>>()
    private val recorderState = MutableLiveData<RecorderStateManager.RecorderState>()

    fun setFile(file: File) {
        this.file = file
    }

    fun getFile(): File {
        return file!!
    }

    fun getTime(seconds: String, minutes: Long) {
        updateTimeLiveData.postValue(LiveDataEvent("$minutes : $seconds"))
    }

    fun getRecorderState(): RecorderStateManager.RecorderState {
        return recorderState.value ?: RecorderStateManager.RecorderState.INIT
    }

    fun updateRecordStatus(newState: RecorderStateManager.RecorderState) {
        recorderState.value = newState
    }

    fun getVideoRecorderCurrentState(): MutableLiveData<RecorderStateManager.RecorderState> {
        return recorderState
    }
}