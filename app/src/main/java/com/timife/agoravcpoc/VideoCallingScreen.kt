package com.timife.agoravcpoc

import android.util.Log
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.timife.agoravcpoc.states.AgoraVCState
import com.timife.agoravcpoc.viewmodel.AgoraVCViewModel
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas

@Composable
fun VideoCallingScreen(
    modifier: Modifier = Modifier,
    viewModel:AgoraVCViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mRtcEngine = viewModel.mRtcEngine.collectAsState()
    val state = viewModel.state.collectAsState()
    LaunchedEffect(key1 = true){
        viewModel.initializeAgoraEngine(context, Consts.APP_ID)
    }

    Box(modifier = Modifier){
        mRtcEngine.value?.let {
            LocalVideoView(rtcEngine = it)
        }
        when (val event = state.value) {
            is AgoraVCState.ChannelJoined -> {
                Log.d("AgoraVCViewModel", "ChannelJoined: ${event.uId}")
            }
            is AgoraVCState.UserJoined -> {
                RemoteVideoView(uid = event.uId, rtcEngine = event.mRtcEngine)
            }
            is AgoraVCState.Error -> {
                // Show error message
                Log.d("AgoraVCViewModel", "Error: ${event.message}")
            }
            AgoraVCState.Idle -> {}
            AgoraVCState.Loading -> {}
        }
    }

    // Video Calling Screen
}



@Composable
fun LocalVideoView(
    rtcEngine: RtcEngine
) {
    rtcEngine.enableVideo()
    rtcEngine.startPreview()
    AndroidView(
        factory = { context ->
            FrameLayout(context).apply {
                val surfaceView = SurfaceView(context)
                addView(surfaceView)
                // Pass the Surface
                rtcEngine.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun RemoteVideoView(
    uid: Int,
    rtcEngine: RtcEngine
) {
    AndroidView(
        factory = { context ->
            FrameLayout(context).apply {
                val surfaceView = SurfaceView(context).apply {
                    setZOrderMediaOverlay(true)
                }
                addView(surfaceView)
                // Pass the SurfaceView object to the SDK and set the remote view
                rtcEngine.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}