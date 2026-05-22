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


package com.starry.piggo.ui.screens.settings.composables

import android.content.ClipData
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.starry.piggo.BuildConfig
import com.starry.piggo.R
import com.starry.piggo.ui.theme.piggoFont
import com.starry.piggo.utils.Utils
import com.starry.piggo.utils.weakHapticFeedback
import kotlinx.coroutines.launch

sealed class AboutLinks(val url: String) {
    data object PrivacyPolicy :
        AboutLinks("https://github.com/Pool-Of-Tears/Piggo/blob/main/legal/PRIVACY-POLICY.md")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.about_screen_header),
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
                scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }) {
        LazyColumn(modifier = Modifier.padding(it)) {
            item {
                SettingsItem(
                    title = stringResource(id = R.string.about_privacy_title),
                    description = stringResource(id = R.string.about_privacy_desc),
                    icon = Icons.Filled.PrivacyTip,
                    onClick = { Utils.openWebLink(context, AboutLinks.PrivacyPolicy.url) }
                )
            }
            item {
                SettingsItem(
                    title = stringResource(id = R.string.about_version_title),
                    description = stringResource(id = R.string.about_version_desc).format(
                        BuildConfig.VERSION_NAME
                    ),
                    icon = Icons.Filled.Info,
                    onClick = {
                        coroutineScope.launch {
                            val clipData = ClipData.newPlainText("", getVersionReport())
                            clipboard.setClipEntry(ClipEntry(clipData))

                        }
                    }
                )
            }
        }
    }
}


fun getVersionReport(): String {
    val versionName = BuildConfig.VERSION_NAME
    val versionCode = BuildConfig.VERSION_CODE
    val release = if (Build.VERSION.SDK_INT >= 30) {
        Build.VERSION.RELEASE_OR_CODENAME
    } else {
        Build.VERSION.RELEASE
    }
    return StringBuilder().append("App version: $versionName ($versionCode)\n")
        .append("Android Version: Android $release (API ${Build.VERSION.SDK_INT})\n")
        .append("Device information: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})\n")
        .append("Supported ABIs: ${Build.SUPPORTED_ABIS.contentToString()}\n")
        .toString()
}

@Preview
@Composable
fun AboutScreenPV() {
    AboutScreen(navController = rememberNavController())
}
