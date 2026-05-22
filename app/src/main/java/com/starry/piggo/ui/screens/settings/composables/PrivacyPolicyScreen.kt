package com.starry.piggo.ui.screens.settings.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.starry.piggo.ui.theme.piggoFont
import com.starry.piggo.utils.weakHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    val view = LocalView.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Privacy Policy",
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
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text(
                    text = "Last updated: May 22, 2026",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                    fontFamily = piggoFont,
                    fontSize = 13.sp
                )
            }
            item {
                PrivacySection(
                    title = "Overview",
                    body = "Piggo is a savings and stash tracking app. It is designed to keep your savings goals, stash balances, transactions, reminders, settings, widgets, and backups on your device unless you choose to export or share that data."
                )
            }
            item {
                PrivacySection(
                    title = "Information Stored By The App",
                    body = "Piggo stores the savings goals or stashes you create, target amounts, deadlines, notes, selected icons, optional goal images, deposits, withdrawals, transfers, reminder settings, widget selections, app preferences, selected currency, display settings, and app lock settings."
                )
            }
            item {
                PrivacySection(
                    title = "Where Data Is Stored",
                    body = "Your app data is stored locally in the app database and local preferences on your device. Piggo does not include advertising, analytics tracking, or remote crash reporting code in this app."
                )
            }
            item {
                PrivacySection(
                    title = "Backups And Exports",
                    body = "Piggo can create backup files that contain your savings goals and transaction data. JSON backups may include goal images. CSV backups do not include goal images. Backup files are created only when you use backup features or enable automatic backups. Anyone with access to a backup file may be able to read the data inside it."
                )
            }
            item {
                PrivacySection(
                    title = "Android System Backup",
                    body = "The app allows Android system backup. Depending on your device and Google account settings, Android may back up app data through the operating system. This is controlled by Android and your device settings, not by Piggo servers."
                )
            }
            item {
                PrivacySection(
                    title = "Images And Files",
                    body = "If you add a goal image, Piggo reads the image you selected and stores a compressed copy in the local app database. If you choose backup or restore files, Piggo reads or writes only the files you select or the backup folder you authorize."
                )
            }
            item {
                PrivacySection(
                    title = "Notifications And Reminders",
                    body = "If you enable saving reminders, Piggo uses local notifications and alarms to remind you about goals. The notification permission is requested only when needed. The boot permission is used to restore scheduled reminders after your device restarts."
                )
            }
            item {
                PrivacySection(
                    title = "App Lock And Biometrics",
                    body = "If you enable app lock, Piggo uses Android's biometric or device credential prompt. Piggo does not receive or store your fingerprint, face data, PIN, password, or biometric template."
                )
            }
            item {
                PrivacySection(
                    title = "Sharing",
                    body = "Piggo may open Android share sheets for features such as sharing app links or backup files. Data is shared only with the app or destination you choose."
                )
            }
            item {
                PrivacySection(
                    title = "Deleting Data",
                    body = "You can delete goals, transactions, backups, and app data from inside the app where features are available. You can also clear Piggo's app data or uninstall the app from Android settings. Backup files saved outside the app must be deleted from their storage location separately."
                )
            }
        }
    }
}

@Composable
private fun PrivacySection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            fontFamily = piggoFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Text(
            text = body,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
            fontFamily = piggoFont,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )
    }
}

@Preview
@Composable
private fun PrivacyPolicyScreenPreview() {
    PrivacyPolicyScreen(navController = rememberNavController())
}
