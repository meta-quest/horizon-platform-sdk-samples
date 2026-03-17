/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.assetfile

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AssetFileViewModelTest {

  @Test
  fun `initial ui state has correct defaults`() {
    val state = AssetFileUiState()

    assertThat(state.isLoading).isFalse()
    assertThat(state.resultMessage).isNull()
    assertThat(state.errorMessage).isNull()
  }

  @Test
  fun `ui state copy updates fields correctly`() {
    val state = AssetFileUiState()
    val updated = state.copy(isLoading = true)

    assertThat(updated.isLoading).isTrue()
    assertThat(updated.resultMessage).isNull()
    assertThat(updated.errorMessage).isNull()
  }

  @Test
  fun `ui state copy preserves result message`() {
    val state = AssetFileUiState(resultMessage = "test result")
    val updated = state.copy(isLoading = false)

    assertThat(updated.resultMessage).isEqualTo("test result")
    assertThat(updated.isLoading).isFalse()
  }

  @Test
  fun `ui state copy preserves error message`() {
    val state = AssetFileUiState(errorMessage = "test error")
    val updated = state.copy(isLoading = false)

    assertThat(updated.errorMessage).isEqualTo("test error")
  }
}
