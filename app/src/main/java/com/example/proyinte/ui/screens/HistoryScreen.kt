// Paquete de pantallas
package com.example.proyinte.ui.screens

// Borde redondeado y layouts para mostrar la lista de eventos
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
// LazyColumn permite hacer scroll si el historial crece con muchas activaciones
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
// Íconos para volver atrás y diferenciar alertas de activaciones normales
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Pantalla independiente "Historial de Activaciones" según el tercer panel del boceto
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    // ViewModel de donde leemos la lista viewModel.historial
    viewModel: AlarmViewModel,
    // Callback para regresar a la pantalla principal
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Barra superior con flecha para regresar y la marca SAFTHINGS
            TopAppBar(
                title = { SafThingsMarca() },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Contenedor principal de la pantalla de historial
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Encabezado "Historial de Activaciones" como en el boceto
            Text(
                text = "Historial de Activaciones",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Si aún no se ha activado el modo Safe, muestra mensaje centrado
            if (viewModel.historial.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no hay activaciones registradas.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Lista desplazable con una tarjeta por cada activación registrada
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Recorre cada HistorialEvento en orden (del más reciente al más antiguo)
                    items(viewModel.historial) { evento ->
                        // Tarjeta redondeada estilo cápsula con borde oscuro como en el dibujo
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFF1F2937),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Triángulo de advertencia rojo si sonó la alarma; palomita verde si no
                                Icon(
                                    imageVector = if (evento.seDisparo) {
                                        Icons.Default.Warning
                                    } else {
                                        Icons.Default.CheckCircle
                                    },
                                    contentDescription = null,
                                    tint = if (evento.seDisparo) {
                                        Color(0xFFEF4444)
                                    } else {
                                        Color(0xFF10B981)
                                    },
                                    modifier = Modifier.size(28.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                // Columna con el texto del evento y el resumen de hora/duración
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = evento.estado,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Inicio: ${evento.horaInicio} • Duración: ${formatearDuracion(evento.duracionSegundos)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Convierte segundos totales a texto legible ("9s" o "1m 15s")
private fun formatearDuracion(segundos: Long): String {
    if (segundos < 60) return "${segundos}s"
    val min = segundos / 60
    val seg = segundos % 60
    return "${min}m ${seg}s"
}
