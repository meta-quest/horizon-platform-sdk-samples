/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.users

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.users.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsersUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class UsersViewModel : ViewModel() {

  private val users = Users()

  private val _uiState = MutableStateFlow(UsersUiState())
  val uiState: StateFlow<UsersUiState> = _uiState

  fun getUser(userId: String) {
    executeAction("get") {
      val user = users.get(userId)
      "User: ${user.json}"
    }
  }

  fun getLoggedInUser() {
    executeAction("getLoggedInUser") {
      val user = users.getLoggedInUser()
      "Logged in user: ${user.json}"
    }
  }

  fun getLoggedInUserFriends() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val pagedResults = users.getLoggedInUserFriends(viewModelScope)
        pagedResults.fetchInitialPage().get()
        val pages = pagedResults.getFetchedPages()
        val friends = pages.firstOrNull()?.getContents() ?: emptyList()
        val result =
            if (friends.isEmpty()) {
              "No friends found"
            } else {
              "Found ${friends.size} friends:\n${friends.joinToString("\n") { friend -> friend.json }}"
            }
        Log.d(TAG, "getLoggedInUserFriends result: $result")
        _uiState.update { it.copy(isLoading = false, resultMessage = result) }
      } catch (e: Exception) {
        Log.e(TAG, "getLoggedInUserFriends failed", e)
        _uiState.update {
          it.copy(isLoading = false, errorMessage = "Error: ${e.message ?: "Unknown error"}")
        }
      }
    }
  }

  fun getAccessToken() {
    executeAction("getAccessToken") {
      val token = users.getAccessToken()
      "Access token: $token"
    }
  }

  fun getUserProof() {
    executeAction("getUserProof") {
      val proof = users.getUserProof()
      "User proof: ${proof.json}"
    }
  }

  fun getOrgScopedId(userId: String) {
    executeAction("getOrgScopedId") {
      val orgScopedId = users.getOrgScopedId(userId)
      "Org scoped ID: ${orgScopedId.json}"
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
    private const val TAG = "UsersViewModel"
  }
}
