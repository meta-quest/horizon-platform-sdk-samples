/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.application

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.application.Application
import horizon.platform.application.options.ApplicationOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class ApplicationViewModel : ViewModel() {

  private val application = Application()

  private val _uiState = MutableStateFlow(ApplicationUiState())
  val uiState: StateFlow<ApplicationUiState> = _uiState

  fun getVersion() {
    executeAction("getVersion") {
      val version = application.getVersion()
      "Version result: ${version.json}"
    }
  }

  fun launchOtherApp() {
    executeAction("launchOtherApp") {
      val result =
          application.launchOtherApp(
              appId = BEAT_SABER_APP_ID,
              deeplinkOptions =
                  ApplicationOptions.builder()
                      .withDeeplinkMessage("deeplink_msg_for_test_destination_beat_saber")
                      .withDestinationApiName("test_destination_beat_saber")
                      .build(),
          )
      "Launch result: $result"
    }
  }

  fun startAppDownload() {
    executeAction("startAppDownload") {
      val result = application.startAppDownload()
      "Start app download result: ${result.json}"
    }
  }

  fun cancelAppDownload() {
    executeAction("cancelAppDownload") {
      val result = application.cancelAppDownload()
      "Cancel app download result: ${result.json}"
    }
  }

  fun installAppUpdateAndRelaunch() {
    executeAction("installAppUpdateAndRelaunch") {
      val result = application.installAppUpdateAndRelaunch()
      "Install app update and relaunch result: ${result.json}"
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
    private const val TAG = "ApplicationViewModel"
    private const val BEAT_SABER_APP_ID = "2448060205267927"
  }
}
