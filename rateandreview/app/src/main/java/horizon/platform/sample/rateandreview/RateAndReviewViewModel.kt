/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.rateandreview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.rateandreview.RateAndReview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RateAndReviewUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class RateAndReviewViewModel : ViewModel() {

  private val rateAndReview = RateAndReview()

  private val _uiState = MutableStateFlow(RateAndReviewUiState())
  val uiState: StateFlow<RateAndReviewUiState> = _uiState

  fun canLaunchRateAndReview() {
    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = rateAndReview.canLaunchRateAndReview()
        Log.d(TAG, "canLaunchRateAndReview result: ${result.canViewerRateAndReview}")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage =
                  if (result.canViewerRateAndReview)
                      "Rate and review can be launched for this user."
                  else "Rate and review cannot be launched for this user.",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "canLaunchRateAndReview failed", e)
        _uiState.update {
          it.copy(
              isLoading = false,
              errorMessage = "Error: ${e.message}",
          )
        }
      }
    }
  }

  fun launchRateAndReview() {
    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        rateAndReview.rateAndReviewLauncher()
        Log.d(TAG, "rateAndReviewLauncher succeeded")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage = "Rate and review flow launched successfully.",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "rateAndReviewLauncher failed", e)
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
    private const val TAG = "RateAndReviewViewModel"
  }
}
