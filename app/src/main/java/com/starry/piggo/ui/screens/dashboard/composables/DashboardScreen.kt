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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.starry.piggo.MainActivity
import com.starry.piggo.R
import com.starry.piggo.database.goal.DashboardGoalSummary
import com.starry.piggo.database.transaction.DashboardDepositSummary
import com.starry.piggo.database.transaction.TransactionType
import com.starry.piggo.ui.navigation.DrawerScreens
import com.starry.piggo.ui.navigation.OtherScreens
import com.starry.piggo.ui.screens.dashboard.DashboardViewModel
import com.starry.piggo.ui.screens.home.composables.HomeDrawer
import com.starry.piggo.ui.screens.premium.PremiumViewModel
import com.starry.piggo.ui.theme.piggoFont
import com.starry.piggo.utils.Constants
import com.starry.piggo.utils.ImageUtils
import com.starry.piggo.utils.NumberUtils
import com.starry.piggo.utils.Utils
import com.starry.piggo.utils.getActivity
import com.starry.piggo.utils.toToast
import com.starry.piggo.utils.weakHapticFeedback
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date

private enum class DashboardSheetMode {
    Deposit,
    Transfer,
    History
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: DashboardViewModel = hiltViewModel()
    val premiumViewModel: PremiumViewModel = hiltViewModel()
    val settingsVM = (context.getActivity() as MainActivity).settingsViewModel
    val goals by viewModel.goalsList.observeAsState(emptyList())
    val deposits by viewModel.depositsList.observeAsState(emptyList())
    val premiumState by premiumViewModel.premiumState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    val currencyCode = remember { viewModel.getDefaultCurrency().ifBlank { "USD" } }
    var sheetMode by remember { mutableStateOf<DashboardSheetMode?>(null) }

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
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    BalanceCard(
                        goals = goals,
                        currencyCode = currencyCode,
                        onDepositClicked = {
                            view.weakHapticFeedback()
                            sheetMode = DashboardSheetMode.Deposit
                        },
                        onTransferClicked = {
                            view.weakHapticFeedback()
                            if (premiumState.isPremium) {
                                sheetMode = DashboardSheetMode.Transfer
                            } else {
                                context.getString(R.string.premium_feature_locked_message).toToast(context)
                                navController.navigate(OtherScreens.PremiumScreen)
                            }
                        },
                        onHistoryClicked = {
                            view.weakHapticFeedback()
                            if (premiumState.isPremium) {
                                sheetMode = DashboardSheetMode.History
                            } else {
                                context.getString(R.string.premium_feature_locked_message).toToast(context)
                                navController.navigate(OtherScreens.PremiumScreen)
                            }
                        }
                    )
                }

                item {
                    SectionTitle(text = stringResource(id = R.string.dashboard_active_goals))
                }

                if (goals.isEmpty()) {
                    item { EmptyCard(text = stringResource(id = R.string.dashboard_no_goals)) }
                } else {
                    items(
                        items = goals,
                        key = { "goal-${it.goalId}" }
                    ) { goalItem ->
                        DashboardGoalRow(
                            goalItem = goalItem,
                            currencyCode = currencyCode,
                            dateText = goalItem.remainingDaysText(),
                            onClick = {
                                view.weakHapticFeedback()
                                navController.navigate(
                                    OtherScreens.GoalInfoScreen(goalId = goalItem.goalId.toString())
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    sheetMode?.let { mode ->
        ModalBottomSheet(
            onDismissRequest = { sheetMode = null },
            sheetState = sheetState
        ) {
            when (mode) {
                DashboardSheetMode.Deposit -> DepositGoalPicker(
                    goals = goals,
                    currencyCode = currencyCode,
                    onGoalSelected = { goal ->
                        sheetMode = null
                        navController.navigate(
                            OtherScreens.DWScreen(
                                goalId = goal.goalId.toString(),
                                transactionType = TransactionType.Deposit.name
                            )
                        )
                    }
                )

                DashboardSheetMode.Transfer -> TransferSheet(
                    goals = goals,
                    currencyCode = currencyCode,
                    onTransfer = { fromGoal, toGoal, amountText ->
                        viewModel.transfer(
                            fromGoal = fromGoal,
                            toGoal = toGoal,
                            amountText = amountText,
                            onComplete = { success ->
                                if (success) {
                                    sheetMode = null
                                    context.getString(R.string.dashboard_transfer_successful).toToast(context)
                                } else {
                                    context.getString(R.string.dashboard_transfer_error).toToast(context)
                                }
                            }
                        )
                    }
                )

                DashboardSheetMode.History -> HistorySheet(
                    deposits = deposits,
                    currencyCode = currencyCode,
                    onDepositSelected = { deposit ->
                        sheetMode = null
                        navController.navigate(
                            OtherScreens.GoalInfoScreen(goalId = deposit.ownerGoalId.toString())
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun BalanceCard(
    goals: List<DashboardGoalSummary>,
    currencyCode: String,
    onDepositClicked: () -> Unit,
    onTransferClicked: () -> Unit,
    onHistoryClicked: () -> Unit
) {
    val totalSaved = goals.sumOf { it.savedAmount }
    val totalTarget = goals.sumOf { it.targetAmount }
    val progress = if (totalTarget > 0) (totalSaved / totalTarget).toFloat().coerceIn(0f, 1f) else 0f
    val progressPercent = (progress * 100).toInt()

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.86f)
                    )
                )
            )
            .padding(18.dp)
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.dashboard_total_saved),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                fontFamily = piggoFont,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = NumberUtils.formatCurrency(totalSaved, currencyCode),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = piggoFont,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
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
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.18f)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DashboardAction(
                    icon = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.dashboard_deposit),
                    onClick = onDepositClicked
                )
                DashboardAction(
                    icon = Icons.Filled.SwapHoriz,
                    contentDescription = stringResource(id = R.string.dashboard_transfer),
                    onClick = onTransferClicked
                )
                DashboardAction(
                    icon = Icons.Filled.History,
                    contentDescription = stringResource(id = R.string.dashboard_history),
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
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontFamily = piggoFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DashboardAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(15.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.14f),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 2.dp),
        fontFamily = piggoFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    )
}

@Composable
private fun DashboardGoalRow(
    goalItem: DashboardGoalSummary,
    currencyCode: String,
    dateText: String,
    onClick: () -> Unit
) {
    val progress = if (goalItem.targetAmount > 0) {
        (goalItem.savedAmount / goalItem.targetAmount).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GoalIcon(goalIconId = goalItem.goalIconId)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goalItem.title,
                        fontFamily = piggoFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dateText,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f),
                        fontFamily = piggoFont,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = NumberUtils.formatCurrency(goalItem.savedAmount, currencyCode),
                    fontFamily = piggoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            )
        }
    }
}

