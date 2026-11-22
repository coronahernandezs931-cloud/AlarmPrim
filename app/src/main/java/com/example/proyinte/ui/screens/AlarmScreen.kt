// Paquete de pantallas de la aplicación
package com.example.proyinte.ui.screens

// Canvas para dibujar la carita feliz o enojada igual que en el boceto
import androidx.compose.foundation.Canvas
// Modificadores de fondo, borde y clic para los controles personalizados
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
// Contenedores y espaciados estándar de Compose
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
// Formas circular y cápsula/píldora para la cara, el switch y el botón de historial
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
// Íconos de Material usados en la barra superior y controles
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Shield
// Componentes de interfaz Material 3
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
import androidx.compose.ui.draw.clip
// Tipos geométricos para ubicar ojos y boca dentro del Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Componente compartido con el escudo y nombre SAFTHINGS para usarlo en Login, Principal e Historial
@Composable
fun SafThingsMarca(
    tamanoIcono: Dp = 28.dp,
    estiloTexto: TextStyle = MaterialTheme.typography.titleLarge
) {
    // Alinea horizontalmente el ícono del escudo y el texto SAFTHINGS
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Ícono de escudo con contorno como en el diseño en papel
        Icon(
            imageVector = Icons.Outlined.Shield,
            contentDescription = null,
            modifier = Modifier.size(tamanoIcono)
        )
        // Espacio horizontal entre el escudo y las letras
        Spacer(modifier = Modifier.width(6.dp))
        // Nombre de la aplicación en negrita
        Text(
            text = "SAFTHINGS",
            style = estiloTexto,
            fontWeight = FontWeight.Bold
        )
    }
}

