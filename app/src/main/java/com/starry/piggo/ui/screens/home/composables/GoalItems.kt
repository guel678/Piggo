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


package com.starry.piggo.ui.screens.home.composables

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.starry.piggo.R
import com.starry.piggo.ui.theme.piggoFont
import com.starry.piggo.ui.theme.piggoNumberFont
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun GoalItemClassic(
    title: String,
    primaryText: String,
    secondaryText: String,
    goalProgress: Float,
    goalImage: Bitmap?,
    isGoalCompleted: Boolean,
    onDepositClicked: () -> Unit,
    onWithdrawClicked: () -> Unit,
    onInfoClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onArchivedClicked: () -> Unit
) {
    val progress by animateFloatAsState(targetValue = goalProgress, label = "goal progress")
    val actionColor = MaterialTheme.colorScheme.primary
    val deleteColor = MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                5.dp
            )
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(CLASSIC_GOAL_IMAGE_ALPHA),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(goalImage ?: R.drawable.default_goal_image)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                            .copy(alpha = CLASSIC_GOAL_IMAGE_OVERLAY_ALPHA)
                    )
            )

            Column {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .padding(start = 14.dp, top = 12.dp, end = 14.dp)
                        .height(4.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(40.dp)),
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 18.dp, end = 24.dp, bottom = 22.dp)
                ) {
                    ClassicGoalText(
                        text = primaryText,
                        highlightColor = actionColor
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    ClassicGoalText(
                        text = secondaryText,
                        highlightColor = actionColor
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isGoalCompleted) {
                        ClassicGoalActionButton(
                            text = stringResource(id = R.string.archive_button).uppercase(),
                            icon = ImageVector.vectorResource(id = R.drawable.ic_compact_goal_archve),
                            contentDescription = stringResource(id = R.string.archive_button),
                            tint = actionColor,
                            onClick = onArchivedClicked
                        )
                    } else {
                        ClassicGoalActionButton(
                            text = stringResource(id = R.string.deposit_button).uppercase(),
                            icon = ImageVector.vectorResource(id = R.drawable.ic_compact_goal_deposit),
                            contentDescription = stringResource(id = R.string.deposit_button),
                            tint = actionColor,
                            onClick = onDepositClicked
                        )
                    }

                    Spacer(modifier = Modifier.size(7.dp))

                    ClassicGoalActionButton(
                        text = stringResource(id = R.string.withdraw_button).uppercase(),
                        icon = ImageVector.vectorResource(id = R.drawable.ic_compact_goal_withdraw),
                        contentDescription = stringResource(id = R.string.withdraw_button),
                        tint = actionColor,
                        onClick = onWithdrawClicked
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    ClassicGoalIconButton(
                        icon = ImageVector.vectorResource(id = R.drawable.ic_goal_info),
                        contentDescription = stringResource(id = R.string.info_button_description),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = onInfoClicked
                    )
                    Spacer(modifier = Modifier.size(9.dp))
                    ClassicGoalIconButton(
                        icon = ImageVector.vectorResource(id = R.drawable.ic_goal_edit),
                        contentDescription = stringResource(id = R.string.edit_button_description),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = onEditClicked
                    )
                    Spacer(modifier = Modifier.size(9.dp))
                    ClassicGoalIconButton(
                        icon = ImageVector.vectorResource(id = R.drawable.ic_goal_delete),
                        contentDescription = stringResource(id = R.string.delete_button_description),
                        tint = deleteColor,
                        onClick = onDeleteClicked
                    )
                }
            }
        }
    }
}

@Composable
private fun ClassicGoalText(
    text: String,
    highlightColor: Color
) {
    Text(
        text = remember(text, highlightColor) {
            text.toClassicGoalAnnotatedString(highlightColor)
        },
        lineHeight = 1.55f.em,
        fontSize = 15.sp,
        fontFamily = piggoFont,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
    )
}

