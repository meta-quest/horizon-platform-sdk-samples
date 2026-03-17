/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.achievements

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import horizon.core.android.driver.coroutines.HorizonServiceConnection

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

  private val APPLICATION_ID: String
    get() =
        throw IllegalStateException(
            "Please set your APPLICATION_ID. " +
                "Follow the instructions at https://developers.meta.com/horizon/documentation/android-apps/ps-setup-kotlin/ " +
                "to create and retrieve your application ID."
        )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Log.i(TAG, "Connecting to Horizon Service")
    HorizonServiceConnection.connect(
        APPLICATION_ID,
        this@MainActivity.applicationContext,
        lifecycleScope,
    )
    Log.i(TAG, "Done connecting to Horizon Service")

    setContent {
      MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          AchievementsScreen()
        }
      }
    }
  }
}

@Composable
fun AchievementsScreen(viewModel: AchievementsViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold { innerPadding ->
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
          text = "Achievements Sample",
          style = MaterialTheme.typography.headlineMedium,
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Achievement name input
      OutlinedTextField(
          value = uiState.achievementName,
          onValueChange = { viewModel.updateAchievementName(it) },
          label = { Text("Achievement name") },
          placeholder = { Text("e.g., simple_achievement_1") },
          modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Count and fields inputs
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        OutlinedTextField(
            value = uiState.achievementCount,
            onValueChange = { viewModel.updateAchievementCount(it) },
            label = { Text("Count") },
            placeholder = { Text("e.g., 2") },
            modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
            value = uiState.achievementFields,
            onValueChange = { viewModel.updateAchievementFields(it) },
            label = { Text("Fields") },
            placeholder = { Text("e.g., 101") },
            modifier = Modifier.weight(1f),
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Action buttons
      Text(
          text = "Actions:",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(bottom = 8.dp),
      )

      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Button(
            onClick = { viewModel.unlock(uiState.achievementName) },
            enabled = !uiState.isLoading && uiState.achievementName.isNotBlank(),
            modifier = Modifier.weight(1f).semantics { contentDescription = "Unlock" },
        ) {
          Text("Unlock")
        }
        Button(
            onClick = { viewModel.addCount(uiState.achievementName, uiState.achievementCount) },
            enabled =
                !uiState.isLoading &&
                    uiState.achievementName.isNotBlank() &&
                    uiState.achievementCount.isNotBlank(),
            modifier = Modifier.weight(1f).semantics { contentDescription = "Add Count" },
        ) {
          Text("Add Count")
        }
        Button(
            onClick = { viewModel.addFields(uiState.achievementName, uiState.achievementFields) },
            enabled =
                !uiState.isLoading &&
                    uiState.achievementName.isNotBlank() &&
                    uiState.achievementFields.isNotBlank(),
            modifier = Modifier.weight(1f).semantics { contentDescription = "Add Fields" },
        ) {
          Text("Add Fields")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Button(
            onClick = { viewModel.getAll() },
            enabled = !uiState.isLoading,
            modifier = Modifier.weight(1f).semantics { contentDescription = "Get All" },
        ) {
          Text("Get All")
        }
        Button(
            onClick = { viewModel.getByName(uiState.achievementName) },
            enabled = !uiState.isLoading && uiState.achievementName.isNotBlank(),
            modifier = Modifier.weight(1f).semantics { contentDescription = "Get By Name" },
        ) {
          Text("Get By Name")
        }
        Button(
            onClick = { viewModel.getProgress() },
            enabled = !uiState.isLoading,
            modifier = Modifier.weight(1f).semantics { contentDescription = "Get Progress" },
        ) {
          Text("Get Progress")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Output section
      if (uiState.isLoading) {
        CircularProgressIndicator()
      }

      uiState.resultMessage?.let { message ->
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        ) {
          Text(
              text = message,
              modifier = Modifier.padding(16.dp),
              style = MaterialTheme.typography.bodySmall,
          )
        }
      }

      uiState.errorMessage?.let { error ->
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
        ) {
          Text(
              text = error,
              modifier = Modifier.padding(16.dp),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onErrorContainer,
          )
        }
      }
    }
  }
}
