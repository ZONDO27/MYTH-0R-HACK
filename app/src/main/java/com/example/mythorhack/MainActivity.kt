package com.example.mythorhack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// -----------------------------
// Main Activity Entry Point
// -----------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MythOrHackApp() }
    }
}

// -----------------------------
// Data Model for Flashcards
// -----------------------------
data class Flashcard(
    val statement: String,
    val isHack: Boolean,
    val explanation: String
)

// -----------------------------
// Main App Composable
// -----------------------------
@Composable
fun MythOrHackApp() {
    var screen by remember { mutableStateOf("welcome") }
    var score by remember { mutableIntStateOf(0) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var paused by remember { mutableStateOf(false) }

    val flashcards = listOf(
        Flashcard("Putting coffee grounds in the fridge removes odors.", true, "Coffee grounds absorb unpleasant smells."),
        Flashcard("Microwaving your phone dries it faster after water damage.", false, "This damages your phone permanently."),
        Flashcard("Using vinegar can clean cloudy glassware effectively.", true, "Vinegar dissolves mineral deposits."),
        Flashcard("Charging your phone overnight ruins the battery.", false, "Modern phones stop charging automatically."),
        Flashcard("Applying toothpaste can remove minor scratches from CDs.", true, "Toothpaste acts as a mild abrasive."),
        Flashcard("Eating carrots improves night vision dramatically.", false, "Carrots help eye health but don’t give night vision."),
        Flashcard("Using baking soda removes stains from clothes.", true, "Baking soda neutralizes odors and lifts stains."),
        Flashcard("Leaving a spoon in champagne keeps it fizzy.", false, "Fizz escapes regardless of spoon placement."),
        Flashcard("Freezing batteries extends their lifespan.", false, "Cold temperatures can damage battery cells."),
        Flashcard("Rubbing banana peel on leather shoes makes them shine.", true, "Natural oils in banana peel polish leather."),
        Flashcard("Touching a frog gives you warts.", false, "Warts are caused by viruses, not frogs."),
        Flashcard("Putting salt in coffee reduces bitterness.", true, "Salt neutralizes bitter compounds."),
        Flashcard("Cracking your knuckles causes arthritis.", false, "No medical evidence supports this myth."),
        Flashcard("Using lemon juice removes rust from metal.", true, "Citric acid dissolves rust effectively.")
    )

    when (screen) {
        "welcome" -> WelcomeScreen(onStart = { screen = "quiz" })
        "quiz" -> FlashcardScreen(
            flashcards = flashcards,
            currentIndex = currentIndex,
            onAnswer = { correct ->
                if (correct) score++
            },
            onNext = {
                if (currentIndex < flashcards.lastIndex) currentIndex++ else screen = "score"
            },
            paused = paused,
            onPause = { paused = !paused },
            onStop = {
                screen = "welcome"
                score = 0
                currentIndex = 0
            }
        )
        "score" -> ScoreScreen(score, flashcards.size, onReview = { screen = "review" })
        "review" -> ReviewScreen(flashcards, onRestart = {
            screen = "welcome"
            score = 0
            currentIndex = 0
        })
    }
}

// -----------------------------
// Welcome Screen
// -----------------------------
@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    ScreenLayout(
        title = "Life Hack or Urban Myth?",
        description = "Can you tell a real life hack from an urban myth? Test your knowledge!",
        buttons = {
            Button(onClick = onStart, colors = ButtonDefaults.buttonColors(Color.Red)) {
                Text("Start", color = Color.White, fontSize = 22.sp)
            }
        }
    )
}

// -----------------------------
// Flashcard Question Screen
// -----------------------------
@Composable
fun FlashcardScreen(
    flashcards: List<Flashcard>,
    currentIndex: Int,
    onAnswer: (Boolean) -> Unit,
    onNext: () -> Unit,
    paused: Boolean,
    onPause: () -> Unit,
    onStop: () -> Unit
) {
    val currentCard = flashcards[currentIndex]
    var feedback by remember { mutableStateOf("") }

    ScreenLayout(
        title = "Flashcard Question",
        description = if (paused) "Paused — tap Resume to continue." else currentCard.statement,
        buttons = {
            if (!paused) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = {
                        val correct = currentCard.isHack
                        feedback = if (correct) "Correct! That's a real time-saver!" else "Wrong! That's just an urban myth."
                        onAnswer(correct)
                    }, colors = ButtonDefaults.buttonColors(Color(0xFFFF9800))) {
                        Text("Hack", color = Color.White, fontSize = 22.sp)
                    }
                    Button(onClick = {
                        val correct = !currentCard.isHack
                        feedback = if (correct) "Correct! That's a myth!" else "Wrong! That’s actually a hack."
                        onAnswer(correct)
                    }, colors = ButtonDefaults.buttonColors(Color(0xFFE91E63))) {
                        Text("Myth", color = Color.White, fontSize = 22.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(feedback, color = Color.Yellow, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onPause, colors = ButtonDefaults.buttonColors(Color.Yellow)) {
                    Text(if (paused) "Resume" else "Pause", color = Color.Black, fontSize = 22.sp)
                }
                Button(onClick = onStop, colors = ButtonDefaults.buttonColors(Color.Red)) {
                    Text("Stop", color = Color.White, fontSize = 22.sp)
                }
                Button(onClick = onNext, colors = ButtonDefaults.buttonColors(Color(0xFFFFC107))) {
                    Text("Next", color = Color.Black, fontSize = 22.sp)
                }
            }
        }
    )
}

// -----------------------------
// Score Screen
// -----------------------------
@Composable
fun ScoreScreen(score: Int, total: Int, onReview: () -> Unit) {
    val feedback = when {
        score == total -> "Master Hacker! Stay Safe Online!"
        score > total / 2 -> "Great job!"
        else -> "Keep practising!"
    }

    ScreenLayout(
        title = "Quiz Finished!",
        description = "You scored $score out of $total.\n$feedback",
        buttons = {
            Button(onClick = onReview, colors = ButtonDefaults.buttonColors(Color.Red)) {
                Text("Review", color = Color.White, fontSize = 22.sp)
            }
        }
    )
}

// -----------------------------
// Review Screen
// -----------------------------
@Composable
fun ReviewScreen(flashcards: List<Flashcard>, onRestart: () -> Unit) {
    ScreenLayout(
        title = "Review Answers",
        description = "Here are all the statements and their correct answers:",
        buttons = {
            Column(horizontalAlignment = Alignment.Start) {
                flashcards.forEach { card ->
                    Text(
                        text = "${card.statement}\nAnswer: ${if (card.isHack) "Hack" else "Myth"}\nExplanation: ${card.explanation}\n",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onRestart, colors = ButtonDefaults.buttonColors(Color.Red)) {
                    Text("Restart", color = Color.White, fontSize = 22.sp)
                }
            }
        }
    )
}

// -----------------------------
// Shared Layout for All Screens
// -----------------------------
@Composable
fun ScreenLayout(title: String, description: String, buttons: @Composable () -> Unit) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Yellow, Color(0xFFFFC107), Color(0xFFFF5722), Color(0xFFE91E63))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))
            Text(description, fontSize = 22.sp, color = Color.White)
            Spacer(modifier = Modifier.height(30.dp))
            buttons()
        }
    }
}
