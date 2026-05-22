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


package com.starry.piggo.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

const val NAVIGATION_ANIM_DURATION = 240
const val NAVIGATION_FADE_DURATION = 150
const val NAVIGATION_SCALE_IN = 0.98f
const val NAVIGATION_SCALE_OUT = 0.995f

private val iOSLikeEaseOut = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
private val iOSLikeEaseInOut = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)

/**
 * Enter transition for the navigation animation
 */
fun enterTransition(): EnterTransition = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth / 12 },
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION,
        easing = iOSLikeEaseOut
    )
) + scaleIn(
    initialScale = NAVIGATION_SCALE_IN,
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION,
        easing = iOSLikeEaseOut
    )
) + fadeIn(
    animationSpec = tween(
        durationMillis = NAVIGATION_FADE_DURATION,
        easing = LinearOutSlowInEasing
    )
)

/**
 * Exit transition for the navigation animation
 */
fun exitTransition(): ExitTransition = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth / 18 },
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION,
        easing = iOSLikeEaseInOut
    )
) + scaleOut(
    targetScale = NAVIGATION_SCALE_OUT,
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION,
        easing = iOSLikeEaseInOut
    )
) + fadeOut(
    animationSpec = tween(
        durationMillis = NAVIGATION_FADE_DURATION,
        easing = LinearOutSlowInEasing
    )
)

/**
 * Enter transition for the pop navigation animation
 */
fun popEnterTransition(): EnterTransition = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth / 14 },
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION,
        easing = iOSLikeEaseOut
    )
) + scaleIn(
    initialScale = NAVIGATION_SCALE_OUT,
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION,
        easing = iOSLikeEaseOut
    )
) + fadeIn(
    animationSpec = tween(
        durationMillis = NAVIGATION_FADE_DURATION,
        easing = LinearOutSlowInEasing
    )
)

/**
 * Exit transition for the pop navigation animation
 */
fun popExitTransition(): ExitTransition = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth / 12 },
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION,
        easing = iOSLikeEaseInOut
    )
) + scaleOut(
    targetScale = NAVIGATION_SCALE_IN,
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION,
        easing = iOSLikeEaseInOut
    )
) + fadeOut(
    animationSpec = tween(
        durationMillis = NAVIGATION_FADE_DURATION,
        easing = LinearOutSlowInEasing
    )
)
