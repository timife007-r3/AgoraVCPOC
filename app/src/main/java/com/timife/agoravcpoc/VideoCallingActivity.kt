/*package com.timife.agoravcpoc

import android.Manifest
import android.os.Bundle
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration

class AgoraAudioVideoCallComposeActivity : ComponentActivity() {

    // Reference to the Agora RtcEngine instance
    private var mRtcEngine: RtcEngine? = null

    // State variables to manage UI and call states
    private var mMuted by mutableStateOf(false)
    private var mCallEnd by mutableStateOf(false)
    private var mVideoClosed by mutableStateOf(false)

    // Agora App ID and channel name
    private val APP_ID = "2245dddd37134af29fab235a206376dc"
    private val CHANNEL_NAME = "test_channel"

    // Event handler for Agora SDK callbacks
    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            // Notify user of successful channel join
            runOnUiThread {
                Toast.makeText(
                    this@AgoraAudioVideoCallComposeActivity,
                    "Joined channel: $channel",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            // Handle remote user joining the channel
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            // Handle remote user leaving the channel
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request necessary permissions for audio and video
        requestPermissions(
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA),
            100
        )

        // Initialize the Agora engine
        initializeEngine()

        // Set the UI content using Compose
        setContent {
            MaterialTheme {
                CallScreen()
            }
        }
    }

    // Initialize the Agora engine and configure video settings
    private fun initializeEngine() {
        mRtcEngine = RtcEngine.create(this, APP_ID, rtcEventHandler)
        mRtcEngine?.enableVideo()
        mRtcEngine?.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }

    // Start a call by joining the specified channel
    private fun startCall() {
        mRtcEngine?.joinChannel(null, CHANNEL_NAME, null, 0)
    }

    // End the ongoing call by leaving the channel
    private fun endCall() {
        mRtcEngine?.leaveChannel()
    }

    @Composable
    fun CallScreen() {
        // Layout for the call screen
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!mCallEnd) {
                // Display the local video feed
                AndroidView(
                    factory = { context ->
                        FrameLayout(context).apply {
                            val surfaceView = SurfaceView(context)
                            addView(surfaceView)
                            mRtcEngine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
                            mRtcEngine!!.startPreview()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            // Row of buttons for call controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Mute/unmute button
                IconButton(onClick = {
                    mMuted = !mMuted
                    mRtcEngine?.muteLocalAudioStream(mMuted)
                }) {
//                    Icon(
//                        painter = painterResource(if (mMuted) R.drawable.btn_mute else R.drawable.btn_unmute),
//                        contentDescription = null
//                    )
                }

                // Start/end call button
                IconButton(onClick = {
                    mCallEnd = !mCallEnd
                    if (mCallEnd) endCall() else startCall()
                }) {
//                    Icon(
//                        painter = painterResource(if (mCallEnd) R.drawable.btn_startcall else R.drawable.reject_call_icon),
//                        contentDescription = null
//                    )
                }

                // Enable/disable video button
                IconButton(onClick = {
                    mVideoClosed = !mVideoClosed
                    mRtcEngine?.enableLocalVideo(!mVideoClosed)
                }) {
//                    Icon(
//                        painter = painterResource(if (mVideoClosed) R.drawable.btn_video_off else R.drawable.btn_video_on),
//                        contentDescription = null
//                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release Agora engine resources
        RtcEngine.destroy()
    }
}*/
