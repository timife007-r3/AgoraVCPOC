package com.timife.agoravcpoc.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timife.agoravcpoc.states.AgoraVCState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgoraVCViewModel @Inject constructor() : ViewModel() {

    private val _state: MutableStateFlow<AgoraVCState> = MutableStateFlow(AgoraVCState.Idle)
    val state: StateFlow<AgoraVCState> = _state

    private val _mRtcEngine: MutableStateFlow<RtcEngine?> = MutableStateFlow(null)
    val mRtcEngine: StateFlow<RtcEngine?> = _mRtcEngine


    //    private var mRtcEngine: RtcEngine? = null
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Callback when successfully joining the channel
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            viewModelScope.launch {
                // When joining a channel, display the local video stream
                mRtcEngine.let {
                    Log.d("AgoraVCViewModel", "onJoinChannelSuccess: $uid")
                    Log.d("AgoraVCViewModel", "Engine: $it")
                    _state.value = AgoraVCState.ChannelJoined(uid, elapsed, it.value!!)
                }
            }
        }

        // Callback when a remote user or host joins the current channel
        override fun onUserJoined(uid: Int, elapsed: Int) {
            viewModelScope.launch {
                // When a remote user joins the channel, display the remote video stream for the specified uid
                mRtcEngine.let {
                    Log.d("AgoraVCViewModel", "onUserJoined: $uid")
                    _state.value = AgoraVCState.UserJoined(uid, it.value!!)
                }
            }
        }

        // Callback when a remote user or host leaves the current channel
        override fun onUserOffline(uid: Int, reason: Int) {
            viewModelScope.launch {
                _state.value = AgoraVCState.Error("User offline: $uid")
            }
        }
    }

    fun initializeAgoraEngine(context: Context, myAppId: String) {
        try {
            val config = RtcEngineConfig().apply {
                mContext = context
                mAppId = myAppId
                mEventHandler = mRtcEventHandler
            }
            // Create and initialize an RtcEngine instance
            _mRtcEngine.value = RtcEngine.create(config)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}