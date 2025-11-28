package com.momentum.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.momentum.app.data.model.*
import com.momentum.app.navigation.Routes
import com.momentum.app.ui.animations.*
import com.momentum.app.viewmodel.ProgressViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgresoScreen(
    navController: NavController,

    viewModel: BienestarViewModel,
    progressViewModel: ProgressViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val progressState by progressViewModel.uiState.collectAsState()
    val stats = progressState.stats
    
    // Inicializar con el usuario actual
    LaunchedEffect(estado.perfil.correo) {
        if (estado.perfil.correo.isNotEmpty()) {
            progressViewModel.initializeUser(estado.perfil.correo)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mi Progreso",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { progressViewModel.refreshStats() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (progressState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. RESUMEN MENSUAL
                item {
                    MonthSummaryCard(
                        totalEntries = stats.totalEntriesThisMonth,
                        predominantMood = stats.predominantMood,
                        breathingSessions = stats.breathingSessions,
                        currentStreak = stats.currentStreak
                    )
                }
                
                // 2. GRÁFICO DE ESTADOS DE ÁNIMO
                item {
                    MoodDistributionCard(
                        moodDistribution = stats.moodDistribution
                    )
                }
                
                // 3. EVOLUCIÓN SEMANAL
                item {
                    WeeklyEvolutionCard(
                        weeklyData = stats.weeklyEvolution
                    )
                }
                
                // 4. ESTADÍSTICAS AVANZADAS
                item {
                    AdvancedStatsCard(
                        mostActiveDay = stats.mostActiveDayOfWeek,
                        preferredHour = stats.preferredHour,
                        longestStreak = stats.longestStreak
                    )
                }
                
                // 5. LOGROS
                item {
                    AchievementsCard(
                        achievements = stats.achievements
                    )
                }
                // ... tus otros item { } de la LazyColumn ...

                item {
                    // Espacio antes del botón (para separarlo de las tarjetas)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Button(
                        onClick = {
                            // 👉 Navegar a la pantalla que habla con el backend
                            navController.navigate(Routes.RemoteMoods.name)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = "Ver estados emocionales (Backend)")
                    }
                }

                // Espacio para bottom navigation
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
                item {
                    Button(
                        onClick = {
                            navController.navigate(Routes.RemoteMoods.name)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Ver estados emocionales (Backend)")
                    }
                }

            }
        }

        // Bottom Navigation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            BottomNavigationBar(
                currentRoute = Routes.Progreso.name,
                navController = navController
            )
        }
    }
}

// ==================== COMPOSABLES AUXILIARES ====================

@Composable
private fun MonthSummaryCard(
    totalEntries: Int,
    predominantMood: String,
    breathingSessions: Int,
    currentStreak: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animatedSlideUp(delay = 100),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Resumen de este mes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryStatItem(
                    value = totalEntries.toString(),
                    label = "Entradas",
                    icon = Icons.Default.Edit
                )
                
                SummaryStatItem(
                    value = predominantMood.ifEmpty { "-" },
                    label = "Estado",
                    icon = null
                )
                
                SummaryStatItem(
                    value = breathingSessions.toString(),
                    label = "Respiración",
                    icon = Icons.Default.Favorite
                )
                
                SummaryStatItem(
                    value = "$currentStreak días",
                    label = "Racha",
                    icon = Icons.Default.Star
                )
            }
        }
    }
}

@Composable
private fun SummaryStatItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = if (icon == null) 32.sp else 24.sp
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MoodDistributionCard(
    moodDistribution: Map<String, MoodStatistic>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animatedSlideUp(delay = 200)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Distribución de Estados de Ánimo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            if (moodDistribution.isEmpty()) {
                Text(
                    "Aún no hay datos suficientes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                moodDistribution.values.forEach { stat ->
                    MoodBarItem(stat)
                }
            }
        }
    }
}

@Composable
private fun MoodBarItem(stat: MoodStatistic) {
    val animatedProgress = remember { Animatable(0f) }
    
    LaunchedEffect(stat.percentage) {
        animatedProgress.animateTo(
            targetValue = stat.percentage / 100f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stat.emoji,
                    fontSize = 24.sp
                )
                Text(
                    text = "${stat.count} veces",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Text(
                text = String.format("%.1f%%", stat.percentage),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(stat.color)
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.value)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(stat.color))
            )
        }
    }
}

@Composable
private fun WeeklyEvolutionCard(
    weeklyData: List<WeekDayStat>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animatedSlideUp(delay = 300)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Evolución Semanal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            if (weeklyData.isEmpty()) {
                Text(
                    "Aún no hay datos suficientes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                WeeklyChart(weeklyData)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weeklyData.forEach { dayStat ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = dayStat.predominantEmoji.ifEmpty { "-" },
                                fontSize = 16.sp
                            )
                            Text(
                                text = dayStat.dayOfWeek,
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

@Composable
private fun WeeklyChart(weeklyData: List<WeekDayStat>) {
    val maxCount = weeklyData.maxOfOrNull { it.entryCount } ?: 1
    
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val barWidth = size.width / (weeklyData.size * 2f)
        val spacing = barWidth
        
        weeklyData.forEachIndexed { index, dayStat ->
            val barHeight = if (maxCount > 0) {
                (dayStat.entryCount.toFloat() / maxCount) * size.height * 0.8f
            } else {
                0f
            }
            
            val x = index * (barWidth + spacing) + spacing / 2
            val y = size.height - barHeight
            
            // Barra
            drawRect(
                color = if (dayStat.entryCount > 0) Color(0xFF4CAF50) else Color.LightGray,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

@Composable
private fun AdvancedStatsCard(
    mostActiveDay: DayOfWeekStat?,
    preferredHour: HourStat?,
    longestStreak: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animatedSlideUp(delay = 400)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Estadísticas Avanzadas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            StatRow(
                icon = Icons.Default.DateRange,
                label = "Día más activo",
                value = mostActiveDay?.dayName ?: "N/A",
                detail = mostActiveDay?.let { "${it.entryCount} entradas" }
            )
            
            HorizontalDivider()
            
            StatRow(
                icon = Icons.Default.Schedule,
                label = "Hora preferida",
                value = preferredHour?.timeRange ?: "N/A",
                detail = preferredHour?.let { "${it.count} entradas" }
            )
            
            HorizontalDivider()
            
            StatRow(
                icon = Icons.Default.Star,
                label = "Racha más larga",
                value = "$longestStreak días",
                detail = "¡Sigue así!"
            )
        }
    }
}

@Composable
private fun StatRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    detail: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (detail != null) {
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AchievementsCard(
    achievements: List<Achievement>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animatedSlideUp(delay = 500)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Logros",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            if (achievements.isEmpty()) {
                Text(
                    "Sigue escribiendo para desbloquear logros",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                achievements.forEach { achievement ->
                    AchievementItem(achievement)
                }
            }
        }
    }
}

@Composable
private fun AchievementItem(achievement: Achievement) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (achievement.isUnlocked)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = achievement.icon,
                fontSize = 24.sp,
                modifier = Modifier.alpha(if (achievement.isUnlocked) 1f else 0.3f)
            )
        }
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (achievement.isUnlocked)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (!achievement.isUnlocked && achievement.progress > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                
                LinearProgressIndicator(
                    progress = { achievement.progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
        
        if (achievement.isUnlocked) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Desbloqueado",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}