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


package com.starry.piggo.ui.screens.dashboard.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.starry.piggo.MainActivity
import com.starry.piggo.R
import com.starry.piggo.database.core.GoalWithTransactions
import com.starry.piggo.database.transaction.TransactionType
import com.starry.piggo.ui.navigation.DrawerScreens
import com.starry.piggo.ui.navigation.OtherScreens
import com.starry.piggo.ui.screens.home.HomeViewModel
import com.starry.piggo.ui.screens.home.composables.HomeDrawer
import com.starry.piggo.ui.theme.piggoFont
import com.starry.piggo.utils.GoalTextUtils
import com.starry.piggo.utils.NumberUtils
import com.starry.piggo.utils.getActivity
import com.starry.piggo.utils.weakHapticFeedback
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = hiltViewModel()
    val settingsVM = (context.getActivity() as MainActivity).settingsViewModel
    val goals by viewModel.goalsList.observeAsState(emptyList())
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    val currencyCode = remember { viewModel.getDefaultCurrency().ifBlank { "USD" } }
    val firstOpenGoal = goals.firstOrNull { it.getCurrentlySavedAmount() < it.goal.targetAmount }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            HomeDrawer(
                drawerState = drawerState,
                navController = navController,
                themeMode = settingsVM.getCurrentTheme()
            )
        },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.dashboard_screen_header),
                            fontFamily = piggoFont,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            view.weakHapticFeedback()
                            coroutineScope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(id = R.string.menu_button_desc)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            view.weakHapticFeedback()
                            navController.navigate(DrawerScreens.Home)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = stringResource(id = R.string.search_button_desc)
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(end = 10.dp, bottom = 12.dp),
                    onClick = {
                        view.weakHapticFeedback()
                        navController.navigate(OtherScreens.InputScreen())
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.new_goal_fab),
                        fontFamily = piggoFont
                    )
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    BalanceCard(
                        goals = goals,
                        currencyCode = currencyCode,
                        onDepositClicked = {
                            view.weakHapticFeedback()
                            firstOpenGoal?.let {
                                navController.navigate(
                                    OtherScreens.DWScreen(
                                        goalId = it.goal.goalId.toString(),
                                        transactionType = TransactionType.Deposit.name
                                    )
                                )
                            } ?: navController.navigate(OtherScreens.InputScreen())
                        },
                        onGoalsClicked = {
                            view.weakHapticFeedback()
                            navController.navigate(DrawerScreens.Home)
                        },
                        onHistoryClicked = {
                            view.weakHapticFeedback()
                            goals.firstOrNull()?.let {
                                navController.navigate(
                                    OtherScreens.GoalInfoScreen(
                                        goalId = it.goal.goalId.toString()
                                    )
                                )
                            } ?: navController.navigate(OtherScreens.InputScreen())
                        }
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.dashboard_active_goals),
                        modifier = Modifier.padding(horizontal = 24.dp),
                        fontFamily = piggoFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                }

                if (goals.isEmpty()) {
                    item {
                        EmptyGoalCard()
                    }
                } else {
                    items(
                        items = goals.take(4),
                        key = { it.goal.goalId }
                    ) { goalItem ->
                        DashboardGoalCard(
                            goalItem = goalItem,
                            currencyCode = currencyCode,
                            dateText = GoalTextUtils.getRemainingDaysText(
                                context = context,
                                goalItem = goalItem,
                                dateStyle = viewModel.getDateStyle()
                            ),
                            onClick = {
                                view.weakHapticFeedback()
                                navController.navigate(
                                    OtherScreens.GoalInfoScreen(
                                        goalId = goalItem.goal.goalId.toString()
                                    )
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(96.dp))
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(
    goals: List<GoalWithTransactions>,
    currencyCode: String,
    onDepositClicked: () -> Unit,
    onGoalsClicked: () -> Unit,
    onHistoryClicked: () -> Unit
) {
    val totalSaved = goals.sumOf { it.getCurrentlySavedAmount() }
    val totalTarget = goals.sumOf { it.goal.targetAmount }
    val progress = if (totalTarget > 0) (totalSaved / totalTarget).toFloat().coerceIn(0f, 1f) else 0f
    val progressPercent = (progress * 100).toInt()

    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.88f)
                    )
                )
            )
            .padding(22.dp)
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.dashboard_total_saved),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                fontFamily = piggoFont,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = NumberUtils.formatCurrency(totalSaved, currencyCode),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = piggoFont,
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceMetric(
                    label = stringResource(id = R.string.dashboard_target_balance),
                    value = NumberUtils.formatCurrency(totalTarget, currencyCode)
                )
                BalanceMetric(
                    label = stringResource(id = R.string.dashboard_progress),
                    value = "$progressPercent%"
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.18f)
            )
            Spacer(modifier = Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashboardAction(
                    modifier = Modifier.weight(1f),
                    label = stringResource(id = R.string.dashboard_deposit),
                    icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                    onClick = onDepositClicked
                )
                DashboardAction(
                    modifier = Modifier.weight(1f),
                    label = stringResource(id = R.string.dashboard_goals),
                    icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = null) },
                    onClick = onGoalsClicked
                )
                DashboardAction(
                    modifier = Modifier.weight(1f),
                    label = stringResource(id = R.string.dashboard_history),
                    icon = { Icon(imageVector = Icons.Filled.History, contentDescription = null) },
                    onClick = onHistoryClicked
                )
            }
        }
    }
}

@Composable
private fun BalanceMetric(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.66f),
            fontFamily = piggoFont,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontFamily = piggoFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DashboardAction(
    modifier: Modifier,
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontFamily = piggoFont,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DashboardGoalCard(
    goalItem: GoalWithTransactions,
    currencyCode: String,
    dateText: String,
    onClick: () -> Unit
) {
    val savedAmount = goalItem.getCurrentlySavedAmount()
    val progress = if (goalItem.goal.targetAmount > 0) {
        (savedAmount / goalItem.goal.targetAmount).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = goalItem.goal.title.firstOrNull()?.uppercase() ?: "?",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontFamily = piggoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goalItem.goal.title,
                        fontFamily = piggoFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dateText,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                        fontFamily = piggoFont,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = NumberUtils.formatCurrency(savedAmount, currencyCode),
                    fontFamily = piggoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(7.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
private fun EmptyGoalCard() {
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.dashboard_no_goals),
                fontFamily = piggoFont,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
            )
        }
    }
}

@Composable
@Preview
private fun DashboardScreenPreview() {
    DashboardScreen(navController = rememberNavController())
}
