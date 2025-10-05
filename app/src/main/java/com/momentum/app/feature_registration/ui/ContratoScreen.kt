package com.momentum.app.feature_registration.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ContratoScreen(
    onBackClick: (() -> Unit)? = null
) {
    val fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Encabezado del contrato
        Text(
            text = "CONTRATO DE TÉRMINOS Y CONDICIONES",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "Aplicación Momentum",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "Fecha de última actualización: $fechaActual",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Divider()
        
        // Introducción
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "INTRODUCCIÓN",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Este documento constituye un contrato legalmente vinculante entre usted (\"Usuario\" o \"Usted\") y Momentum App (\"Nosotros\", \"Nuestra\", \"Compañía\") para el uso de nuestra aplicación móvil y servicios relacionados.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Cláusula 1: Definiciones
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "1. DEFINICIONES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• Aplicación: Software móvil Momentum y todos sus componentes.\n" +
                            "• Servicios: Todas las funcionalidades proporcionadas por la aplicación.\n" +
                            "• Usuario: Persona física que utiliza la aplicación.\n" +
                            "• Datos Personales: Información que identifica al usuario.\n" +
                            "• Contenido: Toda información, texto, gráficos o datos en la aplicación.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Cláusula 2: Aceptación y Vigencia
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "2. ACEPTACIÓN Y VIGENCIA",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "2.1. Al registrarse y usar nuestra aplicación, usted acepta íntegramente estos términos.\n\n" +
                            "2.2. Este contrato entra en vigor desde el momento del registro.\n\n" +
                            "2.3. La vigencia será indefinida hasta la terminación por cualquiera de las partes.\n\n" +
                            "2.4. Nos reservamos el derecho de modificar estos términos con notificación previa de 30 días.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Cláusula 3: Obligaciones del Usuario
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "3. OBLIGACIONES DEL USUARIO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "3.1. Proporcionar información veraz y actualizada.\n\n" +
                            "3.2. Mantener la confidencialidad de sus credenciales de acceso.\n\n" +
                            "3.3. No usar la aplicación para actividades ilegales o fraudulentas.\n\n" +
                            "3.4. No intentar vulnerar la seguridad del sistema.\n\n" +
                            "3.5. Notificar inmediatamente cualquier uso no autorizado de su cuenta.\n\n" +
                            "3.6. Cumplir con todas las leyes aplicables en su jurisdicción.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Cláusula 4: Obligaciones de la Empresa
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "4. OBLIGACIONES DE LA EMPRESA",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "4.1. Proporcionar el servicio de manera continua y eficiente.\n\n" +
                            "4.2. Proteger los datos personales conforme a las leyes de privacidad.\n\n" +
                            "4.3. Mantener la seguridad y integridad de la plataforma.\n\n" +
                            "4.4. Notificar sobre cambios significativos en el servicio.\n\n" +
                            "4.5. Proporcionar soporte técnico durante horarios comerciales.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Cláusula 5: Tratamiento de Datos Personales
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "5. TRATAMIENTO DE DATOS PERSONALES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "5.1. Recopilamos datos necesarios para la prestación del servicio.\n\n" +
                            "5.2. Los datos se utilizan exclusivamente para los fines declarados.\n\n" +
                            "5.3. No compartimos información con terceros sin consentimiento.\n\n" +
                            "5.4. Implementamos medidas de seguridad para proteger sus datos.\n\n" +
                            "5.5. Usted tiene derecho a acceder, rectificar y eliminar sus datos.\n\n" +
                            "5.6. Cumplimos con las regulaciones de protección de datos aplicables.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Cláusula 6: Limitación de Responsabilidad
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "6. LIMITACIÓN DE RESPONSABILIDAD",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "6.1. La aplicación se proporciona \"tal como está\" sin garantías.\n\n" +
                            "6.2. No somos responsables por daños indirectos o consecuenciales.\n\n" +
                            "6.3. Nuestra responsabilidad máxima no excederá el monto pagado por el usuario.\n\n" +
                            "6.4. No garantizamos la disponibilidad ininterrumpida del servicio.\n\n" +
                            "6.5. El usuario asume el riesgo del uso de la aplicación.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Cláusula 7: Propiedad Intelectual
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "7. PROPIEDAD INTELECTUAL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "7.1. Todos los derechos de propiedad intelectual pertenecen a Momentum App.\n\n" +
                            "7.2. Se prohíbe la reproducción, distribución o modificación del software.\n\n" +
                            "7.3. El usuario recibe una licencia limitada y revocable de uso.\n\n" +
                            "7.4. Las marcas comerciales son propiedad de sus respectivos dueños.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Cláusula 8: Terminación del Contrato
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "8. TERMINACIÓN DEL CONTRATO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "8.1. Cualquiera de las partes puede terminar este contrato en cualquier momento.\n\n" +
                            "8.2. La terminación por parte del usuario requiere eliminar la cuenta.\n\n" +
                            "8.3. Podemos suspender o terminar cuentas por violación de términos.\n\n" +
                            "8.4. Tras la terminación, cesarán todos los derechos y obligaciones.\n\n" +
                            "8.5. Los datos pueden conservarse según las leyes aplicables.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Cláusula 9: Resolución de Disputas
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "9. RESOLUCIÓN DE DISPUTAS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "9.1. Las disputas se resolverán preferentemente mediante mediación.\n\n" +
                            "9.2. Se aplicará la legislación del país de domicilio de la empresa.\n\n" +
                            "9.3. Los tribunales competentes serán los del domicilio de la empresa.\n\n" +
                            "9.4. El usuario renuncia a acciones colectivas o de clase.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Cláusula 10: Disposiciones Generales
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "10. DISPOSICIONES GENERALES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "10.1. Si alguna cláusula es inválida, el resto del contrato permanece vigente.\n\n" +
                            "10.2. Este contrato constituye el acuerdo completo entre las partes.\n\n" +
                            "10.3. Las modificaciones deben realizarse por escrito.\n\n" +
                            "10.4. El idioma oficial del contrato es el español.\n\n" +
                            "10.5. Los encabezados son solo para referencia.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Información de contacto
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "INFORMACIÓN DE CONTACTO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Empresa: Momentum App S.L.\n" +
                            "Dirección: Calle Tecnología 123, 28001 Madrid, España\n" +
                            "Email: legal@momentumapp.com\n" +
                            "Teléfono: +34 900 123 456\n" +
                            "Horario de atención: Lunes a Viernes, 9:00 - 18:00 CET",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Declaración de aceptación
        Text(
            text = "Al marcar la casilla de aceptación en el formulario de registro, usted declara haber leído, entendido y aceptado todos los términos y condiciones establecidos en este contrato.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        onBackClick?.let {
            Button(
                onClick = it,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver al Registro")
            }
        }
    }
}