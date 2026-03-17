/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.languagepack

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.languagepack.LanguagePack
import horizon.platform.languagepack.LanguagePackException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LanguagePackUiState(
    val isLoading: Boolean = false,
    val isDownloading: Boolean = false,
    val isComplete: Boolean = false,
    val bytesTransferred: Long = 0,
    val bytesTotal: Long = 0,
    val languageTag: String = "",
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class LanguagePackViewModel : ViewModel() {

  private val languagePack = LanguagePack()

  private val _uiState = MutableStateFlow(LanguagePackUiState())
  val uiState: StateFlow<LanguagePackUiState> = _uiState

  fun updateLanguageTag(tag: String) {
    _uiState.update { it.copy(languageTag = tag) }
  }

  fun getCurrent() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val assetDetails = languagePack.getCurrent()
        Log.d(TAG, "getCurrent success: ${assetDetails.assetId}")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage =
                  "Current language pack:\n" +
                      "assetId=${assetDetails.assetId}\n" +
                      "downloadStatus=${assetDetails.downloadStatus}\n" +
                      "filepath=${assetDetails.filepath}",
          )
        }
      } catch (e: LanguagePackException) {
        Log.e(TAG, "getCurrent failed", e)
        _uiState.update {
          it.copy(
              isLoading = false,
              errorMessage =
                  "Error: request=${e.requestName}, code=${e.code}, message=${e.message}",
          )
        }
      }
    }
  }

  fun setCurrent(lang: String) {
    _uiState.update {
      it.copy(
          isLoading = true,
          isDownloading = false,
          isComplete = false,
          resultMessage = null,
          errorMessage = null,
      )
    }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val downloadResult = languagePack.setCurrent(lang)
        Log.d(TAG, "setCurrent success: assetId=${downloadResult.assetId}")
        _uiState.update {
          it.copy(
              isLoading = false,
              isComplete = true,
              resultMessage =
                  "Set to $lang: assetId=${downloadResult.assetId}, " +
                      "filepath=${downloadResult.filepath}",
          )
        }
      } catch (e: LanguagePackException) {
        Log.e(TAG, "setCurrent failed", e)
        _uiState.update {
          it.copy(
              isLoading = false,
              errorMessage =
                  "Error: request=${e.requestName}, code=${e.code}, message=${e.message}",
          )
        }
      }
    }
  }

  companion object {
    private const val TAG = "LanguagePackVM"
  }
}
