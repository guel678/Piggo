package com.starry.piggo.ui.screens.premium.composables

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.starry.piggo.R
import com.starry.piggo.ui.screens.premium.PremiumViewModel
import com.starry.piggo.ui.theme.piggoFont
import com.starry.piggo.utils.getActivity
import com.starry.piggo.utils.toToast
import com.starry.piggo.utils.weakHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current
    val viewModel: PremiumViewModel = hiltViewModel()
    val state by viewModel.premiumState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    LaunchedEffect(state.message) {
        state.message?.let { message ->
            message.toToast(context)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.premium_screen_header),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = piggoFont
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        view.weakHapticFeedback()
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.WorkspacePremium,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(id = R.string.premium_title),
                fontFamily = piggoFont,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.premium_subtitle),
                modifier = Modifier.padding(top = 8.dp),
                fontFamily = piggoFont,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    PremiumFeature(text = stringResource(id = R.string.premium_feature_unlimited_goals))
                    PremiumFeature(text = stringResource(id = R.string.premium_feature_transfers))
                    PremiumFeature(text = stringResource(id = R.string.premium_feature_history))
                    PremiumFeature(text = stringResource(id = R.string.premium_feature_backup))
                    PremiumFeature(text = stringResource(id = R.string.premium_feature_reminders))
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            if (state.isPremium) {
                Text(
                    text = stringResource(id = R.string.premium_unlocked),
                    fontFamily = piggoFont,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.isBillingReady && state.isProductAvailable,
                    shape = RoundedCornerShape(14.dp),
                    onClick = {
                        view.weakHapticFeedback()
                        context.getActivity()?.let { activity ->
                            viewModel.buyPremium(activity)
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Filled.Diamond, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(id = R.string.premium_buy_button).format(state.priceText),
                        fontFamily = piggoFont
                    )
                }

                if (!state.isProductAvailable) {
                    Text(
                        text = stringResource(id = R.string.premium_product_unavailable),
                        modifier = Modifier.padding(top = 10.dp),
                        fontFamily = piggoFont,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.68f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(14.dp),
                onClick = {
                    view.weakHapticFeedback()
                    viewModel.refresh()
                }
            ) {
                Text(text = stringResource(id = R.string.premium_restore_button), fontFamily = piggoFont)
            }
        }
    }
}

@Composable
private fun PremiumFeature(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            fontFamily = piggoFont,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 19.sp
        )
    }
}