@Composable
private fun ClassicGoalActionButton(
    text: String,
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.32f)),
        modifier = Modifier.height(38.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 7.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = piggoFont,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ClassicGoalIconButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(36.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.48f)
        )
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun String.toClassicGoalAnnotatedString(highlightColor: Color): AnnotatedString {
    val highlightPattern = Regex(
        pattern = """(?:[A-Z]{2,4}|[$₱€£¥])\s?[\d,]+(?:\.\d+)?|(?:\d{1,2}[/-]\d{1,2}[/-]\d{2,4}\s*\(\d+\))"""
    )
    return buildAnnotatedString {
        var currentIndex = 0
        highlightPattern.findAll(this@toClassicGoalAnnotatedString).forEach { match ->
            append(this@toClassicGoalAnnotatedString.substring(currentIndex, match.range.first))
            pushStyle(
                SpanStyle(
                    color = highlightColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
            append(match.value)
            pop()
            currentIndex = match.range.last + 1
        }
        append(this@toClassicGoalAnnotatedString.substring(currentIndex))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalItemCompact(
    title: String,
    savedAmount: String,
    daysLeftText: String,
    goalProgress: Float,
    goalIcon: ImageVector,
    goalImage: Bitmap?,
    isGoalCompleted: Boolean,
    onDepositClicked: () -> Unit,
    onWithdrawClicked: () -> Unit,
    onInfoClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onArchivedClicked: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { direction ->
            when (direction) {
                SwipeToDismissBoxValue.EndToStart -> {
                    coroutineScope.launch {
                        delay(180) // allow the swipe to settle.
                        withContext(Dispatchers.Main) { onEditClicked() }
                    }
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    coroutineScope.launch {
                        delay(180) // allow the swipe to settle.
                        withContext(Dispatchers.Main) { onDeleteClicked() }
                    }
                }

                SwipeToDismissBoxValue.Settled -> {}
            }
            false // Don't allow it to settle on dismissed state.
        }
    )

    val context = LocalContext.current
    val dismissDirection = swipeState.dismissDirection
    val shape = RoundedCornerShape(18.dp)
    val progress by animateFloatAsState(targetValue = goalProgress, label = "progress")

    SwipeToDismissBox(
        state = swipeState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissDirection) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.primary
                    SwipeToDismissBoxValue.StartToEnd -> Color.Red.copy(alpha = 0.5f)
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                }, label = "color"
            )
            val alignment by remember(dismissDirection) {
                derivedStateOf {
                    when (dismissDirection) {
                        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                        SwipeToDismissBoxValue.Settled -> Alignment.Center
                    }
                }
            }
            val icon by remember(dismissDirection) {
                derivedStateOf {
                    when (dismissDirection) {
                        SwipeToDismissBoxValue.EndToStart -> R.drawable.ic_goal_edit
                        SwipeToDismissBoxValue.StartToEnd -> R.drawable.ic_goal_delete
                        // Placeholder icon, not used anywhere.
                        SwipeToDismissBoxValue.Settled -> R.drawable.ic_goal_info
                    }
                }
            }
            val iconDescription by remember(dismissDirection) {
                derivedStateOf {
                    when (dismissDirection) {
                        SwipeToDismissBoxValue.EndToStart -> context.getString(R.string.edit_button_description)
                        SwipeToDismissBoxValue.StartToEnd -> context.getString(R.string.delete_button_description)
                        // Placeholder string, not used anywhere.
                        SwipeToDismissBoxValue.Settled -> context.getString(R.string.info_button_description)
                    }
                }
            }
            val scale by animateFloatAsState(
                if (swipeState.dismissDirection != SwipeToDismissBoxValue.Settled) 1f else 0.75f,
                label = "scale"
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = icon),
                    contentDescription = iconDescription,
                    modifier = Modifier.scale(scale)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(shape = shape),
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        content = {
            Card(
                onClick = onInfoClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = shape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (goalImage != null) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(GOAL_CARD_IMAGE_ALPHA),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(goalImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer.copy(
                                        alpha = GOAL_CARD_IMAGE_OVERLAY_ALPHA
                                    )
                                )
                        )
                    } else {
                        Row {
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = goalIcon,
                                contentDescription = null,
                                modifier = Modifier.size(200.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(start = 10.dp, top = 8.dp, end = 10.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50.dp)),
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 34.dp, end = 12.dp)
                    ) {
                        if (isGoalCompleted) {
                            IconButton(
                                onClick = { onArchivedClicked() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    modifier = Modifier.size(17.dp),
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_compact_goal_archve),
                                    contentDescription = stringResource(id = R.string.archive_button),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { onDepositClicked() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    modifier = Modifier.size(17.dp),
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_compact_goal_deposit),
                                    contentDescription = stringResource(id = R.string.deposit_button),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        IconButton(
                            onClick = { onWithdrawClicked() },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(17.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.ic_compact_goal_withdraw),
                                contentDescription = stringResource(id = R.string.withdraw_button),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth(0.72f)
                            .padding(start = 16.dp, bottom = 18.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(42.dp)
                                .padding(bottom = 6.dp),
                            imageVector = goalIcon,
                            contentDescription = title,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = title,
                            fontWeight = FontWeight.Normal,
                            fontFamily = piggoFont,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Text(
                            text = savedAmount,
                            fontSize = 20.sp,
                            lineHeight = 24.sp,
                            fontFamily = piggoNumberFont,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Text(
                        text = daysLeftText,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 18.dp),
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        fontFamily = piggoFont,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    )
}

private const val CLASSIC_GOAL_IMAGE_ALPHA = 0.18f
private const val CLASSIC_GOAL_IMAGE_OVERLAY_ALPHA = 0.86f
private const val GOAL_CARD_IMAGE_ALPHA = 0.34f
private const val GOAL_CARD_IMAGE_OVERLAY_ALPHA = 0.54f

@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true)
fun GoalItemsPV() {
    Column(modifier = Modifier.padding(10.dp)) {
        GoalItemClassic(
            title = "Home Decorations",
            primaryText = "You're off to a great start!\nCurrently  saved $0.00 out of $1,000.00.",
            secondaryText = "You have until 26/05/2023 (85) days left.\nYou need to save around $58.83/day, $416.67/week, $2,500.00/month.",
            goalProgress = 0.6f,
            goalImage = null,
            isGoalCompleted = false,
            onDepositClicked = { },
            onWithdrawClicked = { },
            onInfoClicked = { },
            onEditClicked = { },
            onDeleteClicked = { },
            onArchivedClicked = { },
        )

        Spacer(modifier = Modifier.height(10.dp))

        GoalItemCompact(
            title = "Home Decorations",
            savedAmount = "$1,000.00",
            daysLeftText = "Goal Achieved!",
            goalProgress = 0.8f,
            goalIcon = ImageVector.vectorResource(id = R.drawable.ic_nav_rating),
            goalImage = null,
            isGoalCompleted = true,
            onDepositClicked = {},
            onWithdrawClicked = {},
            onInfoClicked = {},
            onEditClicked = {},
            onDeleteClicked = {},
            onArchivedClicked = {},
        )
    }
}
