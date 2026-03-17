/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.iap

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.iap.Iap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IapUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
    val availableSkus: List<String> =
        listOf("SKU-sub-1", "sku-consumable-1", "sku-dur-1", "sku-dur-2", "sku1", "sku2"),
    val selectedSkus: List<String> = emptyList(),
)

class IapViewModel : ViewModel() {

  private val iap = Iap()

  private val _uiState = MutableStateFlow(IapUiState())
  val uiState: StateFlow<IapUiState> = _uiState

  fun enableSku(sku: String) {
    _uiState.update { it.copy(selectedSkus = it.selectedSkus + sku) }
  }

  fun disableSku(sku: String) {
    _uiState.update { it.copy(selectedSkus = it.selectedSkus.filter { s -> s != sku }) }
  }

  fun getProducts() {
    val skus = _uiState.value.selectedSkus
    if (skus.isEmpty()) {
      return
    }

    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val pagedResults = iap.getProductsBySku(viewModelScope, skus)
        pagedResults.fetchInitialPage().get()
        val pages = pagedResults.getFetchedPages()
        val products = pages.firstOrNull()?.getContents() ?: emptyList()
        val text =
            if (products.isEmpty()) {
              "No products found for SKUs: $skus"
            } else {
              products.joinToString("\n") { product ->
                "${product.sku} - ${product.name} - ${product.price}"
              }
            }
        _uiState.update { it.copy(isLoading = false, resultMessage = text) }
      } catch (e: Exception) {
        Log.e(TAG, "getProducts failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  fun getViewerPurchases() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val purchases = iap.getViewerPurchases()
        val text =
            if (purchases.isEmpty()) {
              "No purchases found"
            } else {
              purchases.joinToString("\n") { it.toString() }
            }
        _uiState.update { it.copy(isLoading = false, resultMessage = text) }
      } catch (e: Exception) {
        Log.e(TAG, "getViewerPurchases failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  fun launchCheckoutFlow() {
    val sku = _uiState.value.selectedSkus.firstOrNull() ?: return
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val purchase = iap.launchCheckoutFlow(sku)
        _uiState.update {
          it.copy(isLoading = false, resultMessage = "Checkout complete: ${purchase.json}")
        }
      } catch (e: Exception) {
        Log.e(TAG, "launchCheckoutFlow failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  fun consumePurchase() {
    val sku = _uiState.value.selectedSkus.firstOrNull() ?: return
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        iap.consumePurchase(sku)
        _uiState.update { it.copy(isLoading = false, resultMessage = "Purchase consumed: $sku") }
      } catch (e: Exception) {
        Log.e(TAG, "consumePurchase failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  companion object {
    private const val TAG = "IapViewModel"
  }
}
