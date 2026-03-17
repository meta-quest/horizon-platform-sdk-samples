/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.achievements

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.achievements.Achievements
import horizon.platform.achievements.AchievementsException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AchievementsUiState(
    val isLoading: Boolean = false,
    val achievementName: String = "",
    val achievementCount: String = "",
    val achievementFields: String = "",
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class AchievementsViewModel : ViewModel() {

  private val achievements = Achievements()
  private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  private val _uiState = MutableStateFlow(AchievementsUiState())
  val uiState: StateFlow<AchievementsUiState> = _uiState

  fun updateAchievementName(name: String) {
    _uiState.update { it.copy(achievementName = name) }
  }

  fun updateAchievementCount(count: String) {
    _uiState.update { it.copy(achievementCount = count) }
  }

  fun updateAchievementFields(fields: String) {
    _uiState.update { it.copy(achievementFields = fields) }
  }

  fun unlock(name: String) {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = achievements.unlock(name)
        Log.d(TAG, "unlock success: ${result.name}")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage =
                  "Unlocked: ${result.name}\n" + "Just Unlocked: ${result.justUnlocked}",
          )
        }
      } catch (e: AchievementsException) {
        Log.e(TAG, "unlock failed", e)
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

  fun addCount(name: String, count: String) {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val countValue = count.toULongOrNull()
        if (countValue == null) {
          _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid count value") }
          return@launch
        }
        val result = achievements.addCount(name, countValue)
        Log.d(TAG, "addCount success: ${result.name}")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage =
                  "Added count to: ${result.name}\n" + "Just Unlocked: ${result.justUnlocked}",
          )
        }
      } catch (e: AchievementsException) {
        Log.e(TAG, "addCount failed", e)
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

  fun addFields(name: String, fields: String) {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = achievements.addFields(name, fields)
        Log.d(TAG, "addFields success: ${result.name}")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage =
                  "Added fields to: ${result.name}\n" + "Just Unlocked: ${result.justUnlocked}",
          )
        }
      } catch (e: AchievementsException) {
        Log.e(TAG, "addFields failed", e)
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

  fun getAll() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val pagedResults = achievements.getAllDefinitions(repositoryScope)
        val page = pagedResults.fetchedPages.firstOrNull()
        val definitions = page?.contents ?: emptyList()
        val text =
            if (definitions.isEmpty()) {
              "No achievement definitions found"
            } else {
              definitions.joinToString("\n\n") { definition ->
                "Name: ${definition.name}\n" +
                    "Type: ${definition.type}\n" +
                    "Target: ${definition.target}\n" +
                    "Bitfield Length: ${definition.bitfieldLength}"
              }
            }
        Log.d(TAG, "getAll success: ${definitions.size} definitions")
        _uiState.update { it.copy(isLoading = false, resultMessage = text) }
      } catch (e: AchievementsException) {
        Log.e(TAG, "getAll failed", e)
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

  fun getByName(name: String) {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val names = name.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val pagedResults = achievements.getDefinitionsByName(repositoryScope, names)
        val page = pagedResults.fetchedPages.firstOrNull()
        val definitions = page?.contents ?: emptyList()
        val text =
            if (definitions.isEmpty()) {
              "No achievement definitions found for: $name"
            } else {
              definitions.joinToString("\n\n") { definition ->
                "Name: ${definition.name}\n" +
                    "Type: ${definition.type}\n" +
                    "Target: ${definition.target}\n" +
                    "Bitfield Length: ${definition.bitfieldLength}"
              }
            }
        Log.d(TAG, "getByName success: ${definitions.size} definitions")
        _uiState.update { it.copy(isLoading = false, resultMessage = text) }
      } catch (e: AchievementsException) {
        Log.e(TAG, "getByName failed", e)
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

  fun getProgress() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val pagedResults = achievements.getAllProgress(repositoryScope)
        val page = pagedResults.fetchedPages.firstOrNull()
        val progressList = page?.contents ?: emptyList()
        val text =
            if (progressList.isEmpty()) {
              "No achievement progress found"
            } else {
              progressList.joinToString("\n\n") { progress ->
                "Name: ${progress.name}\n" +
                    "Unlocked: ${progress.isUnlocked}\n" +
                    "Count: ${progress.count}\n" +
                    "Bitfield: ${progress.bitfield ?: "N/A"}\n" +
                    "Unlock Time: ${progress.unlockTime}"
              }
            }
        Log.d(TAG, "getProgress success: ${progressList.size} entries")
        _uiState.update { it.copy(isLoading = false, resultMessage = text) }
      } catch (e: AchievementsException) {
        Log.e(TAG, "getProgress failed", e)
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

  override fun onCleared() {
    super.onCleared()
    repositoryScope.cancel()
  }

  companion object {
    private const val TAG = "AchievementsVM"
  }
}
