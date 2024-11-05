package com.example.fifteen_game

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fifteen_game.ui.theme.Fifteen_game

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign


import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme



import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*



import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Fifteen_game {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = "Fifteen Game",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xff5a88fe)
                                )
                            }
                        )
                    }
                ) { innerPadding ->
                    MainActivityContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    device = "spec:parent=pixel_5",
    showBackground = true, showSystemUi = true, locale = "uk"
)
@Composable
fun GreetingPreview() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.game_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xff2131dd)
                    )
                }
            )
        }
    ) { innerPadding ->
        MainActivityContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            engine = object : FifteenEngine by FifteenEngine.Companion {
                override fun getInitialState(): List<Int> =
                    buildList {
                        repeat(14) {add(it + 1)
                        }
                        add(16)
                        add(15)
                    }
            }
        )
    }
}


@Composable
fun MovesText(moveCount: Int) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(
                color = Color(0xff91b7dc), // Цвет фона
                shape = RoundedCornerShape(8.dp) // Закругление углов
            )
            .padding(16.dp) // Внутренние отступы
    ) {
        Text(
            text = "Moves: $moveCount",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold, // Жирный шрифт
            color = Color(0xff000000), // Цвет текста
            style = MaterialTheme.typography.titleLarge // Использование встроенной темы
        )
    }
}


@Composable
fun MainActivityContent(
    modifier: Modifier = Modifier,
    engine: FifteenEngine = FifteenEngine
) {
    var cells by remember { mutableStateOf(engine.getInitialState()) }
    val isWin by remember { derivedStateOf { engine.isWin(cells) } }
    var moveCount by remember { mutableStateOf(0) }

    if (!isWin) {
        Column(modifier = modifier) {
            MovesText(moveCount)
            Grid(
                cells,
                modifier.weight(1.0f)
            ) { chipNumber ->
                val newCells = engine.transitionState(cells, chipNumber)
                if (newCells != cells) {
                    cells = newCells
                    moveCount++
                }
            }
        }
    } else {
        VictoryScreen(onPlayAgain = {
            // Сбросьте состояние игры при нажатии кнопки
            cells = engine.getInitialState()
            moveCount = 0
        })
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun VictoryScreen(onPlayAgain: () -> Unit) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        // Задержка перед исчезновением текста
        delay(2000)
        visible = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Анимация текста "VICTORY"
            AnimatedVisibility(visible) {
                Text(
                    text = "VICTORY",
                    color = Color.White,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(32.dp)) // Промежуток перед кнопкой

            Button(
                onClick = { onPlayAgain() },
                modifier = Modifier
                    .padding(16.dp)
                    .height(56.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)), // Цвет кнопки
                shape = RoundedCornerShape(8.dp) // Закругление углов
            ) {
                Text(
                    text = "Play Again",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        }
    }
}

data class Position(val x: Int, val y: Int)

@Composable
fun Grid(
    cells: List<Int>,
    modifier: Modifier,
    onChipClick: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(4) { outerIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                repeat(4) { innerIndex ->
                    val index = outerIndex * 4 + innerIndex
                    Chip(cells[index], onChipClick)
                }
            }
        }
    }
}

@Composable
fun Chip(
    cell: Int,
    onClick: (Int) -> Unit
) {
    val size = 80.dp

    Box(
        modifier = Modifier
            .size(size)
    ) {
        Button(
            onClick = {
                onClick(cell)
                Log.i("MyButtonOnClick", "Clicking on chip with number $cell")
            },
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(containerColor = if (cell == 16) Color.Transparent else Color(0xFFFEE1FC)),
            border = if (cell != 16) BorderStroke(1.dp, Color.Black) else null,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            if (cell != 16) {
                Text(
                    text = cell.toString(),
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black // можно изменить цвет текста
                )
            }
        }
    }
}