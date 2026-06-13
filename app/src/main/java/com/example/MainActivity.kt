package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private val viewModel: SaludViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar() }
                ) { innerPadding ->
                    HealthApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HealthApp(
    viewModel: SaludViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf("Dashboard") }
    val paciente by viewModel.paciente.collectAsStateWithLifecycle()
    val allGlucosa by viewModel.allGlucosa.collectAsStateWithLifecycle()
    val allPresion by viewModel.allPresion.collectAsStateWithLifecycle()
    val allMedicamentos by viewModel.allMedicamentos.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Header(paciente)

        Box(modifier = Modifier.weight(1f)) {
            when (currentScreen) {
                "Dashboard" -> DashboardScreen(allGlucosa, allPresion, viewModel)
                "Medicamentos" -> MedicamentosScreen(allMedicamentos, viewModel)
                "Historial" -> HistorialScreen(allGlucosa, allPresion, viewModel)
            }
        }

        // Simple navigation simulation within the Column for this example
        TabRow(selectedTabIndex = when(currentScreen) {
            "Dashboard" -> 0
            "Medicamentos" -> 1
            "Historial" -> 2
            else -> 0
        }) {
            Tab(selected = currentScreen == "Dashboard", onClick = { currentScreen = "Dashboard" }) {
                Text("Resumen", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = currentScreen == "Medicamentos", onClick = { currentScreen = "Medicamentos" }) {
                Text("Tratamiento", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = currentScreen == "Historial", onClick = { currentScreen = "Historial" }) {
                Text("Historial", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun Header(paciente: Paciente?) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Mi Monitor de Salud",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            paciente?.let {
                Text(
                    text = "${it.nombre} • ${it.diagnostico}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun DashboardScreen(
    glucosa: List<LecturaGlucosa>,
    presion: List<LecturaPresion>,
    viewModel: SaludViewModel
) {
    var showGlucosaDialog by remember { mutableStateOf(false) }
    var showPresionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Últimas Mediciones", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard(
                title = "Glucosa",
                value = if (glucosa.isNotEmpty()) "${glucosa.first().valor} mg/dL" else "--",
                icon = Icons.Default.Info,
                color = StatusGlucose,
                modifier = Modifier.weight(1f),
                onClick = { showGlucosaDialog = true }
            )
            MetricCard(
                title = "Presión Art.",
                value = if (presion.isNotEmpty()) "${presion.first().sistolica}/${presion.first().diastolica}" else "--",
                icon = Icons.Default.Favorite,
                color = StatusPressure,
                modifier = Modifier.weight(1f),
                onClick = { showPresionDialog = true }
            )
        }

        // Quick Analysis
        if (glucosa.isNotEmpty()) {
            val last = glucosa.first().valor
            val status = when {
                last < 70 -> "Baja (Hipoglucemia)" to StatusDanger
                last <= 130 -> "Normal" to StatusNormal
                else -> "Alta (Hiperglucemia)" to StatusDanger
            }
            StatusSummaryCard("Estado de Glucosa", status.first, status.second)
        }

        if (presion.isNotEmpty()) {
            val lastS = presion.first().sistolica
            val lastD = presion.first().diastolica
            val status = when {
                lastS < 120 && lastD < 80 -> "Óptima" to StatusNormal
                lastS <= 129 || lastD <= 84 -> "Normal" to StatusNormal
                else -> "Elevada" to StatusDanger
            }
            StatusSummaryCard("Estado de Presión", status.first, status.second)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { showGlucosaDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Registrar Glucosa")
        }
        Button(
            onClick = { showPresionDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HealthSecondary)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Registrar Presión")
        }
    }

    if (showGlucosaDialog) {
        AddGlucosaDialog(onDismiss = { showGlucosaDialog = false }, onSave = { v, n, t ->
            viewModel.agregarGlucosa(v, n, t)
            showGlucosaDialog = false
        })
    }
    if (showPresionDialog) {
        AddPresionDialog(onDismiss = { showPresionDialog = false }, onSave = { s, d, p, n ->
            viewModel.agregarPresion(s, d, p, n)
            showPresionDialog = false
        })
    }
}

@Composable
fun MetricCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Text(title, fontSize = 14.sp, color = HealthTextSecondary)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun StatusSummaryCard(title: String, status: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontSize = 12.sp, color = HealthTextSecondary)
                Text(status, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
fun MedicamentosScreen(medicamentos: List<Medicamento>, viewModel: SaludViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Tratamiento", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Añadir", tint = HealthPrimary, modifier = Modifier.size(32.dp))
            }
        }

        if (medicamentos.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No hay medicamentos registrados", color = HealthTextSecondary)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(medicamentos) { med ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = if (med.paraEnfermedad == "Diabetes") StatusGlucose else StatusPressure)
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(med.nombre, fontWeight = FontWeight.Bold)
                                Text("${med.dosis} - ${med.frecuencia}", fontSize = 12.sp)
                            }
                            IconButton(onClick = { viewModel.eliminarMedicamento(med.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = StatusDanger)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMedicamentoDialog(onDismiss = { showAddDialog = false }, onSave = { n, d, f, e ->
            viewModel.agregarMedicamento(n, d, f, e)
            showAddDialog = false
        })
    }
}

@Composable
fun HistorialScreen(glucosa: List<LecturaGlucosa>, presion: List<LecturaPresion>, viewModel: SaludViewModel) {
    var tabIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndex) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }) { Text("Glucosa", Modifier.padding(8.dp)) }
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }) { Text("Presión", Modifier.padding(8.dp)) }
        }

        if (tabIndex == 0) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(glucosa) { item ->
                    HistoryItem(
                        title = "${item.valor} mg/dL",
                        subtitle = "${item.tipoComida} • ${item.nota}",
                        date = formatEpoch(item.fecha),
                        color = StatusGlucose,
                        onDelete = { viewModel.eliminarGlucosa(item.id) }
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presion) { item ->
                    HistoryItem(
                        title = "${item.sistolica}/${item.diastolica} mmHg",
                        subtitle = "Pulso: ${item.pulso} • ${item.nota}",
                        date = formatEpoch(item.fecha),
                        color = StatusPressure,
                        onDelete = { viewModel.eliminarPresion(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItem(title: String, subtitle: String, date: String, color: Color, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 12.sp, color = HealthTextSecondary)
                Text(date, fontSize = 10.sp, color = HealthTextSecondary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
}

// --- DIALOGS ---

@Composable
fun AddGlucosaDialog(onDismiss: () -> Unit, onSave: (Double, String, String) -> Unit) {
    var valor by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Ayunas") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Glucosa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = valor,
                    onValueChange = { valor = it },
                    label = { Text("Valor (mg/dL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = nota,
                    onValueChange = { nota = it },
                    label = { Text("Nota/Comida") }
                )
                // Simplified type select
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Ayunas", "Post", "Noche").forEach { t ->
                        FilterChip(selected = tipo == t, onClick = { tipo = t }, label = { Text(t) })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(valor.toDoubleOrNull() ?: 0.0, nota, tipo) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun AddPresionDialog(onDismiss: () -> Unit, onSave: (Int, Int, Int, String) -> Unit) {
    var sis by remember { mutableStateOf("") }
    var dia by remember { mutableStateOf("") }
    var pulso by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Presión Art.") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = sis, onValueChange = { sis = it }, label = { Text("Sist.") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = dia, onValueChange = { dia = it }, label = { Text("Diast.") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                OutlinedTextField(value = pulso, onValueChange = { pulso = it }, label = { Text("Pulso") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = nota, onValueChange = { nota = it }, label = { Text("Nota") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onSave(sis.toIntOrNull() ?: 0, dia.toIntOrNull() ?: 0, pulso.toIntOrNull() ?: 0, nota) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun AddMedicamentoDialog(onDismiss: () -> Unit, onSave: (String, String, String, String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var freq by remember { mutableStateOf("") }
    var enf by remember { mutableStateOf("Diabetes") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Medicamento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                OutlinedTextField(value = dosis, onValueChange = { dosis = it }, label = { Text("Dosis (ej: 500mg)") })
                OutlinedTextField(value = freq, onValueChange = { freq = it }, label = { Text("Frecuencia (ej: Cada 12h)") })
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(selected = enf == "Diabetes", onClick = { enf = "Diabetes" }, label = { Text("Diabetes") })
                    FilterChip(selected = enf == "Hipertensión", onClick = { enf = "Hipertensión" }, label = { Text("Hipertensión") })
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(nombre, dosis, freq, enf) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun BottomNavigationBar() {
    // Not strictly used but kept for scaffold structure
}

fun formatEpoch(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
