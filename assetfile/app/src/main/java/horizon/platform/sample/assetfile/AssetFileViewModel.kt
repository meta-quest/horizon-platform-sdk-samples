/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.assetfile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.assetfile.AssetFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AssetFileUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class AssetFileViewModel : ViewModel() {

  private val assetFile = AssetFile()

  private val _uiState = MutableStateFlow(AssetFileUiState())
  val uiState: StateFlow<AssetFileUiState> = _uiState

  fun getList() {
    executeAction("getList") {
      val files = assetFile.getList()
      "Found ${files.size} assets:\n${files.joinToString("\n") { it.json }}"
    }
  }

  fun downloadById(assetId: String) {
    executeAction("downloadById") {
      val result = assetFile.downloadById(assetId)
      "Download result: assetId=${result.assetId}, filepath=${result.filepath}"
    }
  }

  fun downloadCancelById(assetId: String) {
    executeAction("downloadCancelById") {
      val result = assetFile.downloadCancelById(assetId)
      "Download cancel result: assetId=${result.assetId}, success=${result.success}"
    }
  }

  fun deleteById(assetId: String) {
    executeAction("deleteById") {
      val result = assetFile.deleteById(assetId)
      "Delete result: assetId=${result.assetId}, success=${result.success}"
    }
  }

  fun statusById(assetId: String) {
    executeAction("statusById") {
      val result = assetFile.statusById(assetId)
      "Status result: assetId=${result.assetId}, downloadStatus=${result.downloadStatus}"
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
    private const val TAG = "AssetFileViewModel"
  }
}
