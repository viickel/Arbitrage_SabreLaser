package com.example.arbitragesabrelaser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.arbitragesabrelaser.ui.theme.ArbitrageSabreLaserTheme
import kotlinx.coroutines.delay
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State


class CombatViewModel : ViewModel() {
    // Variables d'état pour les scores des combattants
    private val _pointsCombattant1 = mutableStateOf(0)
    val pointsCombattant1: State<Int> = _pointsCombattant1

    private val _pointsCombattant2 = mutableStateOf(0)
    val pointsCombattant2: State<Int> = _pointsCombattant2

    // Variables d'état pour les noms des combattants
    private val _combattant1Name = mutableStateOf("Combattant Rouge")
    val combattant1Name: State<String> = _combattant1Name

    private val _combattant2Name = mutableStateOf("Combattant Vert")
    val combattant2Name: State<String> = _combattant2Name

    // Chronomètre
    private val _chronoTime = mutableStateOf(180L)
    val chronoTime: State<Long> = _chronoTime

    private val _isChronoRunning = mutableStateOf(false)
    val isChronoRunning: State<Boolean> = _isChronoRunning

    // Fonction pour modifier le nom du combattant 1
    fun setCombattant1Name(newName: String) {
        _combattant1Name.value = newName
    }

    // Fonction pour modifier le nom du combattant 2
    fun setCombattant2Name(newName: String) {
        _combattant2Name.value = newName
    }

    // Fonctions pour gérer les points
    fun addPointsToCombattant1(points: Int) {
        _pointsCombattant1.value += points
    }

    fun addPointsToCombattant2(points: Int) {
        _pointsCombattant2.value += points
    }

    fun removePointsFromCombattant1(points: Int) {
        _pointsCombattant1.value -= points
    }

    fun removePointsFromCombattant2(points: Int) {
        _pointsCombattant2.value -= points
    }

    // Fonctions pour gérer le chronomètre
    fun setChronoTime(time: Long) {
        _chronoTime.value = time
    }

    fun toggleChronoRunning() {
        _isChronoRunning.value = !_isChronoRunning.value
    }
}



