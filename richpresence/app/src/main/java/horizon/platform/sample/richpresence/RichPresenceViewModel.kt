/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.richpresence

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.richpresence.RichPresence
import horizon.platform.richpresence.options.RichPresenceOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RichPresenceUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

@Suppress("DEPRECATION")
class RichPresenceViewModel : ViewModel() {

  private val richPresence = RichPresence()

  private val _uiState = MutableStateFlow(RichPresenceUiState())
  val uiState: StateFlow<RichPresenceUiState> = _uiState

  fun setPresence() {
    executeAction("set") {
      val options =
          RichPresenceOptions.builder()
              .withApiName("sample_destination")
              .withDeeplinkMessageOverride("sample_deeplink_message")
              .withIsJoinable(true)
              .build()
      richPresence.set(options)
      "Rich presence set successfully"
    }
  }

  fun clearPresence() {
    executeAction("clear") {
      richPresence.clear()
      "Rich presence cleared successfully"
    }
  }

  fun getDestinations() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val pagedResults = richPresence.getDestinations(viewModelScope)
        pagedResults.fetchInitialPage().get()
        val pages = pagedResults.getFetchedPages()
        val destinations = pages.firstOrNull()?.getContents() ?: emptyList()
        val result =
            if (destinations.isEmpty()) {
              "No destinations found"
            } else {
              "Found ${destinations.size} destinations:\n${destinations.joinToString("\n") { dest -> dest.json }}"
            }
        Log.d(TAG, "getDestinations result: $result")
        _uiState.update { it.copy(isLoading = false, resultMessage = result) }
      } catch (e: Exception) {
        Log.e(TAG, "getDestinations failed", e)
        _uiState.update {
          it.copy(isLoading = false, errorMessage = "Error: ${e.message ?: "Unknown error"}")
        }
      }
    }
  }

  private fun executeAction(actionName: String, action: suspend () -> String) {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = action()
        Log.d(TAG, "$actionName result: $result")
        _uiState.update { it.copy(isLoading = false, resultMessage = result) }
      } catch (e: Exception) {
        Log.e(TAG, "$actionName failed", e)
        _uiState.update {
          it.copy(isLoading = false, errorMessage = "Error: ${e.message ?: "Unknown error"}")
        }
      }
    }
  }

  companion object {
    private const val TAG = "RichPresenceViewModel"
  }
}
