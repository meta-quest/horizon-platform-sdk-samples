/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.notifications.Notifications
import horizon.platform.notifications.NotificationsException
import horizon.platform.notifications.configs.DeviceNotificationConfig
import horizon.platform.notifications.enums.ActionDisplayType
import horizon.platform.notifications.enums.ActionIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class NotificationsViewModel : ViewModel() {

  private val notifications = Notifications()

  private val _uiState = MutableStateFlow(NotificationsUiState())
  val uiState: StateFlow<NotificationsUiState> = _uiState

  fun deviceNotification() {
    executeAction("deviceNotification") {
      val config =
          DeviceNotificationConfig.builder()
              .withTitle("Title from Notifications Sample App")
              .withMessage("Message from Notifications Sample App")
              .build()
      val response = notifications.deviceNotification(config)
      "Successfully sent notification: $response"
    }
  }

  fun toastOnly() {
    executeAction("toastOnly") {
      val config =
          DeviceNotificationConfig.builder()
              .withTitle("Device Notification Toast Only")
              .withMessage("This notification is a toast only")
              .withIsToastOnly(true)
              .build()
      val response = notifications.deviceNotification(config)
      "Successfully sent toast-only notification: $response"
    }
  }

  fun appStoreActionNotification() {
    executeAction("appStoreActionNotification") {
      val config =
          DeviceNotificationConfig.builder()
              .withTitle("App Store Action Notification")
              .withMessage("This notification opens the store page if Beatsaber is not installed")
              .withActionDisplayType(ActionDisplayType.Iconable)
              .withActionTitle("Open App")
              .withActionIcon(ActionIcon.Download)
              .withActionAppId(BEATSABER_APP_ID)
              .withActionPackageName("com.beatgames.beatsaber")
              .build()
      val response = notifications.deviceNotification(config)
      "Successfully sent notification with app store action: $response"
    }
  }

  fun zoomJoinIntentNotification() {
    executeAction("zoomJoinIntentNotification") {
      val ovrSocialLaunch =
          JSONObject().apply {
            put("type", "DEEPLINK")
            put("launch_source", "OTHER_APP")
            put("deeplink_message", "")
          }

      val intentCmd = JSONObject().apply { put("ovr_social_launch", ovrSocialLaunch) }

      val actionIntentExtras =
          JSONObject().apply {
            put("meeting_id", "123456789")
            put("source", "calendar_notification")
            put("intent_cmd", intentCmd.toString())
            put("skipDestinationUi", true)
          }

      val config =
          DeviceNotificationConfig.builder()
              .withTitle("Calendar Meeting - Zoom")
              .withMessage("Meeting starts in 5 minutes")
              .withActionDisplayType(ActionDisplayType.Iconable)
              .withActionTitle("Join Zoom")
              .withActionIcon(ActionIcon.Party)
              .withActionIntentData("https://zoom.us/j/123456789")
              .withActionPackageName("com.zoom.videomeetings")
              .withActionIntentExtras(actionIntentExtras.toString())
              .build()
      val response = notifications.deviceNotification(config)
      "Successfully sent Zoom join notification: $response"
    }
  }

  private fun executeAction(actionName: String, action: suspend () -> String) {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = action()
        Log.d(TAG, "$actionName result: $result")
        _uiState.update { it.copy(isLoading = false, resultMessage = result) }
      } catch (e: NotificationsException) {
        Log.e(TAG, "$actionName failed", e)
        _uiState.update {
          it.copy(
              isLoading = false,
              errorMessage = "Failed: ${e.requestName}, code=${e.code}, message=${e.message}",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "$actionName failed", e)
        _uiState.update {
          it.copy(isLoading = false, errorMessage = "Error: ${e.message ?: "Unknown error"}")
        }
      }
    }
  }

  companion object {
    private const val TAG = "NotificationsViewModel"
    private const val BEATSABER_APP_ID = "2448060205267927"
  }
}
