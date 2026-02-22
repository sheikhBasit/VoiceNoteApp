package com.abstudio.voicenote.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abstudio.voicenote.data.api.AuthApi
import com.abstudio.voicenote.data.api.BillingApi
import com.abstudio.voicenote.data.billing.BillingManager
import com.abstudio.voicenote.data.billing.PurchaseState
import com.abstudio.voicenote.data.models.PlanResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WalletBillingUiState(
    val isLoading: Boolean = false,
    val walletBalance: Int = 0,
    val currentPlanName: String? = null,
    val userTier: String? = null,
    val plans: List<PlanResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class WalletBillingViewModel @Inject constructor(
    private val billingApi: BillingApi,
    private val authApi: AuthApi,
    private val billingManager: BillingManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletBillingUiState())
    val uiState: StateFlow<WalletBillingUiState> = _uiState.asStateFlow()

    init {
        observePurchaseState()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Fetch user profile for wallet balance
            try {
                val meResponse = authApi.getMe()
                if (meResponse.isSuccessful) {
                    val user = meResponse.body()
                    _uiState.update {
                        it.copy(
                            userTier = user?.primaryRole,
                            currentPlanName = user?.primaryRole // Will be enhanced when plan info is in user response
                        )
                    }
                }
            } catch (_: Exception) {}

            // Fetch available plans
            try {
                val plansResponse = billingApi.getPlans()
                if (plansResponse.isSuccessful) {
                    _uiState.update { it.copy(plans = plansResponse.body() ?: emptyList()) }
                }
            } catch (_: Exception) {}

            _uiState.update { it.copy(isLoading = false) }

            // Initialize billing client and query products
            val productIds = _uiState.value.plans.mapNotNull { it.googlePlayProductId }
            if (productIds.isNotEmpty()) {
                billingManager.startConnection {
                    billingManager.queryProducts(productIds)
                }
            }
        }
    }

    fun initiatePurchase(plan: PlanResponse) {
        val productId = plan.googlePlayProductId ?: return
        val productDetails = billingManager.productDetails.value.find {
            it.productId == productId
        }
        if (productDetails != null) {
            // Activity must be provided by the composable - for now store pending plan
            _uiState.update { it.copy(error = "Launch purchase from Activity context") }
        } else {
            _uiState.update { it.copy(error = "Product not available in Google Play") }
        }
    }

    private fun observePurchaseState() {
        viewModelScope.launch {
            billingManager.purchaseState.collect { state ->
                when (state) {
                    is PurchaseState.PendingVerification -> {
                        // Verify with backend
                        val result = billingManager.verifyWithBackend(
                            state.purchaseToken,
                            state.productId
                        )
                        if (result?.success == true) {
                            _uiState.update { it.copy(userTier = result.newTier) }
                            loadData() // Refresh all data
                        }
                    }
                    is PurchaseState.Error -> {
                        _uiState.update { it.copy(error = state.message) }
                    }
                    is PurchaseState.Success -> {
                        _uiState.update { it.copy(error = null) }
                    }
                    else -> {}
                }
            }
        }
    }
}
