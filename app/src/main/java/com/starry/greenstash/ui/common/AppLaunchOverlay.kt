/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] StÉ‘rry ShivÉ‘m
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.starry.greenstash.ui.common

import android.animation.ValueAnimator
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.starry.greenstash.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppLaunchOverlay(
    readyToExit: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val overlayAlpha = remember { Animatable(1f) }
    val logoAlpha = remember { Animatable(1f) }
    val logoScale = remember { Animatable(1f) }
    val animationsEnabled = remember {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O || ValueAnimator.areAnimatorsEnabled()
    }

    LaunchedEffect(readyToExit, animationsEnabled) {
        if (!readyToExit) {
            return@LaunchedEffect
        }

        if (!animationsEnabled) {
            onFinished()
            return@LaunchedEffect
        }

        delay(LAUNCH_SETTLE_DELAY)
        launch {
            overlayAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = LAUNCH_EXIT_DURATION,
                    easing = launchEaseOut
                )
            )
        }
        launch {
            logoAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = LAUNCH_EXIT_DURATION,
                    easing = launchEaseOut
                )
            )
        }
        logoScale.animateTo(
            targetValue = LAUNCH_LOGO_EXIT_SCALE,
            animationSpec = tween(
                durationMillis = LAUNCH_EXIT_DURATION,
                easing = launchEaseOut
            )
        )
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = overlayAlpha.value }
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.launch_logo),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier
                .size(154.dp)
                .graphicsLayer {
                    alpha = logoAlpha.value
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                }
                .clip(CircleShape)
        )
    }
}

private const val LAUNCH_SETTLE_DELAY = 90L
private const val LAUNCH_EXIT_DURATION = 320
private const val LAUNCH_LOGO_EXIT_SCALE = 1.08f
private val launchEaseOut = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
