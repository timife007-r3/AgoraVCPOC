package com.timife.agoravcpoc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.timife.agoravcpoc.Consts.APP_ID
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas

private const val PERMISSION_REQ_ID = 22
private val permissions = arrayOf(
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.CAMERA
)



class VideoCallActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val channelName = intent.getStringExtra("ChannelName")
        val userRole = intent.getStringExtra("UserRole")
        val userId = intent.getStringExtra("UserId")




        enableEdgeToEdge()
        setContent {
            Scaffold {
                UIRequirePermissions(
                    permissions = permissions,
                    onPermissionGranted = {
                        if (channelName != null && userRole != null) {
                            CallScreen(channelName, userRole, userId!!.toInt())
                        }
                    },
                    onPermissionDenied = { AlertScreen(it) }
                )
            }
        }
    }
}


///13241e685d8b4d6c8642da35f6970d4b
fun initEngine(context: Context, eventHandler: IRtcEngineEventHandler, channelName: String, userRole: String, userId:Int): RtcEngine =
    RtcEngine.create(context, "d9b1d4e54b9e4a01aac1de9833d83752", eventHandler).apply {
        enableVideo()
        setChannelProfile(1)
        setClientRole(if (userRole == "Broadcaster") 1 else 0)
        joinChannel(null, channelName, null, userId)
    }

@Composable
private fun CallScreen(channelName: String, userRole: String, userId:Int) {
    val context = LocalContext.current
    var remoteUserMap by remember { mutableStateOf(mapOf<Int, SurfaceView>()) }

    val engine = remember {
        initEngine(context, object : IRtcEngineEventHandler() {
            override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                println("Channel: $channel, UID: $uid")
            }

            override fun onUserJoined(uid: Int, elapsed: Int) {
                remoteUserMap = remoteUserMap + (uid to SurfaceView(context).apply { setZOrderMediaOverlay(true) })
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                remoteUserMap = remoteUserMap - uid
            }
        }, channelName, userRole, userId)
    }

    if(userRole == "Broadcaster") {

        LocalView(engine )
//        mEngine.setupLocalVideo(VideoCanvas(localSurfaceView, Constants.RENDER_MODE_FIT, 0))
    }



    Box(Modifier.fillMaxSize()) {
        LocalView(engine)
        RemoteView(remoteUserMap, engine)
        UserControls(engine)
    }
}

@Composable
fun LocalView(engine: RtcEngine) {
    AndroidView(
        factory = { context ->
            FrameLayout(context).apply {
                val surfaceView = SurfaceView(context)
                addView(surfaceView)
                engine.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
                engine.startPreview()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun RemoteView(remoteUserMap: Map<Int, SurfaceView>, engine: RtcEngine) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .horizontalScroll(rememberScrollState())
    ) {
        remoteUserMap.forEach { (uid, surfaceView) ->
            AndroidView(
                factory = { surfaceView },
                modifier = Modifier.size(180.dp, 240.dp)
            )
            engine.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
        }
    }
}

@Composable
private fun UserControls(engine: RtcEngine) {
    var muted by remember { mutableStateOf(false) }
    var videoDisabled by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? ComponentActivity

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedButton(
            onClick = {
                muted = !muted
                engine.muteLocalAudioStream(muted)
            },
            shape = CircleShape,
            modifier = Modifier.size(50.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.outlinedButtonColors(if (muted) Color.Blue else Color.White)
        ) {
            Icon(
                imageVector = if (muted) Icons.Rounded.Close else Icons.Rounded.Add,
                contentDescription = null,
                tint = if (muted) Color.White else Color.Blue
            )
        }
        OutlinedButton(
            onClick = {
                engine.leaveChannel()
                activity?.finish()
            },
            shape = CircleShape,
            modifier = Modifier.size(70.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.outlinedButtonColors(Color.Red)
        ) {
            Icon(Icons.Rounded.Call, contentDescription = null, tint = Color.White)
        }
        OutlinedButton(
            onClick = {
                videoDisabled = !videoDisabled
                engine.muteLocalVideoStream(videoDisabled)
            },
            shape = CircleShape,
            modifier = Modifier.size(50.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.outlinedButtonColors(if (videoDisabled) Color.Blue else Color.White)
        ) {
            Icon(
                imageVector = if (videoDisabled) Icons.Rounded.Call else Icons.Rounded.Info,
                contentDescription = null,
                tint = if (videoDisabled) Color.White else Color.Blue
            )
        }
    }
}

@Composable
private fun AlertScreen(requester: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = requester) {
            Icon(Icons.Rounded.Warning, contentDescription = null)
            Text(text = "Permission Required")
        }
    }
}

@Composable
private fun UIRequirePermissions(
    permissions: Array<String>,
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable (requester: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    var grantState by remember {
        mutableStateOf(permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        })
    }

    if (grantState) {
        onPermissionGranted()
    } else {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                grantState = it.values.all { granted -> granted }
            }
        )
        onPermissionDenied { launcher.launch(permissions) }
    }
}
