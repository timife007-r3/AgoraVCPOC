package com.timife.agoravcpoc

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.timife.agoravcpoc.viewmodel.AgoraVCViewModel

@Composable
fun JoinCallScreen(
    modifier: Modifier = Modifier,
    viewModel: AgoraVCViewModel = hiltViewModel()
){




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Agora Live Video Streaming", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Event Name", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(50.dp))
        InputFields(modifier)
    }

}

@Composable
private fun InputFields( modifier: Modifier = Modifier,){
    val context = LocalContext.current

    val channelNameState = remember { mutableStateOf(TextFieldValue()) }
    val userId = remember { mutableStateOf(TextFieldValue()) }
    val userRoleOptions = listOf("Broadcaster", "Audience")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(userRoleOptions[0]) }

    Column(
        modifier =modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center,
    )
    {
        Spacer(modifier = modifier.height(80.dp))

        TextField(
            value = channelNameState.value,
            onValueChange = { channelNameState.value = it },
            label = { Text("Channel Name ") },
            placeholder = { Text("test") },
            modifier = Modifier
                .align(
                    alignment = Alignment.CenterHorizontally
                )
                .fillMaxWidth()
        )
        Spacer(modifier = modifier.height(16.dp))

        TextField(
            value = userId.value,
            onValueChange = { userId.value = it },
            label = { Text("UserId") },
            placeholder = { Text("test") },
            modifier = Modifier
                .align(
                    alignment = Alignment.CenterHorizontally
                )
                .fillMaxWidth()
        )
        Spacer(modifier = modifier.height(16.dp))
        userRoleOptions.forEach{ text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) }
                    )
            )  {
                RadioButton(
                    selected = (
                            text == selectedOption),
                    modifier = modifier.padding(
                        horizontal = 25.dp,
                        vertical = 0.dp
                    ),
                    onClick = {
                        onOptionSelected(text)
                    }
                )

                Text(
                    text = text,
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    fontSize = 18.sp
                )
            }
        }


        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if ( channelNameState.value.text.isNotEmpty() && userId.value.text.isNotEmpty()){
                    val intent = Intent(context, VideoCallActivity::class.java)
                    intent.putExtra("ChannelName", channelNameState.value.text)
                    intent.putExtra("UserRole", selectedOption)
                    intent.putExtra("UserId", userId.value.text)
                    ContextCompat.startActivity(context, intent, Bundle())
                }

            },
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 10.dp
            )
        ) {
            Icon(Icons.Filled.ArrowForward, contentDescription = "Join", modifier = modifier.size(24.dp))
            Spacer(modifier = modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Join", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }

}