// OptIn requerido porque TopAppBar es API experimental en Material 3
@OptIn(ExperimentalMaterial3Api::class)
// Pantalla principal del sistema de vigilancia por movimiento
@Composable
fun AlarmScreen(
    // ViewModel compartido que contiene el estado del sensor y los audios
    viewModel: AlarmViewModel,
    // Callback para abrir la pantalla de Configuración de audios
    onOpenConfig: () -> Unit,
    // Callback para abrir la pantalla de Historial de activaciones
    onOpenHistory: () -> Unit,
    // Modificador opcional recibido desde MainActivity
    modifier: Modifier = Modifier
) {
    // Estructura base de pantalla con barra superior
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Barra superior con el logo a la izquierda y el engrane a la derecha
            TopAppBar(
                title = { SafThingsMarca() },
                actions = {
                    // Botón de engrane para ir a Configuración
                    IconButton(onClick = onOpenConfig) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Columna centrada que organiza carita, texto de estado y controles inferiores
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Carita circular que cambia de feliz a enojada cuando suena la alarma
            CaraSafThings(
                activo = viewModel.activo,
                alarmaSonando = viewModel.alarmaSonando
            )

            // Bloque central con el texto de estado y el audio seleccionado
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Muestra "Inicia el modo Safe", "Deja el celular quieto...", "Modo Safe activo" o "¡MOVIMIENTO DETECTADO!"
                Text(
                    text = viewModel.textoEstado,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Muestra cuál audio del servidor sonará si se mueve el celular
                Text(
                    text = "Audio: ${viewModel.audioActivo?.nombre ?: "Predeterminado"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Fila inferior con el switch ACTIVAR/DESACTIVAR a la izquierda y el botón Historial a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Botón tipo switch ovalado como en el boceto (ocupa el ancho disponible)
                SwitchModoSafe(
                    activo = viewModel.activo,
                    onToggle = { viewModel.cambiarEstado(!viewModel.activo) },
                    modifier = Modifier.weight(1f)
                )

                // Botón circular con la etiqueta "Historial" arriba del reloj
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Historial",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Círculo con borde oscuro e ícono de reloj/historial
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(width = 2.5.dp, color = Color(0xFF1F2937), shape = CircleShape)
                            .clickable { onOpenHistory() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Abrir historial",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

// Dibuja la carita circular del boceto usando Canvas de Compose
@Composable
private fun CaraSafThings(
    // Indica si el modo Safe está encendido
    activo: Boolean,
    // Indica si la alarma está disparada (carita enojada)
    alarmaSonando: Boolean
) {
    // Color de fondo suave dentro del círculo según el estado actual
    val colorFondo = when {
        alarmaSonando -> Color(0xFFFEE2E2)
        activo -> Color(0xFFD1FAE5)
        else -> Color.White
    }
    // Color oscuro usado para bordes, ojos y boca
    val colorTrazo = Color(0xFF1F2937)

    // Contenedor circular con borde grueso igual que el dibujo
    Box(
        modifier = Modifier
            .size(190.dp)
            .clip(CircleShape)
            .background(colorFondo)
            .border(width = 4.dp, color = colorTrazo, shape = CircleShape)
    ) {
        // Canvas donde dibujamos los ojos y la boca proporcionalmente al tamaño del círculo
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ancho = size.width
            val alto = size.height

            if (alarmaSonando) {
                // Ojos circulares para la expresión de alerta
                drawCircle(
                    color = colorTrazo,
                    radius = ancho * 0.065f,
                    center = Offset(ancho * 0.34f, alto * 0.42f)
                )
                drawCircle(
                    color = colorTrazo,
                    radius = ancho * 0.065f,
                    center = Offset(ancho * 0.66f, alto * 0.42f)
                )
                // Ceja izquierda inclinada hacia abajo (cara enojada)
                drawLine(
                    color = colorTrazo,
                    start = Offset(ancho * 0.23f, alto * 0.27f),
                    end = Offset(ancho * 0.43f, alto * 0.36f),
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )
                // Ceja derecha inclinada hacia abajo (cara enojada)
                drawLine(
                    color = colorTrazo,
                    start = Offset(ancho * 0.77f, alto * 0.27f),
                    end = Offset(ancho * 0.57f, alto * 0.36f),
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )
                // Boca abierta en arco hacia arriba simulando grito/alarma
                drawArc(
                    color = colorTrazo,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(ancho * 0.32f, alto * 0.57f),
                    size = Size(ancho * 0.36f, alto * 0.30f)
                )
            } else {
                // Ojo izquierdo alargado verticalmente (carita tranquila/feliz)
                drawRoundRect(
                    color = colorTrazo,
                    topLeft = Offset(ancho * 0.32f, alto * 0.28f),
                    size = Size(ancho * 0.07f, alto * 0.16f),
                    cornerRadius = CornerRadius(20f, 20f)
                )
                // Ojo derecho alargado verticalmente
                drawRoundRect(
                    color = colorTrazo,
                    topLeft = Offset(ancho * 0.61f, alto * 0.28f),
                    size = Size(ancho * 0.07f, alto * 0.16f),
                    cornerRadius = CornerRadius(20f, 20f)
                )
                // Sonrisa feliz en forma de medio círculo relleno hacia abajo
                drawArc(
                    color = colorTrazo,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(ancho * 0.30f, alto * 0.46f),
                    size = Size(ancho * 0.40f, alto * 0.30f)
                )
            }
        }
    }
}

// Botón switch tipo cápsula con flecha -> ACTIVAR o DESACTIVAR <- según el estado
@Composable
private fun SwitchModoSafe(
    // Estado actual de activación
    activo: Boolean,
    // Acción que se ejecuta al tocar la cápsula
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Contenedor en forma de píldora con borde oscuro
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(50))
            .background(if (activo) Color(0xFFD1FAE5) else Color.White)
            .border(width = 3.dp, color = Color(0xFF1F2937), shape = RoundedCornerShape(50))
            .clickable { onToggle() }
            .padding(horizontal = 8.dp)
    ) {
        if (!activo) {
            // Cuando está apagado, el círculo con flecha derecha va a la izquierda
            CirculoFlecha(icono = Icons.AutoMirrored.Filled.ArrowForward)
            // Etiqueta ACTIVAR centrada en el espacio restante
            Text(
                text = "ACTIVAR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        } else {
            // Cuando está encendido, el texto DESACTIVAR va primero
            Text(
                text = "DESACTIVAR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            // Y el círculo con flecha izquierda queda a la derecha
            CirculoFlecha(icono = Icons.AutoMirrored.Filled.ArrowBack)
        }
    }
}

// Pequeño círculo con borde que contiene la flecha dentro del switch
@Composable
private fun CirculoFlecha(icono: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(width = 2.5.dp, color = Color(0xFF1F2937), shape = CircleShape)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
    }
}
