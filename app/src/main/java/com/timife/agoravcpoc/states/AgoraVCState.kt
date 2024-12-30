package com.timife.agoravcpoc.states

import io.agora.rtc2.RtcEngine

sealed class AgoraVCState {
    data object Idle : AgoraVCState()
    data object Loading : AgoraVCState()
    data class Error(val message: String) : AgoraVCState()
    data class ChannelJoined(val uId:Int, val elapsed:Int, val mRtcEngine: RtcEngine) : AgoraVCState()
    data class UserJoined(val uId:Int, val mRtcEngine: RtcEngine) : AgoraVCState()
}