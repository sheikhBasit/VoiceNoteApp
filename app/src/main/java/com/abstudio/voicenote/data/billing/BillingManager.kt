package com.abstudio.voicenote.data.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.abstudio.voicenote.data.api.BillingApi
import com.abstudio.voicenote.data.models.VerifyPurchaseRequest
import com.abstudio.voicenote.data.models.VerifyPurchaseResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val billingApi: BillingApi
) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingManager"
    }

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()

    private val _productDetails = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetails: StateFlow<List<ProductDetails>> = _productDetails.asStateFlow()

    private var pendingVerificationCallback: ((VerifyPurchaseResponse?) -> Unit)? = null

    fun startConnection(onConnected: () -> Unit = {}) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing client connected")
                    onConnected()
                } else {
                    Log.e(TAG, "Billing setup failed: ${result.debugMessage}")
                    _purchaseState.value = PurchaseState.Error("Billing setup failed")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
            }
        })
    }

    fun queryProducts(productIds: List<String>) {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map { productId ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                }
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { result, details ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                _productDetails.value = details
            } else {
                Log.e(TAG, "Failed to query products: ${result.debugMessage}")
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        _purchaseState.value = PurchaseState.Loading
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                _purchaseState.value = PurchaseState.Idle
            }
            else -> {
                _purchaseState.value = PurchaseState.Error("Purchase failed: ${result.debugMessage}")
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            _purchaseState.value = PurchaseState.Verifying

            // Acknowledge the purchase
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { ackResult ->
                    if (ackResult.responseCode != BillingClient.BillingResponseCode.OK) {
                        Log.e(TAG, "Failed to acknowledge purchase: ${ackResult.debugMessage}")
                    }
                }
            }

            // The ViewModel will call verifyWithBackend separately
            _purchaseState.value = PurchaseState.PendingVerification(
                purchaseToken = purchase.purchaseToken,
                productId = purchase.products.firstOrNull() ?: ""
            )
        }
    }

    suspend fun verifyWithBackend(purchaseToken: String, productId: String): VerifyPurchaseResponse? {
        return try {
            val response = billingApi.verifyPurchase(
                VerifyPurchaseRequest(purchaseToken, productId)
            )
            if (response.isSuccessful) {
                _purchaseState.value = PurchaseState.Success(response.body()?.message ?: "Upgraded!")
                response.body()
            } else {
                _purchaseState.value = PurchaseState.Error("Verification failed")
                null
            }
        } catch (e: Exception) {
            _purchaseState.value = PurchaseState.Error(e.message ?: "Verification error")
            null
        }
    }

    fun endConnection() {
        billingClient.endConnection()
    }
}

sealed class PurchaseState {
    object Idle : PurchaseState()
    object Loading : PurchaseState()
    object Verifying : PurchaseState()
    data class PendingVerification(val purchaseToken: String, val productId: String) : PurchaseState()
    data class Success(val message: String) : PurchaseState()
    data class Error(val message: String) : PurchaseState()
}
