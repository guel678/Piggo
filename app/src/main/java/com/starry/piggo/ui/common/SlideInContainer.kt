/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
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


package com.starry.piggo.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay

@Composable
fun SlideInAnimatedContainer(
    initialDelay: Long, content:
    @Composable () -> Unit
) {
    val showContent = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        delay(initialDelay)
        showContent.value = true
    }

    AnimatedVisibility(
        visible = showContent.value,
        enter = slideInVertically(
            initialOffsetY = { height -> height / 10 },
            animationSpec = tween(durationMillis = 260, easing = smoothEaseOut)
        ) + scaleIn(
            initialScale = 0.985f,
            animationSpec = tween(durationMillis = 260, easing = smoothEaseOut)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(durationMillis = 180)
        ),
        exit = slideOutVertically(
            targetOffsetY = { height -> height / 12 },
            animationSpec = tween(durationMillis = 180, easing = smoothEaseInOut)
        ) + scaleOut(
            targetScale = 0.99f,
            animationSpec = tween(durationMillis = 180, easing = smoothEaseInOut)
        ) + fadeOut(
            animationSpec = tween(durationMillis = 140)
        ),
    ) {
        content()
    }
}

private val smoothEaseOut = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
private val smoothEaseInOut = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
