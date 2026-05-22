package com.starry.piggo.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.starry.piggo.utils.PreferenceUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class PremiumState(
    val isPremium: Boolean = false,
    val priceText: String = "PHP 60",
    val isBillingReady: Boolean = false,
    val isProductAvailable: Boolean = false,
    val message: String? = null
)

@Singleton
class PremiumRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val preferenceUtil: PreferenceUtil
) : PurchasesUpdatedListener {

    companion object {
        const val PREMIUM_PRODUCT_ID = "piggo_premium"
        const val FREE_GOAL_LIMIT = 3
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    private val _state = MutableStateFlow(
        PremiumState(isPremium = preferenceUtil.getBoolean(PreferenceUtil.PREMIUM_UNLOCKED_BOOL, false))
    )
    val state: StateFlow<PremiumState> = _state

    private var premiumProductDetails: ProductDetails? = null

    init {
        connect()
    }

    fun refresh() {
        if (billingClient.isReady) {
            queryPurchases()
            queryPremiumProduct()
        } else {
            connect()
        }
    }

    fun launchPurchase(activity: Activity) {
        val productDetails = premiumProductDetails
        if (!billingClient.isReady || productDetails == null) {
            _state.value = _state.value.copy(
                message = "Premium is not ready yet. Please try again in a moment."
            )
            refresh()
            return
        }

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            _state.value = _state.value.copy(message = result.debugMessage.ifBlank {
                "Unable to start purchase."
            })
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> purchases?.forEach { purchase ->
                handlePurchase(purchase, notifyUser = true)
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> Unit
            else -> {
                _state.value = _state.value.copy(message = billingResult.debugMessage.ifBlank {
                    "Purchase was not completed."
                })
            }
        }
    }

    private fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                _state.value = _state.value.copy(isBillingReady = false)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                val ready = billingResult.responseCode == BillingClient.BillingResponseCode.OK
                _state.value = _state.value.copy(isBillingReady = ready)
                if (ready) {
                    queryPurchases()
                    queryPremiumProduct()
                }
            }
        })
    }

    private fun queryPremiumProduct() {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(PREMIUM_PRODUCT_ID)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsResult ->
            val productDetails = if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsResult.productDetailsList.firstOrNull()
            } else {
                null
            }

            premiumProductDetails = productDetails
            _state.value = _state.value.copy(
                isProductAvailable = productDetails != null,
                priceText = productDetails?.oneTimePurchaseOfferDetails?.formattedPrice
                    ?: _state.value.priceText
            )
        }
    }

    private fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { purchase ->
                    handlePurchase(purchase, notifyUser = false)
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase, notifyUser: Boolean) {
        if (!purchase.products.contains(PREMIUM_PRODUCT_ID)) return
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return

        unlockPremium(notifyUser)
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    unlockPremium(notifyUser)
                }
            }
        }
    }

    private fun unlockPremium(notifyUser: Boolean) {
        preferenceUtil.putBoolean(PreferenceUtil.PREMIUM_UNLOCKED_BOOL, true)
        _state.value = _state.value.copy(
            isPremium = true,
            message = if (notifyUser) "Piggo Premium unlocked!" else _state.value.message
        )
    }
}
