package com.momentum.app.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Fade in animation
fun Modifier.animatedFadeIn(
    delay: Int = 0,
    duration: Int = 500
): Modifier = composed {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "fadeIn"
    )
    
    this.alpha(alpha)
}

// Slide up animation
fun Modifier.animatedSlideUp(
    delay: Int = 0,
    duration: Int = 500
): Modifier = composed {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }
    
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 100f,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "slideUp"
    )
    
    this.offset(y = offsetY.dp)
}

// Scale animation for buttons
fun Modifier.animatedScale(
    delay: Int = 0,
    duration: Int = 400
): Modifier = composed {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    this.scale(scale)
}

// Bounce effect on press
fun Modifier.bounceClick(scale: Float = 0.95f): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) scale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bounceClick"
    )
    
    this.scale(animatedScale)
}

// Shimmer loading animation
@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.3f),
            Color.LightGray.copy(alpha = 0.5f),
            Color.LightGray.copy(alpha = 0.3f)
        ),
        start = Offset(translateAnim - 100f, translateAnim - 100f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .background(brush, shape = RoundedCornerShape(8.dp))
    )
}

// Skeleton card for loading
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShimmerLoading(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
            )
            ShimmerLoading(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerLoading(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp)
                )
                ShimmerLoading(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp)
                )
            }
        }
    }
}

// Pulsing animation for loading indicators
@Composable
fun rememberPulseAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    return scale
}

// Staggered animation for lists
fun getStaggeredDelay(index: Int, baseDelay: Int = 50): Int {
    return index * baseDelay
}

// Swipe to dismiss modifier
fun Modifier.swipeToDismiss(
    onDismiss: () -> Unit,
    threshold: Float = 0.3f
): Modifier = composed {
    var offsetX by remember { mutableStateOf(0f) }
    var isDismissed by remember { mutableStateOf(false) }
    
    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isDismissed) 1000f else offsetX,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "swipeDismiss",
        finishedListener = {
            if (isDismissed) {
                onDismiss()
            }
        }
    )
    
    this
        .offset(x = animatedOffsetX.dp)
        .alpha(1f - (kotlin.math.abs(animatedOffsetX) / 1000f))
}

// Enter/Exit transitions for navigation
@OptIn(ExperimentalAnimationApi::class)
fun slideInFromRight(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(300))
}

@OptIn(ExperimentalAnimationApi::class)
fun slideOutToLeft(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(300))
}

@OptIn(ExperimentalAnimationApi::class)
fun slideInFromLeft(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(300))
}

@OptIn(ExperimentalAnimationApi::class)
fun slideOutToRight(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(300))
}