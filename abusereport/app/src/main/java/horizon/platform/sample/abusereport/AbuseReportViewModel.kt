/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.abusereport

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.abusereport.AbuseReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AbuseReportUiState(
    val isLoading: Boolean = false,
    val isListening: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class AbuseReportViewModel : ViewModel() {

  private val abuseReport = AbuseReport()

  private val _uiState = MutableStateFlow(AbuseReportUiState())
  val uiState: StateFlow<AbuseReportUiState> = _uiState

  fun startListeningForReportButtonPressed() {
    _uiState.update { it.copy(isListening = true, isLoading = true, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = abuseReport.reportButtonPressed().first()
        Log.d(TAG, "reportButtonPressed result: $result")
        _uiState.update {
          it.copy(
              isLoading = false,
              isListening = false,
              resultMessage = "Report button pressed event received: $result",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "reportButtonPressed failed", e)
        _uiState.update {
          it.copy(
              isLoading = false,
              isListening = false,
              errorMessage = "Error: ${e.message}",
          )
        }
      }
    }
  }

  companion object {
    private const val TAG = "AbuseReportViewModel"
  }
}
