/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.iap

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
          IapScreen()
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IapScreen(viewModel: IapViewModel = viewModel()) {
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
          text = "IAP Sample",
          style = MaterialTheme.typography.headlineMedium,
      )

      Spacer(modifier = Modifier.height(16.dp))

      // SKU Selection
      Text(
          text = "SKU Management (Selected: ${uiState.selectedSkus.size})",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(8.dp))

      FlowRow(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        uiState.availableSkus.forEach { sku ->
          val isSelected = uiState.selectedSkus.contains(sku)
          if (isSelected) {
            Button(
                onClick = { viewModel.disableSku(sku) },
                modifier = Modifier.semantics { contentDescription = sku },
            ) {
              Text(sku)
            }
          } else {
            OutlinedButton(
                onClick = { viewModel.enableSku(sku) },
                modifier = Modifier.semantics { contentDescription = sku },
            ) {
              Text(sku)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // API Buttons
      Text(
          text = "IAP APIs",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Button(
            onClick = { viewModel.getProducts() },
            enabled = !uiState.isLoading && uiState.selectedSkus.isNotEmpty(),
            modifier = Modifier.weight(1f).semantics { contentDescription = "Get Products" },
        ) {
          Text("Get Products")
        }

        Button(
            onClick = { viewModel.getViewerPurchases() },
            enabled = !uiState.isLoading,
            modifier = Modifier.weight(1f).semantics { contentDescription = "Get Purchases" },
        ) {
          Text("Get Purchases")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Button(
            onClick = { viewModel.launchCheckoutFlow() },
            enabled = !uiState.isLoading && uiState.selectedSkus.size == 1,
            modifier = Modifier.weight(1f).semantics { contentDescription = "Launch Checkout" },
        ) {
          Text("Launch Checkout")
        }

        Button(
            onClick = { viewModel.consumePurchase() },
            enabled = !uiState.isLoading && uiState.selectedSkus.size == 1,
            modifier = Modifier.weight(1f).semantics { contentDescription = "Consume Purchase" },
        ) {
          Text("Consume Purchase")
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Output
      Text(
          text = "Output:",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.fillMaxWidth(),
      )

      if (uiState.isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
      }

      uiState.resultMessage?.let { message ->
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
        )
      }

      uiState.errorMessage?.let { error ->
        Spacer(modifier = Modifier.height(8.dp))
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
