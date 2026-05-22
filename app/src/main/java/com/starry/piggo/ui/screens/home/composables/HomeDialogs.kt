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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.starry.piggo.R
import com.starry.piggo.ui.theme.piggoFont


@Composable
fun HomeDialogs(
    openDeleteDialog: MutableState<Boolean>,
    openArchiveDialog: MutableState<Boolean>,
    onDeleteConfirmed: () -> Unit,
    onArchiveConfirmed: () -> Unit
) {
    if (openDeleteDialog.value) {
        Dialog(
            onDismissRequest = {
                openDeleteDialog.value = false
            }
        ) {
            Surface(
                modifier = Modifier.width(276.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.16f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_goal_delete),
                            contentDescription = null,
                            modifier = Modifier.size(17.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = stringResource(id = R.string.goal_delete_confirmation),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = piggoFont,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "This goal will be permanently deleted.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = piggoFont,
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                openDeleteDialog.value = false
                            },
                            modifier = Modifier.size(width = 92.dp, height = 38.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.cancel),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                fontFamily = piggoFont
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        FilledTonalButton(
                            onClick = {
                                openDeleteDialog.value = false
                                onDeleteConfirmed()
                            },
                            modifier = Modifier.size(width = 96.dp, height = 38.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text(
                                stringResource(id = R.string.confirm),
                                fontSize = 13.sp,
                                fontFamily = piggoFont
                            )
                        }
                    }
                }
            }
        }
    }

    if (openArchiveDialog.value) {

        AlertDialog(
            onDismissRequest = {
                openArchiveDialog.value = false
            }, title = {
                Text(
                    text = stringResource(id = R.string.goal_archive_confirmation),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = piggoFont,
                    fontSize = 18.sp
                )
            }, confirmButton = {
                FilledTonalButton(
                    onClick = {
                        openArchiveDialog.value = false
                        onArchiveConfirmed()
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text(stringResource(id = R.string.confirm), fontFamily = piggoFont)
                }
            }, dismissButton = {
                TextButton(onClick = {
                    openArchiveDialog.value = false
                }) {
                    Text(stringResource(id = R.string.cancel), fontFamily = piggoFont)
                }
            },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_compact_goal_archve),
                    contentDescription = null
                )
            }
        )
    }

}
