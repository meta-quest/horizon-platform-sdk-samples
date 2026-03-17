/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.applicationlifecycle

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.applicationlifecycle.ApplicationLifecycle
import horizon.platform.applicationlifecycle.enums.LaunchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationLifecycleUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class ApplicationLifecycleViewModel : ViewModel() {

  private val applicationLifecycle = ApplicationLifecycle()

  private val _uiState = MutableStateFlow(ApplicationLifecycleUiState())
  val uiState: StateFlow<ApplicationLifecycleUiState> = _uiState

  fun getLaunchDetails() {
    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val launchDetails = applicationLifecycle.getLaunchDetails()
        Log.d(TAG, "getLaunchDetails result: $launchDetails")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage =
                  "Launch type: ${launchDetails.launchType}\n" +
                      "Deeplink message: ${launchDetails.deeplinkMessage}\n" +
                      "Tracking ID: ${launchDetails.trackingId}",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "getLaunchDetails failed", e)
        _uiState.update {
          it.copy(
              isLoading = false,
              errorMessage = "Error: ${e.message}",
          )
        }
      }
    }
  }

  fun logDeeplinkResult() {
    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val launchDetails = applicationLifecycle.getLaunchDetails()
        val trackingId = launchDetails.trackingId
        if (trackingId == null) {
          _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = "Error: Tracking ID is null",
            )
          }
          return@launch
        }
        applicationLifecycle.logDeeplinkResult(trackingId, LaunchResult.Unknown)
        Log.d(TAG, "logDeeplinkResult succeeded for trackingId: $trackingId")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage = "Logged deeplink result for tracking ID: $trackingId",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "logDeeplinkResult failed", e)
        _uiState.update {
          it.copy(
              isLoading = false,
              errorMessage = "Error: ${e.message}",
          )
        }
      }
    }
  }

  fun listenForLaunchIntentChanged() {
    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val intent = applicationLifecycle.launchIntentChanged().first()
        Log.d(TAG, "launchIntentChanged result: $intent")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage = "Launch intent changed: $intent",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "launchIntentChanged failed", e)
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
    private const val TAG = "AppLifecycleViewModel"
  }
}
