// Paquete de pantallas
package com.example.proyinte.ui.screens

// Clases de Android para abrir el selector de archivos y leer su flujo de bytes
import android.content.Context
import android.net.Uri
// Contratos estándar de Android para lanzar el selector de audio sin pedir permisos extra
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
// Íconos de barra superior, acciones de CRUD y salir de cuenta
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
// Componentes Material 3
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyinte.data.model.AudioAlarma
import java.io.File

// Pantalla "Configuracion" para el CRUD de audios del servidor y cierre de sesión
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioConfigScreen(
    // ViewModel con las operaciones GET, POST, PUT, DELETE hacia Spring Boot
    viewModel: AlarmViewModel,
    // Callback para volver a la pantalla principal de SAFTHINGS
    onBack: () -> Unit,
    // Callback para salir de la cuenta y regresar a Inicia Sesión
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Controla si se muestra la ventana emergente de Crear/Editar audio
    var mostrarDialogo by remember { mutableStateOf(false) }
    // Si es null crea uno nuevo; si tiene un AudioAlarma lo edita
    var audioAEditar by remember { mutableStateOf<AudioAlarma?>(null) }
    // Guarda el audio elegido cuando el usuario toca el bote de basura para confirmar
    var audioAEliminar by remember { mutableStateOf<AudioAlarma?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Barra superior con flecha atrás, ícono engrane + Configuración y botón salir [->]
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Configuración", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Botón salir de cuenta [->] dibujado arriba a la derecha en el boceto
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Salir de cuenta"
                        )
                    }
                }
            )
        },
        // Botón flotante "+" abajo a la derecha para subir un nuevo audio
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    audioAEditar = null
                    mostrarDialogo = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Subir audio")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Encabezado "Audios de alarma" con su botón de recargar a la derecha (como en el boceto)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Audios de alarma",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { viewModel.cargarAudios() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Recargar audios"
                    )
                }
            }

            // Muestra un spinner mientras se realiza una petición HTTP al servidor
            if (viewModel.cargando) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Si ocurrió un error de conexión, lo muestra en una tarjeta con botón OK
            viewModel.mensajeError?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.limpiarError() }) {
                            Text("OK")
                        }
                    }
                }
            }

            // Si la lista está vacía y no está cargando, muestra texto informativo
            if (viewModel.audios.isEmpty() && !viewModel.cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay audios guardados. Pulsa + para subir uno.")
                }
            } else {
                // Lista de audios registrados en SQLite3 con tarjetas redondeadas y borde oscuro
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.audios, key = { it.id ?: it.hashCode() }) { audio ->
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
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Botón circular para elegir este audio como sonido activo (PUT)
                                RadioButton(
                                    selected = audio.activo,
                                    onClick = {
                                        audio.id?.let { id ->
                                            viewModel.actualizarAudio(
                                                id = id,
                                                nombre = audio.nombre,
                                                activo = true,
                                                archivo = null
                                            )
                                        }
                                    }
                                )
                                // Nombre y estado/nombre de archivo en el centro
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = audio.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (audio.activo) "Activo" else audio.archivo,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (audio.activo) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                                // Lápiz para abrir el formulario de edición (PUT)
                                IconButton(
                                    onClick = {
                                        audioAEditar = audio
                                        mostrarDialogo = true
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                // Bote de basura para confirmar eliminación (DELETE)
                                IconButton(onClick = { audioAEliminar = audio }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Abre el diálogo de subida o edición cuando mostrarDialogo sea true
    if (mostrarDialogo) {
        AudioFormDialog(
            audioAEditar = audioAEditar,
            cargando = viewModel.cargando,
            onDismiss = { mostrarDialogo = false },
            onGuardar = { nombre, activo, archivo ->
                val actual = audioAEditar
                if (actual?.id == null) {
                    // Alta nueva (POST): requiere archivo seleccionado
                    if (archivo != null) {
                        viewModel.crearAudio(nombre, activo, archivo) {
                            mostrarDialogo = false
                        }
                    }
                } else {
                    // Edición (PUT): el archivo es opcional
                    viewModel.actualizarAudio(actual.id, nombre, activo, archivo) {
                        mostrarDialogo = false
                    }
                }
            }
        )
    }

    // Diálogo de confirmación antes de borrar un audio del servidor
    audioAEliminar?.let { audio ->
        AlertDialog(
            onDismissRequest = { audioAEliminar = null },
            title = { Text("Eliminar audio") },
            text = { Text("¿Eliminar \"${audio.nombre}\" del servidor?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        audio.id?.let { viewModel.eliminarAudio(it) }
                        audioAEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { audioAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Ventana modal (AlertDialog) para capturar nombre, archivo de audio y si queda activo
@Composable
private fun AudioFormDialog(
    audioAEditar: AudioAlarma?,
    cargando: Boolean,
    onDismiss: () -> Unit,
    onGuardar: (nombre: String, activo: Boolean, archivo: File?) -> Unit
) {
    val context = LocalContext.current
    // Estado del campo nombre prellenado si se está editando
    var nombre by remember(audioAEditar) { mutableStateOf(audioAEditar?.nombre ?: "") }
    // Switch para marcar si este sonido será el activo al guardar
    var activo by remember(audioAEditar) { mutableStateOf(audioAEditar?.activo ?: true) }
    // Archivo temporal copiado desde el selector de archivos del teléfono
    var archivoSeleccionado by remember(audioAEditar) { mutableStateOf<File?>(null) }

    // Lanzador del selector de archivos nativo de Android filtrado a audios ("audio/*")
    val selectorAudio = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Copia los bytes del Uri elegido a un File temporal en cacheDir para enviarlo con Retrofit
            archivoSeleccionado = copiarAudioTemporal(context, it)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (audioAEditar == null) "Subir audio" else "Editar audio") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del sonido") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Botón que abre el selector del sistema buscando archivos de audio
                OutlinedButton(
                    onClick = { selectorAudio.launch("audio/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        archivoSeleccionado?.name
                            ?: audioAEditar?.archivo
                            ?: "Seleccionar archivo de audio"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Usar como alarma activa", modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = activo,
                        onCheckedChange = { activo = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                // Deshabilita Guardar si está cargando, falta el nombre o falta archivo al crear
                enabled = !cargando && nombre.isNotBlank() && (audioAEditar != null || archivoSeleccionado != null),
                onClick = { onGuardar(nombre.trim(), activo, archivoSeleccionado) }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Copia el contenido de un Uri del teléfono a un archivo temporal en cacheDir para subirlo con OkHttp
private fun copiarAudioTemporal(context: Context, uri: Uri): File? {
    val destino = File(context.cacheDir, "audio_${System.currentTimeMillis()}.mp3")
    return try {
        val input = context.contentResolver.openInputStream(uri) ?: return null
        input.use { stream ->
            destino.outputStream().use { output ->
                stream.copyTo(output)
            }
        }
        destino
    } catch (_: Exception) {
        null
    }
}