@Composable
private fun DepositHistoryRow(
    deposit: DashboardDepositSummary,
    currencyCode: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoalIcon(goalIconId = deposit.goalIconId, size = 38)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deposit.goalTitle,
                    fontFamily = piggoFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = deposit.formattedDate(),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                    fontFamily = piggoFont,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = NumberUtils.formatCurrency(deposit.amount, currencyCode),
                color = MaterialTheme.colorScheme.primary,
                fontFamily = piggoFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HistorySheet(
    deposits: List<DashboardDepositSummary>,
    currencyCode: String,
    onDepositSelected: (DashboardDepositSummary) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        SheetTitle(text = stringResource(id = R.string.dashboard_transaction_history))
        if (deposits.isEmpty()) {
            EmptyCard(text = stringResource(id = R.string.dashboard_no_deposits))
        } else {
            deposits.forEach { deposit ->
                DepositHistoryRow(
                    deposit = deposit,
                    currencyCode = currencyCode,
                    onClick = { onDepositSelected(deposit) }
                )
            }
        }
    }
}

@Composable
private fun GoalIcon(goalIconId: String?, size: Int = 42) {
    val icon = remember(goalIconId) {
        ImageUtils.createIconVector(goalIconId ?: Constants.DEFAULT_GOAL_ICON_ID)
            ?: Icons.Filled.Savings
    }

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size((size * 0.52f).dp)
        )
    }
}

@Composable
private fun EmptyCard(text: String) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(18.dp),
            fontFamily = piggoFont,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
        )
    }
}

