/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.useragecategory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.useragecategory.UserAgeCategory
import horizon.platform.useragecategory.enums.AccountAgeCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserAgeCategoryUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class UserAgeCategoryViewModel : ViewModel() {

  private val userAgeCategory = UserAgeCategory()

  private val _uiState = MutableStateFlow(UserAgeCategoryUiState())
  val uiState: StateFlow<UserAgeCategoryUiState> = _uiState

  fun get() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = userAgeCategory.get()
        val ageCategoryText =
            when (result.ageCategory) {
              AccountAgeCategory.Ch -> "Age category: Child (CH)"
              AccountAgeCategory.Tn -> "Age category: Teen (TN)"
              AccountAgeCategory.Ad -> "Age category: Adult (AD)"
              AccountAgeCategory.Unknown -> "Age category: Unknown"
            }
        Log.d(TAG, "get success: $ageCategoryText")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage = ageCategoryText,
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "get failed", e)
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
    private const val TAG = "UserAgeCategoryVM"
  }
}
