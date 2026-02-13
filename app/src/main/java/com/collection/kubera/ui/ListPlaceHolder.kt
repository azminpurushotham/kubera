//package com.collection.kubera.ui
//
//import androidx.compose.animation.core.LinearEasing
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun ListPlaceHolder(){
//    val shimmerColors = listOf(
//        Color.LightGray.copy(alpha = 0.6f),
//        Color.Gray.copy(alpha = 0.3f),
//        Color.LightGray.copy(alpha = 0.6f)
//    )
//
//    val transition = rememberInfiniteTransition()
//    val shimmerX = transition.animateFloat(
//        initialValue = -300f,
//        targetValue = 300f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 1000, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        )
//    )
//
//    val brush = Brush.linearGradient(
//        colors = shimmerColors,
//        start = Offset(0),360F),
//        end = shimmerX.value + 300f
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(30.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .background(brush)
//    )
//}