@Composable
private fun DepositGoalPicker(
    goals: List<DashboardGoalSummary>,
    currencyCode: String,
    onGoalSelected: (DashboardGoalSummary) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        SheetTitle(text = stringResource(id = R.string.dashboard_choose_stash))
        if (goals.isEmpty()) {
            EmptyCard(text = stringResource(id = R.string.dashboard_no_goals))
        } else {
            goals.forEach { goal ->
                GoalPickerRow(
                    goal = goal,
                    currencyCode = currencyCode,
                    selected = false,
                    onClick = { onGoalSelected(goal) }
                )
            }
        }
    }
}

@Composable
private fun TransferSheet(
    goals: List<DashboardGoalSummary>,
    currencyCode: String,
    onTransfer: (DashboardGoalSummary, DashboardGoalSummary, String) -> Unit
) {
    var fromGoalId by remember(goals) {
        mutableStateOf(goals.firstOrNull { it.savedAmount > 0 }?.goalId ?: goals.firstOrNull()?.goalId)
    }
    var toGoalId by remember(goals, fromGoalId) {
        mutableStateOf(goals.firstOrNull { it.goalId != fromGoalId }?.goalId)
    }
    var amountText by remember { mutableStateOf("") }
    val fromGoal = goals.firstOrNull { it.goalId == fromGoalId }
    val toGoal = goals.firstOrNull { it.goalId == toGoalId }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(bottom = 24.dp)
    ) {
        SheetTitle(text = stringResource(id = R.string.dashboard_transfer))
        if (goals.size < 2) {
            EmptyCard(text = stringResource(id = R.string.dashboard_transfer_needs_goals))
            return@Column
        }

        SheetSubTitle(text = stringResource(id = R.string.dashboard_transfer_from))
        goals.filter { it.savedAmount > 0 }.forEach { goal ->
            GoalPickerRow(
                goal = goal,
                currencyCode = currencyCode,
                selected = goal.goalId == fromGoalId,
                onClick = {
                    fromGoalId = goal.goalId
                    if (toGoalId == goal.goalId) {
                        toGoalId = goals.firstOrNull { it.goalId != goal.goalId }?.goalId
                    }
                }
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SheetSubTitle(text = stringResource(id = R.string.dashboard_transfer_to))
        goals.filter { it.goalId != fromGoalId }.forEach { goal ->
            GoalPickerRow(
                goal = goal,
                currencyCode = currencyCode,
                selected = goal.goalId == toGoalId,
                onClick = { toGoalId = goal.goalId }
            )
        }
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = NumberUtils.getValidatedNumber(it) },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.dashboard_transfer_amount)) },
            singleLine = true,
            prefix = { Text(text = NumberUtils.getCurrencySymbol(currencyCode)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            enabled = fromGoal != null && toGoal != null,
            onClick = {
                if (fromGoal != null && toGoal != null) {
                    onTransfer(fromGoal, toGoal, amountText)
                }
            }
        ) {
            Text(text = stringResource(id = R.string.confirm), fontFamily = piggoFont)
        }
    }
}

@Composable
private fun SheetTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        fontFamily = piggoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
}

@Composable
private fun SheetSubTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
        fontFamily = piggoFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
}

@Composable
private fun GoalPickerRow(
    goal: DashboardGoalSummary,
    currencyCode: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoalIcon(goalIconId = goal.goalIconId, size = 36)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.title,
                    fontFamily = piggoFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = NumberUtils.formatCurrency(goal.savedAmount, currencyCode),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                    fontFamily = piggoFont,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

private fun DashboardGoalSummary.remainingDaysText(): String {
    if (savedAmount >= targetAmount) {
        return "Goal achieved!"
    }
    if (deadline == 0L) {
        return "No deadline set"
    }
    val endDate = Utils.convertEpochToLocalDate(deadline)
    val days = ChronoUnit.DAYS.between(LocalDate.now(), endDate).coerceAtLeast(0)
    return "$days days left"
}

private fun DashboardDepositSummary.formattedDate(): String {
    return DateFormat.getDateInstance().format(Date(timeStamp))
}

@Composable
@Preview
private fun DashboardScreenPreview() {
    DashboardScreen(navController = rememberNavController())
}
