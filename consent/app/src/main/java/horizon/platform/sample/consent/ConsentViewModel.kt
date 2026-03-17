/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.consent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.consent.Consent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConsentUiState(
    val isLoading: Boolean = false,
    val consentFlowName: String = "rl_social_privacy_setting_dedup",
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class ConsentViewModel : ViewModel() {

  private val consent = Consent()

  private val _uiState = MutableStateFlow(ConsentUiState())
  val uiState: StateFlow<ConsentUiState> = _uiState

  fun updateConsentFlowName(name: String) {
    _uiState.update { it.copy(consentFlowName = name) }
  }

  fun getConsentStatus() {
    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val status = consent.getConsentStatus(_uiState.value.consentFlowName, null, emptyMap())
        Log.d(TAG, "getConsentStatus result: $status")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage = "Consent status: ${status[0].status}",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "getConsentStatus failed", e)
        _uiState.update {
          it.copy(
              isLoading = false,
              errorMessage = "Error: ${e.message}",
          )
        }
      }
    }
  }

  fun launchConsentIfRequired() {
    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result =
            consent.launchConsentIfRequired(_uiState.value.consentFlowName, null, emptyMap())
        Log.d(TAG, "launchConsentIfRequired result: $result")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage = "Consent launch outcome: ${result.outcome}",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "launchConsentIfRequired failed", e)
        _uiState.update {
          it.copy(
              isLoading = false,
              errorMessage = "Error: ${e.message}",
          )
        }
      }
    }
  }

  companion object {
    private const val TAG = "ConsentViewModel"
  }
}
