/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.deviceapplicationintegrity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.deviceapplicationintegrity.DeviceApplicationIntegrity
import horizon.platform.deviceapplicationintegrity.DeviceApplicationIntegrityException
import java.security.SecureRandom
import java.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeviceIntegrityUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class DeviceIntegrityViewModel : ViewModel() {

  private val deviceApplicationIntegrity = DeviceApplicationIntegrity()

  private val _uiState = MutableStateFlow(DeviceIntegrityUiState())
  val uiState: StateFlow<DeviceIntegrityUiState> = _uiState

  private fun generateChallengeNonce(): String {
    val randomBytes = ByteArray(16)
    SecureRandom().nextBytes(randomBytes)
    val base64Nonce = Base64.getEncoder().encodeToString(randomBytes)
    return base64Nonce.replace('+', '-').replace('/', '_')
  }

  fun getIntegrityToken() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val challengeNonce = generateChallengeNonce()
        val integrityToken = deviceApplicationIntegrity.getIntegrityToken(challengeNonce)
        Log.d(TAG, "getIntegrityToken success")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage = "Integrity Token: $integrityToken",
          )
        }
      } catch (e: DeviceApplicationIntegrityException) {
        Log.e(TAG, "getIntegrityToken failed", e)
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
    private const val TAG = "DeviceIntegrityVM"
  }
}
