/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.richpresence

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
          RichPresenceScreen()
        }
      }
    }
  }
}

@Composable
fun RichPresenceScreen(viewModel: RichPresenceViewModel = viewModel()) {
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
          text = "Rich Presence Sample",
          style = MaterialTheme.typography.headlineMedium,
      )

      Spacer(modifier = Modifier.height(24.dp))

      Text(
          text =
              "Rich Presence has been deprecated in favor of Group Presence. " +
                  "This sample demonstrates the set(), clear(), and getDestinations() APIs.",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(24.dp))

      @Suppress("DEPRECATION")
      Button(
          onClick = { viewModel.setPresence() },
          enabled = !uiState.isLoading,
          modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Set Rich Presence" },
      ) {
        Text("Set Rich Presence")
      }

      Spacer(modifier = Modifier.height(8.dp))

      @Suppress("DEPRECATION")
      Button(
          onClick = { viewModel.clearPresence() },
          enabled = !uiState.isLoading,
          modifier =
              Modifier.fillMaxWidth().semantics { contentDescription = "Clear Rich Presence" },
      ) {
        Text("Clear Rich Presence")
      }

      Spacer(modifier = Modifier.height(8.dp))

      @Suppress("DEPRECATION")
      Button(
          onClick = { viewModel.getDestinations() },
          enabled = !uiState.isLoading,
          modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Get Destinations" },
      ) {
        Text("Get Destinations")
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (uiState.isLoading) {
        CircularProgressIndicator()
      }

      uiState.resultMessage?.let { message ->
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
          Text(
              text = message,
              modifier = Modifier.padding(16.dp),
              style = MaterialTheme.typography.bodyMedium,
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