@Composable
fun Chronometer(
    timeLeft: Long,
    isRunning: Boolean,
    onTimeUp: () -> Unit
) {
    // Lancer un effet pour décrémenter le temps
    LaunchedEffect(timeLeft, isRunning) {
        if (timeLeft > 0 && isRunning) {
            delay(1000L) // Attend 1 seconde
            onTimeUp() // Appelle la fonction quand le temps est écoulé
        }
    }

    // Calcul des minutes et secondes restantes
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    // Charger la police Digital-7
    val digital7FontFamily = FontFamily(
        Font(R.font.digital7, FontWeight.Normal)
    )

    // Affichage du chronomètre avec la police personnalisée
    Text(
        text = String.format("%02d:%02d", minutes, seconds),
        fontSize = 70.sp,
        fontFamily = digital7FontFamily, // Utiliser la police personnalisée
        style = MaterialTheme.typography.headlineMedium // Garde le style de base
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArbitrageSabreLaserTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CombatScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CombatScreen(modifier: Modifier = Modifier) {
    // Variables d'état pour les scores et le chronomètre
    var pointsCombattant1 by remember { mutableStateOf(0) }
    var pointsCombattant2 by remember { mutableStateOf(0) }

    // Variables d'état pour les noms des combattants
    var combattant1Name by remember { mutableStateOf("Combattant Rouge") }
    var combattant2Name by remember { mutableStateOf("Combattant Vert") }

    // Chronomètre
    var chronoTime by remember { mutableStateOf(180L) } // Chrono initial à 3 minutes (180s)
    var isChronoRunning by remember { mutableStateOf(false) } // État du chronomètre (démarré ou non)

    // États pour afficher les boîtes de dialogue de changement de nom et de temps
    var showDialogForCombattant1 by remember { mutableStateOf(false) }
    var showDialogForCombattant2 by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) } // Pour la boîte de dialogue du temps personnalisé

    // Variables pour les minutes et secondes personnalisées
    var customMinutes by remember { mutableStateOf("") }
    var customSeconds by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Chronomètre
        Chronometer(timeLeft = chronoTime, isRunning = isChronoRunning, onTimeUp = {
            if (chronoTime > 0) {
                chronoTime-- // Décrémenter le temps
            } else {
                isChronoRunning = false // Arrêter le chronomètre quand le temps est écoulé
            }
        })

        Spacer(modifier = Modifier.height(16.dp))

        // Row pour placer les deux combattants côte à côte
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Occupation égale des deux côtés
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CombatantColumn(
                combattantName = combattant1Name,
                points = pointsCombattant1,
                onNameChange = { showDialogForCombattant1 = true },
                onAddPoints = { pointsCombattant1 += it },
                onRemovePoints = { pointsCombattant1 -= it },
                backgroundColor = Color(red = 185, green = 0, blue = 64),
                isChronoRunning = isChronoRunning, // Passe l'état du chronomètre
                modifier = Modifier.weight(1f)
            )

            CombatantColumn(
                combattantName = combattant2Name,
                points = pointsCombattant2,
                onNameChange = { showDialogForCombattant2 = true },
                onAddPoints = { pointsCombattant2 += it },
                onRemovePoints = { pointsCombattant2 -= it },
                backgroundColor = Color(red = 0, green = 128, blue = 64),
                isChronoRunning = isChronoRunning, // Passe l'état du chronomètre
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton Start/Stop du chronomètre
        Button(onClick = {
            isChronoRunning = !isChronoRunning // Basculer entre démarrer et arrêter
        }) {
            Text(text = if (isChronoRunning) "STOP" else "START")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Boutons pour choisir le temps du chronomètre
        if (!isChronoRunning) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { chronoTime = 180L; isChronoRunning = false }) { // 3 minutes
                    Text(text = "3 min")
                }
                Button(onClick = { chronoTime = 30L; isChronoRunning = false }) { // 30 secondes
                    Text(text = "30 sec")
                }
                Button(onClick = {
                    showDialog = true // Afficher la boîte de dialogue pour un temps personnalisé
                }) {
                    Text(text = "Temps personnalisé")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row pour les sanctions des deux combattants côte à côte
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sanctions pour le combattant Rouge (donne des points au combattant Vert)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(red = 185, green = 0, blue = 64))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Sanctions - Combattant Rouge", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SanctionButton(
                        text = "Blanc",
                        containerColor = Color.White,
                        textColor = Color.Black,
                        onClick = { /* Carton blanc : Pas de point ajouté */ }
                    )
                    SanctionButton(
                        text = "Jaune",
                        containerColor = Color.Yellow,
                        textColor = Color.Black,
                        onClick = { pointsCombattant2 += 3 } // Carton jaune
                    )
                    SanctionButton(
                        text = "Rouge",
                        containerColor = Color.Red,
                        textColor = Color.White,
                        onClick = { pointsCombattant2 += 5 } // Carton rouge
                    )
                    SanctionButton(
                        text = "Noir",
                        containerColor = Color.Black,
                        textColor = Color.White,
                        onClick = { /* Carton noir : disqualification */ }
                    )
                }
            }

            // Sanctions pour le combattant Vert (donne des points au combattant Rouge)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(red = 0, green = 128, blue = 64))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Sanctions - Combattant Vert", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SanctionButton(
                        text = "Blanc",
                        containerColor = Color.White,
                        textColor = Color.Black,
                        onClick = { /* Carton blanc : Pas de point ajouté */ }
                    )
                    SanctionButton(
                        text = "Jaune",
                        containerColor = Color.Yellow,
                        textColor = Color.Black,
                        onClick = { pointsCombattant1 += 3 } // Carton jaune
                    )
                    SanctionButton(
                        text = "Rouge",
                        containerColor = Color.Red,
                        textColor = Color.White,
                        onClick = { pointsCombattant1 += 5 } // Carton rouge
                    )
                    SanctionButton(
                        text = "Noir",
                        containerColor = Color.Black,
                        textColor = Color.White,
                        onClick = { /* Carton noir : disqualification */ }
                    )
                }
            }
        }

        // Boîtes de dialogue pour changer les noms des combattants et le temps personnalisé
        // (Même logique que dans votre code original pour afficher les dialogues)

        if (showDialogForCombattant1) {
            var newName by remember { mutableStateOf(combattant1Name) }

            AlertDialog(
                onDismissRequest = { showDialogForCombattant1 = false },
                title = { Text("Changer le nom du combattant") },
                text = {
                    Column {
                        BasicTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            modifier = Modifier
                                .background(Color.LightGray)
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        combattant1Name = newName
                        showDialogForCombattant1 = false
                    }) {
                        Text("Confirmer")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialogForCombattant1 = false }) {
                        Text("Annuler")
                    }
                }
            )
        }

        if (showDialogForCombattant2) {
            var newName by remember { mutableStateOf(combattant2Name) }

            AlertDialog(
                onDismissRequest = { showDialogForCombattant2 = false },
                title = { Text("Changer le nom du combattant") },
                text = {
                    Column {
                        BasicTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            modifier = Modifier
                                .background(Color.LightGray)
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        combattant2Name = newName
                        showDialogForCombattant2 = false
                    }) {
                        Text("Confirmer")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialogForCombattant2 = false }) {
                        Text("Annuler")
                    }
                }
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Temps personnalisé") },
                text = {
                    Column {
                        BasicTextField(
                            value = customMinutes,
                            onValueChange = { customMinutes = it },
                            modifier = Modifier
                                .background(Color.LightGray)
                                .padding(8.dp),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                if (customMinutes.isEmpty()) {
                                    Text(text = "Minutes", color = Color.Gray)
                                }
                                innerTextField()
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        BasicTextField(
                            value = customSeconds,
                            onValueChange = { customSeconds = it },
                            modifier = Modifier
                                .background(Color.LightGray)
                                .padding(8.dp),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                if (customSeconds.isEmpty()) {
                                    Text(text = "Secondes", color = Color.Gray)
                                }
                                innerTextField()
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val minutes = customMinutes.toLongOrNull() ?: 0L
                        val seconds = customSeconds.toLongOrNull() ?: 0L
                        chronoTime = (minutes * 60) + seconds
                        isChronoRunning = false // Ne pas démarrer automatiquement
                        showDialog = false // Fermer la boîte de dialogue
                    }) {
                        Text("Confirmer")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}

@Composable
fun CombatantColumn(
    combattantName: String,
    points: Int,
    onNameChange: () -> Unit,
    onAddPoints: (Int) -> Unit,
    onRemovePoints: (Int) -> Unit,
    backgroundColor: Color,
    isChronoRunning: Boolean, // Ajout de ce paramètre
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nom du combattant
        Text(text = combattantName, style = MaterialTheme.typography.displayMedium)

        // Afficher le bouton "Changer nom" uniquement si le chronomètre est arrêté
        if (!isChronoRunning) {
            Button(onClick = onNameChange) {
                Text(text = "Changer nom")
            }
        }

        // Points du combattant
        Text(text = "$points Points", style = MaterialTheme.typography.displayLarge)

        // Ajouter des points (afficher uniquement si le chrono est lancé)
        if (isChronoRunning) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onAddPoints(1) }) { Text(text = "+1 Pts") }
                Button(onClick = { onAddPoints(3) }) { Text(text = "+3 Pts") }
                Button(onClick = { onAddPoints(5) }) { Text(text = "+5 Pts") }
            }

            // Enlever des points (afficher uniquement si le chrono est lancé)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onRemovePoints(1) }) { Text(text = "-1 Pts") }
                Button(onClick = { onRemovePoints(3) }) { Text(text = "-3 Pts") }
                Button(onClick = { onRemovePoints(5) }) { Text(text = "-5 Pts") }
            }
        }
    }
}



@Composable
fun SanctionButton(
    text: String,
    containerColor: Color,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(text = text, color = textColor)
    }
}

@Composable
fun SanctionButtonsForCombatant(
    combattantName: String,
    onGiveWhiteCard: () -> Unit,
    onGiveYellowCard: () -> Unit,
    onGiveRedCard: () -> Unit,
    onGiveBlackCard: () -> Unit,
    backgroundColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$combattantName - Sanctions", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SanctionButton(
                text = "Blanc",
                containerColor = Color.White,
                textColor = Color.Black,
                onClick = onGiveWhiteCard
            )
            SanctionButton(
                text = "Jaune",
                containerColor = Color.Yellow,
                textColor = Color.Black,
                onClick = onGiveYellowCard
            )
            SanctionButton(
                text = "Rouge",
                containerColor = Color.Red,
                textColor = Color.White,
                onClick = onGiveRedCard
            )
            SanctionButton(
                text = "Noir",
                containerColor = Color.Black,
                textColor = Color.White,
                onClick = onGiveBlackCard
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CombatScreenPreview() {
    ArbitrageSabreLaserTheme {
        CombatScreen()
    }
}
