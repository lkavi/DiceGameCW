package com.lkavi.dicegamecw

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlin.random.Random

class Game : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val winThreshold = intent.getIntExtra("WIN_THRESHOLD", 101)
            GameGUI(winThreshold)
        }
    }

    @Composable
    fun GameGUI(winThreshold: Int) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val scrollState = rememberScrollState()

        var playerDiceValues by rememberSaveable { mutableStateOf(listOf<Int>()) }
        var computerDiceValues by rememberSaveable { mutableStateOf(listOf<Int>()) }
        var playerScore by rememberSaveable { mutableStateOf(0) }
        var computerScore by rememberSaveable { mutableStateOf(0) }
        var computerWins by rememberSaveable { mutableStateOf(0) }
        var humanWins by rememberSaveable { mutableStateOf(0) }
        var throwNo by rememberSaveable { mutableStateOf(0) }
        var reThrowNo by rememberSaveable { mutableStateOf(0) }
        var gameFinishedDialog by rememberSaveable { mutableStateOf(false) }
        var tieBreakDialog by rememberSaveable { mutableStateOf(false) }
        var hasScored by rememberSaveable { mutableStateOf(false) }
        var selectedDiceIndices by rememberSaveable { mutableStateOf(setOf<Int>()) }

        if (isLandscape) {
            // Landscape layout - dice on left, controls on right
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Left side - Dice displays
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Player dice
                    if (playerDiceValues.isNotEmpty()) {
                        Text("Player Dice:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.size(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            playerDiceValues.forEachIndexed { index, value ->
                                val isSelected = selectedDiceIndices.contains(index)
                                Box(
                                    modifier = Modifier
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = if (isSelected) Color.Blue else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            selectedDiceIndices = if (isSelected) {
                                                selectedDiceIndices - index
                                            } else if (selectedDiceIndices.size < 4) {
                                                selectedDiceIndices + index
                                            } else {
                                                selectedDiceIndices
                                            }
                                        }
                                ) {
                                    DieImage(value, modifier = Modifier.size(50.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Select up to 4 dice to keep (${selectedDiceIndices.size}/4)",
                            fontSize = 14.sp,
                            color = if (selectedDiceIndices.size >= 4) Color.Red else Color.Gray
                        )
                        Text("Values: ${playerDiceValues.joinToString(" ")}", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.size(16.dp))

                    // Computer dice
                    if (computerDiceValues.isNotEmpty()) {
                        Text("Computer Dice:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.size(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (value in computerDiceValues) {
                                DieImage(value, modifier = Modifier.size(50.dp))
                            }
                        }

                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Values: ${computerDiceValues.joinToString(" ")}", fontSize = 14.sp)
                    }
                }

                // Right side - Score and controls
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Scores section
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth(0.5f)
                                .background(
                                    color = Color.Black,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                "Wins",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "H : ${humanWins}",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    "C : ${computerWins}",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Column(
                            Modifier
                                .fillMaxWidth(1f)
                                .background(
                                    color = Color.Black,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Score",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Right,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "H : ${playerScore}",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    "C : ${computerScore}",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Target section
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth(1f)
                                .background(
                                    color = Color.Black,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Target : ${winThreshold}",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White)
                        }
                    }

                    // Game controls (Throw, Re-throw, Score buttons)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    ) {
                        if (throwNo > 0 && reThrowNo < 2 && !hasScored) {
                            Button(
                                onClick = {
                                    if (reThrowNo < 2 && gameFinishedDialog == false) {
                                        playerDiceValues = generateUnselectedDiceNumbers(playerDiceValues, selectedDiceIndices)
                                        reThrowNo++

                                        if (reThrowNo == 2) {
                                            playerScore += playerDiceValues.sum()
                                            val remainingRerolls = 2
                                            for (i in 0 until remainingRerolls) {
                                                if (Random.nextBoolean()) {
                                                    val computerSelectedDiceIndices = mutableSetOf<Int>()

                                                    for (diceIndex in computerDiceValues.indices) {
                                                        if (Random.nextBoolean()) {
                                                            computerSelectedDiceIndices.add(diceIndex)
                                                        }
                                                    }

                                                    computerDiceValues = computerDiceValues.mapIndexed { index, value ->
                                                        if (index in computerSelectedDiceIndices) {
                                                            value
                                                        } else {
                                                            1 + Random.nextInt(6)
                                                        }
                                                    }
                                                } else {
                                                    break
                                                }
                                            }
                                            computerScore += computerDiceValues.sum()
                                            hasScored = true

                                            if (computerScore >= winThreshold && playerScore >= winThreshold && computerScore == playerScore) {
                                                tieBreakDialog = true
                                            } else {
                                                if (computerScore >= winThreshold || playerScore >= winThreshold) {
                                                    if (computerScore > playerScore) computerWins++ else humanWins++
                                                    gameFinishedDialog = true
                                                }
                                            }

                                            selectedDiceIndices = emptySet()
                                            reThrowNo = 0
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Text(
                                    text = "Re-Throw",
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                        }

                        if (throwNo == 0 || hasScored) {
                            Button(
                                onClick = {
                                    playerDiceValues = emptyList()
                                    computerDiceValues = emptyList()
                                    selectedDiceIndices = emptySet()
                                    playerDiceValues = generateDiceNumbers()
                                    computerDiceValues = generateDiceNumbers()
                                    hasScored = false
                                    throwNo++
                                },
                                colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Text(
                                    text = "Throw",
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                        }

                        Button(
                            onClick = {
                                playerScore += playerDiceValues.sum()
                                val remainingRerolls = 2 - reThrowNo
                                for (i in 0 until remainingRerolls) {
                                    if (Random.nextBoolean()) {
                                        val computerSelectedDiceIndices = mutableSetOf<Int>()
                                        for (diceIndex in computerDiceValues.indices) {
                                            if (Random.nextBoolean()) {
                                                computerSelectedDiceIndices.add(diceIndex)
                                            }
                                        }
                                        computerDiceValues = computerDiceValues.mapIndexed { index, value ->
                                            if (index in computerSelectedDiceIndices) {
                                                value
                                            } else {
                                                1 + Random.nextInt(6)
                                            }
                                        }
                                    } else {
                                        break
                                    }
                                }
                                computerScore += computerDiceValues.sum()
                                hasScored = true

                                if (computerScore >= winThreshold && playerScore >= winThreshold && computerScore == playerScore) {
                                    tieBreakDialog = true
                                } else {
                                    if (computerScore >= winThreshold || playerScore >= winThreshold) {
                                        if (computerScore > playerScore) computerWins++ else humanWins++
                                        gameFinishedDialog = true
                                    }
                                }
                                selectedDiceIndices = emptySet()
                            },
                            enabled = playerDiceValues.isNotEmpty() && !hasScored,
                            colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text(
                                text = "Score",
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Portrait layout (original)
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .background(
                                color = Color.Black,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Wins",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "H : ${humanWins}",
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(
                                "C : ${computerWins}",
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Column(
                        Modifier
                            .fillMaxWidth(1f)
                            .background(
                                color = Color.Black,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Score",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Right,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "H : ${playerScore}",
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(
                                "C : ${computerScore}",
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth(1f)
                            .background(
                                color = Color.Black,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Target : ${winThreshold}",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.size(150.dp))
                Row(
                    Modifier.fillMaxHeight(0.5f),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (playerDiceValues.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                playerDiceValues.forEachIndexed { index, value ->
                                    val isSelected = selectedDiceIndices.contains(index)
                                    Box(
                                        modifier = Modifier
                                            .border(
                                                width = if (isSelected) 3.dp else 0.dp,
                                                color = if (isSelected) Color.Blue else Color.Transparent,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clickable {
                                                selectedDiceIndices = if (isSelected) {
                                                    selectedDiceIndices - index
                                                } else if (selectedDiceIndices.size < 4) {
                                                    selectedDiceIndices + index
                                                } else {
                                                    selectedDiceIndices
                                                }
                                            }
                                    ) {
                                        DieImage(value, modifier = Modifier.size(60.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Text("Player Dice: ${playerDiceValues.joinToString(" ")}")
                        }

                        if (playerDiceValues.isNotEmpty()) {
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Select up to 4 dice to keep (${selectedDiceIndices.size}/4)",
                                fontSize = 14.sp,
                                color = if (selectedDiceIndices.size >= 4) Color.Red else Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.size(32.dp))
                        if (computerDiceValues.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (value in computerDiceValues) {
                                    DieImage(value, modifier = Modifier.size(60.dp))
                                }
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Text("Computer Dice: ${computerDiceValues.joinToString(" ")}")
                        }
                    }
                }

                Spacer(modifier = Modifier.size(100.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (throwNo > 0 && reThrowNo < 2 && !hasScored) {
                        Button(
                            onClick = {
                                if (reThrowNo < 2 && gameFinishedDialog == false) {
                                    playerDiceValues = generateUnselectedDiceNumbers(playerDiceValues, selectedDiceIndices)
                                    reThrowNo++

                                    if (reThrowNo == 2) {
                                        playerScore += playerDiceValues.sum()
                                        val remainingRerolls = 2
                                        for (i in 0 until remainingRerolls) {
                                            if (Random.nextBoolean()) {
                                                val computerSelectedDiceIndices = mutableSetOf<Int>()

                                                for (diceIndex in computerDiceValues.indices) {
                                                    if (Random.nextBoolean()) {
                                                        computerSelectedDiceIndices.add(diceIndex)
                                                    }
                                                }

                                                computerDiceValues = computerDiceValues.mapIndexed { index, value ->
                                                    if (index in computerSelectedDiceIndices) {
                                                        value
                                                    } else {
                                                        1 + Random.nextInt(6)
                                                    }
                                                }
                                            } else {
                                                break
                                            }
                                        }
                                        computerScore += computerDiceValues.sum()
                                        hasScored = true

                                        if (computerScore >= winThreshold && playerScore >= winThreshold && computerScore == playerScore) {
                                            tieBreakDialog = true
                                        } else {
                                            if (computerScore >= winThreshold || playerScore >= winThreshold) {
                                                if (computerScore > playerScore) computerWins++ else humanWins++
                                                gameFinishedDialog = true
                                            }
                                        }

                                        selectedDiceIndices = emptySet()
                                        reThrowNo = 0
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Re-Throw",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .width(100.dp)
                            )
                        }
                    }
                    if (throwNo == 0 || hasScored) {
                        Button(
                            onClick = {
                                playerDiceValues = emptyList()
                                computerDiceValues = emptyList()
                                selectedDiceIndices = emptySet()
                                playerDiceValues = generateDiceNumbers()
                                computerDiceValues = generateDiceNumbers()
                                hasScored = false
                                throwNo++
                            },
                            colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Throw",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .width(100.dp)
                            )
                        }
                    }
                    Spacer(Modifier.size(10.dp))
                    Button(
                        onClick = {
                            playerScore += playerDiceValues.sum()
                            val remainingRerolls = 2 - reThrowNo
                            for (i in 0 until remainingRerolls) {
                                if (Random.nextBoolean()) {
                                    val computerSelectedDiceIndices = mutableSetOf<Int>()
                                    for (diceIndex in computerDiceValues.indices) {
                                        if (Random.nextBoolean()) {
                                            computerSelectedDiceIndices.add(diceIndex)
                                        }
                                    }
                                    computerDiceValues = computerDiceValues.mapIndexed { index, value ->
                                        if (index in computerSelectedDiceIndices) {
                                            value
                                        } else {
                                            1 + Random.nextInt(6)
                                        }
                                    }
                                } else {
                                    break
                                }
                            }
                            computerScore += computerDiceValues.sum()
                            hasScored = true

                            if (computerScore >= winThreshold && playerScore >= winThreshold && computerScore == playerScore) {
                                tieBreakDialog = true
                            } else {
                                if (computerScore >= winThreshold || playerScore >= winThreshold) {
                                    if (computerScore > playerScore) computerWins++ else humanWins++
                                    gameFinishedDialog = true
                                }
                            }
                            selectedDiceIndices = emptySet()
                        },
                        enabled = playerDiceValues.isNotEmpty() && !hasScored,
                        colors = ButtonDefaults.buttonColors(Color.Black, Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Score",
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(16.dp)
                                .width(100.dp),
                        )
                    }
                }
            }
        }

        val resultText = if (computerScore > playerScore && computerScore >= winThreshold) "You Lost!" else "You Won!"
        val textColor = if (computerScore > playerScore && computerScore >= winThreshold) Color(0xFFF44336) else Color(0xFF4CAF50)
        if (gameFinishedDialog) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AlertDialog(
                    modifier = Modifier
                        .widthIn(max = if (isLandscape) 600.dp else 600.dp)
                        .heightIn(max = if (isLandscape) 400.dp else 600.dp),
                    icon = {
                        Icon(Icons.Default.Info, contentDescription = "Example Icon")
                    },
                    title = {
                        Text(
                            text = "Game Finished",
                            fontSize = if (isLandscape) 18.sp else 24.sp
                        )
                    },
                    text = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = if (isLandscape) 180.dp else 400.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = resultText,
                                    fontSize = if (isLandscape) 24.sp else 50.sp,
                                    color = textColor,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )

                                // Scores
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Text(
                                        "Player: $playerScore",
                                        fontSize = if (isLandscape) 14.sp else 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Computer: $computerScore",
                                        fontSize = if (isLandscape) 14.sp else 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Dice display - more compact in landscape
                                if (isLandscape) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        // Player dice column
                                        if (playerDiceValues.isNotEmpty()) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Player Dice", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    for (value in playerDiceValues) {
                                                        DieImage(value, Modifier.size(30.dp))
                                                    }
                                                }
                                            }
                                        }

                                        // Computer dice column
                                        if (computerDiceValues.isNotEmpty()) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Computer Dice", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    for (value in computerDiceValues) {
                                                        DieImage(value, Modifier.size(30.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Portrait layout - dice stacked vertically
                                    if (playerDiceValues.isNotEmpty()) {
                                        Spacer(modifier = Modifier.size(16.dp))
                                        Text("Player Dice:", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.size(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            for (value in playerDiceValues) {
                                                DieImage(value, Modifier.size(40.dp))
                                            }
                                        }
                                        Text("Values: ${playerDiceValues.joinToString(" ")}", fontSize = 14.sp)
                                    }

                                    if (computerDiceValues.isNotEmpty()) {
                                        Spacer(modifier = Modifier.size(16.dp))
                                        Text("Computer Dice:", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.size(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            for (value in computerDiceValues) {
                                                DieImage(value, Modifier.size(40.dp))
                                            }
                                        }
                                        Text("Values: ${computerDiceValues.joinToString(" ")}", fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    },
                    onDismissRequest = {
                        gameFinishedDialog = true
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                gameFinishedDialog = false
                                playerScore = 0
                                computerScore = 0
                                computerDiceValues = emptyList()
                                playerDiceValues = emptyList()
                                throwNo = 0
                            }
                        ) {
                            Text("New Game", fontSize = if (isLandscape) 14.sp else 16.sp)
                        }
                    },
                    containerColor = Color.White,
                    properties = DialogProperties(
                        usePlatformDefaultWidth = false,
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    )
                )
            }
        }
        if (tieBreakDialog) {
            AlertDialog(
                icon = {
                    Icon(Icons.Default.Info, contentDescription = "Example Icon")
                },
                title = {
                    Text(text = "Tie!")
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Throw again below to determine a winner",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                onDismissRequest = {
                    tieBreakDialog = true
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            tieBreakDialog = false
                            selectedDiceIndices = emptySet()
                            playerDiceValues = generateDiceNumbers()
                            computerDiceValues = generateDiceNumbers()
                            playerScore += playerDiceValues.sum()
                            computerScore += computerDiceValues.sum()
                            hasScored = true
                            if (computerScore >= winThreshold && playerScore >= winThreshold && computerScore == playerScore) {
                                tieBreakDialog = true
                            } else {
                                if (computerScore >= winThreshold || playerScore >= winThreshold) {
                                    if (computerScore > playerScore) computerWins++
                                    else humanWins++
                                    gameFinishedDialog = true
                                }
                            }
                            throwNo++
                        }
                    ) {
                        Text("Throw Again")
                    }
                },
                properties = DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier.widthIn(max = 600.dp)
            )
        }
    }

    fun generateUnselectedDiceNumbers(
        currentDice: List<Int>,
        selectedIndices: Set<Int>
    ):List<Int> {
        return currentDice.mapIndexed { index, value ->
            if (index in selectedIndices) {
                value
            } else {
                1 + Random.nextInt(6)
            }
        }
    }

    fun generateDiceNumbers(): List<Int> {
        val numbers = mutableListOf<Int>()
        while (numbers.size < 5) {
            val newnumber = 1 + Random.nextInt(6)
            numbers.add(newnumber)
        }
        return numbers
    }

    @Composable
    fun DieImage(value: Int, modifier: Modifier = Modifier) {
        val diceResource = when (value) {
            1 -> R.drawable.one
            2 -> R.drawable.two
            3 -> R.drawable.three
            4 -> R.drawable.four
            5 -> R.drawable.five
            6 -> R.drawable.six
            else -> R.drawable.one // Fallback
        }

        Image(
            painter = painterResource(id = diceResource),
            contentDescription = "Dice showing $value",
            modifier = modifier.size(50.dp)
        )
    }
}