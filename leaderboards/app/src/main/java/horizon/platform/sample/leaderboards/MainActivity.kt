/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.leaderboards

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
          LeaderboardsScreen()
        }
      }
    }
  }
}

@Composable
fun LeaderboardsScreen(viewModel: LeaderboardsViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val leaderboardName by viewModel.leaderboardName.collectAsStateWithLifecycle()
  val currentScore by viewModel.currentScore.collectAsStateWithLifecycle()

  Scaffold { innerPadding ->
    Row(
        modifier = Modifier.fillMaxSize().padding(innerPadding).padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Left column - Controls
      Column(
          modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(text = "Leaderboards APIs", style = MaterialTheme.typography.headlineSmall)

        // Leaderboard name input
        OutlinedTextField(
            value = leaderboardName,
            onValueChange = { viewModel.setLeaderboardName(it) },
            label = { Text("Leaderboard Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Preset name buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Button(
              onClick = { viewModel.setLeaderboardName("sample_leaderboard_visible") },
              modifier = Modifier.weight(1f).semantics { contentDescription = "Default" },
          ) {
            Text("Default", style = MaterialTheme.typography.labelSmall)
          }
          Button(
              onClick = { viewModel.setLeaderboardName("gauntlet-test-leaderboard") },
              modifier = Modifier.weight(1f).semantics { contentDescription = "E2E Test" },
          ) {
            Text("E2E Test", style = MaterialTheme.typography.labelSmall)
          }
        }

        // Score controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Button(
              onClick = { viewModel.updateScore { it - 1 } },
              modifier = Modifier.semantics { contentDescription = "Decrease score" },
          ) {
            Text("-")
          }
          Text(
              text = "Score: $currentScore",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(horizontal = 16.dp),
          )
          Button(
              onClick = { viewModel.updateScore { it + 1 } },
              modifier = Modifier.semantics { contentDescription = "Increase score" },
          ) {
            Text("+")
          }
        }

        // Get APIs
        SectionTitle("Read APIs")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Button(
              onClick = { viewModel.get() },
              enabled = !uiState.isLoading,
              modifier = Modifier.weight(1f).semantics { contentDescription = "Get" },
          ) {
            Text("Get")
          }
          Button(
              onClick = { viewModel.getEntries() },
              enabled = !uiState.isLoading,
              modifier = Modifier.weight(1f).semantics { contentDescription = "Get Entries" },
          ) {
            Text("Get Entries")
          }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Button(
              onClick = { viewModel.getEntriesByIds() },
              enabled = !uiState.isLoading,
              modifier = Modifier.weight(1f).semantics { contentDescription = "Get By IDs" },
          ) {
            Text("Get By IDs")
          }
        }

        // Write APIs
        SectionTitle("Write APIs")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Button(
              onClick = { viewModel.writeEntry() },
              enabled = !uiState.isLoading,
              modifier = Modifier.weight(1f).semantics { contentDescription = "Write Entry" },
          ) {
            Text("Write Entry")
          }
          Button(
              onClick = { viewModel.writeEntryWithSupplementaryMetric() },
              enabled = !uiState.isLoading,
              modifier = Modifier.weight(1f).semantics { contentDescription = "Write + Metric" },
          ) {
            Text("Write + Metric")
          }
        }
      }

      // Right column - Output
      Column(
          modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(text = "Output", style = MaterialTheme.typography.headlineSmall)

        if (uiState.isLoading) {
          CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        uiState.resultMessage?.let { message ->
          Text(
              text = message,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.fillMaxWidth(),
          )
        }

        uiState.errorMessage?.let { error ->
          Text(
              text = "Error: $error",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.error,
              modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    }
  }
}

@Composable
private fun SectionTitle(title: String) {
  Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
  )
}
