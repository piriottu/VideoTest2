package com.example.videotest2.recordbutton


interface IRecorderActions {

    fun onStartRecord()

    fun onPauseRecord()

    fun onResumeRecord()

    fun onEndRecord()

    fun onDurationTooShortError()

    fun onSingleTap()

    fun onCancelled()

    fun getCurrentRecorderState() : RecorderStateManager.RecorderState

    fun getTime(time:Long)
}
