// Paquete de pantallas
package com.example.proyinte.ui.screens

// Componentes de layout, espaciado y formas de Compose
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
// Opciones de teclado (correo y contraseña oculta)
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
// Manejo de estado local en Composable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

// Pantalla de inicio de sesión y registro basada en el primer boceto (SAFTHINGS)
@Composable
fun LoginScreen(
    // ViewModel que valida y guarda la sesión en SharedPreferences
    viewModel: AlarmViewModel,
    modifier: Modifier = Modifier
) {
    // Texto escrito por el usuario en el campo de correo
    var correo by remember { mutableStateOf("") }
    // Texto escrito en el campo de contraseña
    var contrasena by remember { mutableStateOf("") }
    // False = pantalla "Inicia Sesión"; True = pantalla "Regístrate"
    var esRegistro by remember { mutableStateOf(false) }

    // Estructura base para respetar márgenes de barra de estado del sistema
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        // Columna centrada vertical y horizontalmente con todos los elementos del formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo con escudo y título SAFTHINGS en tamaño grande
            SafThingsMarca(
                tamanoIcono = 48.dp,
                estiloTexto = MaterialTheme.typography.headlineMedium
            )

            // Espacio entre el logo y el título del formulario
            Spacer(modifier = Modifier.height(36.dp))

            // Título principal que cambia entre Login y Registro
            Text(
                text = if (esRegistro) "Regístrate" else "Inicia Sesión",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de texto para escribir el correo electrónico
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo de texto para la contraseña ocultando los caracteres con puntos
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            // Si existe un mensaje de error de validación, lo muestra en rojo
            viewModel.mensajeAuth?.let { error ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón ovalado "Siguiente" (o "Crear cuenta" en modo registro)
            Button(
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp),
                onClick = {
                    if (esRegistro) {
                        // Crea cuenta nueva en SharedPreferences y entra a la app
                        viewModel.registrarUsuario(correo, contrasena)
                    } else {
                        // Valida credenciales existentes en SharedPreferences
                        viewModel.iniciarSesion(correo, contrasena)
                    }
                }
            ) {
                Text(
                    text = if (esRegistro) "Crear cuenta" else "Siguiente",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Enlace de texto inferior para alternar entre Login y Registro
            TextButton(
                onClick = {
                    // Cambia de modo y limpia mensajes de error anteriores
                    esRegistro = !esRegistro
                    viewModel.limpiarMensajeAuth()
                }
            ) {
                Text(
                    text = if (esRegistro) {
                        "¿Ya tienes cuenta? Inicia sesión"
                    } else {
                        "¿Eres nuevo? Regístrate aquí"
                    }
                )
            }
        }
    }
}
