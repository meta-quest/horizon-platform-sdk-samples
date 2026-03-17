/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.leaderboards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.leaderboards.Leaderboards
import horizon.platform.leaderboards.enums.LeaderboardFilterType
import horizon.platform.leaderboards.enums.LeaderboardStartAt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaderboardsUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class LeaderboardsViewModel : ViewModel() {

  private val leaderboards = Leaderboards()

  private val _uiState = MutableStateFlow(LeaderboardsUiState())
  val uiState: StateFlow<LeaderboardsUiState> = _uiState

  private val _leaderboardName = MutableStateFlow(DEFAULT_LEADERBOARD_NAME)
  val leaderboardName: StateFlow<String> = _leaderboardName

  private val _currentScore = MutableStateFlow(DEFAULT_SCORE)
  val currentScore: StateFlow<Long> = _currentScore

  fun setLeaderboardName(name: String) {
    _leaderboardName.update { name }
  }

  fun updateScore(calculate: (Long) -> Long) {
    _currentScore.update { calculate(it) }
  }

  fun get() {
    val name = _leaderboardName.value
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val pagedResults = leaderboards.get(viewModelScope, name)
        pagedResults.fetchInitialPage().get()
        val pages = pagedResults.getFetchedPages()
        val results = pages.firstOrNull()?.getContents() ?: emptyList()
        val text =
            if (results.isEmpty()) {
              "No leaderboards found for name: $name"
            } else {
              results.joinToString("\n") { entry ->
                "ApiName: ${entry.apiName}, ID: ${entry.id}, Destination: ${entry.destination}"
              }
            }
        _uiState.update { it.copy(isLoading = false, resultMessage = "Get Leaderboard:\n$text") }
      } catch (e: Exception) {
        Log.e(TAG, "get() failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  fun getEntries() {
    val name = _leaderboardName.value
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val pagedResults =
            leaderboards.getEntries(
                viewModelScope,
                name,
                5,
                LeaderboardFilterType.None,
                LeaderboardStartAt.Top,
            )
        pagedResults.fetchInitialPage().get()
        val pages = pagedResults.getFetchedPages()
        val results = pages.firstOrNull()?.getContents() ?: emptyList()
        val text =
            if (results.isEmpty()) {
              "No entries found"
            } else {
              results.joinToString("\n") { entry ->
                "Rank: ${entry.rank}, Score: ${entry.score}, User: ${entry.user.id}"
              }
            }
        _uiState.update {
          it.copy(isLoading = false, resultMessage = "Get Leaderboard Entries:\n$text")
        }
      } catch (e: Exception) {
        Log.e(TAG, "getEntries() failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  fun getEntriesByIds() {
    val name = _leaderboardName.value
    // Example user IDs for demonstration
    val userIds = listOf("23921684890790836", "7967249980044820")
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val pagedResults =
            leaderboards.getEntriesByIds(
                viewModelScope,
                name,
                5,
                LeaderboardStartAt.Top,
                userIds,
            )
        pagedResults.fetchInitialPage().get()
        val pages = pagedResults.getFetchedPages()
        val results = pages.firstOrNull()?.getContents() ?: emptyList()
        val text =
            if (results.isEmpty()) {
              "No entries found for given user IDs"
            } else {
              results.joinToString("\n") { entry ->
                "Rank: ${entry.rank}, Score: ${entry.score}, User: ${entry.user.id}"
              }
            }
        _uiState.update { it.copy(isLoading = false, resultMessage = "Get Entries By IDs:\n$text") }
      } catch (e: Exception) {
        Log.e(TAG, "getEntriesByIds() failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  fun writeEntry() {
    val name = _leaderboardName.value
    val score = _currentScore.value
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = leaderboards.writeEntry(name, score, null, null)
        _uiState.update {
          it.copy(isLoading = false, resultMessage = "Write Entry: ${result.json}")
        }
      } catch (e: Exception) {
        Log.e(TAG, "writeEntry() failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  fun writeEntryWithSupplementaryMetric() {
    val name = _leaderboardName.value
    val score = _currentScore.value
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = leaderboards.writeEntryWithSupplementaryMetric(name, score, 5L, null, null)
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage = "Write Entry with Supplementary Metric: ${result.json}",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "writeEntryWithSupplementaryMetric() failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  companion object {
    private const val TAG = "LeaderboardsViewModel"
    const val DEFAULT_LEADERBOARD_NAME = "sample_leaderboard_visible"
    const val DEFAULT_SCORE = 120L
  }
}
