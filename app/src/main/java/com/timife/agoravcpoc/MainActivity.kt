package com.timife.agoravcpoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.timife.agoravcpoc.ui.theme.AgoraVCPOCTheme
import com.timife.agoravcpoc.viewmodel.AgoraVCViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.RtcEngine

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var mRtcEngine: RtcEngine? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: AgoraVCViewModel = hiltViewModel()
            AgoraVCPOCTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding -> JoinCallScreen()
                  /*  if (checkPermissions(this)) {
                        JoinCallScreen()
                        *//*VideoCallingScreen()*//*
                    }*/
                }
            }
        }
    }
}

// Create an instance of ChannelMediaOptions and configure it
val options = ChannelMediaOptions().apply {
    // Set the user role to BROADCASTER or AUDIENCE according to the use-case
    clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
    // In the live broadcast use-case, set the channel profile to COMMUNICATION (live broadcast use-case)
    channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
}

//mRtcEngine.joinChannel(token, channelName, 0, options)


// Use the temporary token to join the channel
// Specify the user ID yourself and ensure it is unique within the channel








@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AgoraVCPOCTheme {
        Greeting("Android")
    }
}