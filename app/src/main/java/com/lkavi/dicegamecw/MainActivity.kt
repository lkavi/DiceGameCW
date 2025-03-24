package com.lkavi.dicegamecw

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GUI()
        }
    }
}

@Composable
fun GUI(){
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var openDialog by rememberSaveable { mutableStateOf(false) }
    var winThreshold by rememberSaveable { mutableStateOf(101) }
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    if (isLandscape) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 100.dp, top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.homedice),
                    contentDescription = "Dice Game Logo",
                    modifier = Modifier.size(500.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Welcome to the Dice Rolling Game", textAlign = TextAlign.Center, fontSize = 18.sp)
                Spacer(modifier = Modifier.size(16.dp))

                val context = LocalContext.current
                Button(onClick = {
                    val i = Intent(context, Game::class.java)
                    i.putExtra("WIN_THRESHOLD", winThreshold)
                    context.startActivity(i)
                },
                    colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("New Game",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(12.dp)
                            .width(100.dp)
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Button(onClick = { openDialog = true },
                    colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                    shape = RoundedCornerShape(10.dp)) {
                    Text("About",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(12.dp)
                            .width(100.dp))
                }

                Spacer(modifier = Modifier.size(16.dp))

                Button(
                    onClick = { showSettingsDialog = true },
                    colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Settings",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(65.dp)
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.homedice),
                contentDescription = "Dice Game Logo",
                modifier = Modifier
                    .padding(top = 200.dp)
                    .size(300.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Welcome to the Dice Rolling Game", textAlign = TextAlign.Center, fontSize = 20.sp)
                Spacer(modifier = Modifier.size(30.dp))
                val context = LocalContext.current
                Button(onClick = {
                    val i = Intent(context, Game::class.java)
                    i.putExtra("WIN_THRESHOLD", winThreshold)
                    context.startActivity(i)
                },
                    colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("New Game",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .width(100.dp)
                    )
                }
                Spacer(modifier = Modifier.size(30.dp))
                Button(onClick = { openDialog = true },
                    colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                    shape = RoundedCornerShape(10.dp)) {
                    Text("About",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .width(100.dp))
                }
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { showSettingsDialog = true },
                        colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Settings",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(8.dp)
                                .width(65.dp)
                        )
                    }
                }
            }
        }
    }

    if (openDialog) {
        AlertDialog(
            title = {
                Text(text = "About")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "IIT ID: 20231156\nUoW ID: w2051871",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "I confirm that I understand what plagiarism is and have read and " +
                                "understood the section on Assessment Offences in the Essential " +
                                "Information for Students. The work that I have submitted is " +
                                "entirely my own. Any work from other authors is duly referenced " +
                                "and acknowledged.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            onDismissRequest = {
                openDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                    }
                ) {
                    Text("Dismiss")
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.widthIn(max = 350.dp)
        )
    }

    if (showSettingsDialog) {
        var inputText by rememberSaveable { mutableStateOf(winThreshold.toString()) }
        var isError by rememberSaveable { mutableStateOf(false) }

        AlertDialog(
            title = { Text("Game Settings") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Win Threshold")
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                            isError = it.toIntOrNull() == null ||
                                    (it.toIntOrNull() ?: 0) < 50
                        },
                        label = { Text("Enter value (minimum 50)") },
                        isError = isError,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 8.dp)
                    )

                    if (isError) {
                        Text(
                            text = "Please enter a number of at least 50",
                            color = Color.Red,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            },
            onDismissRequest = { showSettingsDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!isError) {
                            winThreshold = inputText.toIntOrNull() ?: winThreshold
                            showSettingsDialog = false
                        }
                    },
                    enabled = !isError
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Cancel")
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.widthIn(max = 400.dp)
        )
    }
}