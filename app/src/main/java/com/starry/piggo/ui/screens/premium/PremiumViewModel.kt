package com.starry.piggo.ui.screens.premium

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.starry.piggo.billing.PremiumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val premiumRepository: PremiumRepository
) : ViewModel() {

    val premiumState = premiumRepository.state

    fun refresh() = premiumRepository.refresh()

    fun buyPremium(activity: Activity) = premiumRepository.launchPurchase(activity)

    fun clearMessage() = premiumRepository.clearMessage()
}
