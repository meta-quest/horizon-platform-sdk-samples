/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.languagepack

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LanguagePackViewModelTest {

  @Test
  fun `initial ui state has correct defaults`() {
    val state = LanguagePackUiState()

    assertThat(state.isLoading).isFalse()
    assertThat(state.isDownloading).isFalse()
    assertThat(state.isComplete).isFalse()
    assertThat(state.bytesTransferred).isEqualTo(0L)
    assertThat(state.bytesTotal).isEqualTo(0L)
    assertThat(state.languageTag).isEmpty()
    assertThat(state.resultMessage).isNull()
    assertThat(state.errorMessage).isNull()
  }

  @Test
  fun `ui state copy updates fields correctly`() {
    val state = LanguagePackUiState()
    val updated =
        state.copy(
            isLoading = true,
            isDownloading = true,
            languageTag = "de",
            bytesTransferred = 500L,
            bytesTotal = 1000L,
        )

    assertThat(updated.isLoading).isTrue()
    assertThat(updated.isDownloading).isTrue()
    assertThat(updated.isComplete).isFalse()
    assertThat(updated.languageTag).isEqualTo("de")
    assertThat(updated.bytesTransferred).isEqualTo(500L)
    assertThat(updated.bytesTotal).isEqualTo(1000L)
    assertThat(updated.resultMessage).isNull()
    assertThat(updated.errorMessage).isNull()
  }

  @Test
  fun `ui state copy preserves result message`() {
    val state = LanguagePackUiState(resultMessage = "test result")
    val updated = state.copy(isLoading = false)

    assertThat(updated.resultMessage).isEqualTo("test result")
    assertThat(updated.isLoading).isFalse()
  }

  @Test
  fun `ui state copy preserves error message`() {
    val state = LanguagePackUiState(errorMessage = "test error")
    val updated = state.copy(isLoading = false)

    assertThat(updated.errorMessage).isEqualTo("test error")
  }

  @Test
  fun `ui state copy can mark complete`() {
    val state = LanguagePackUiState()
    val updated = state.copy(isComplete = true, isDownloading = false)

    assertThat(updated.isComplete).isTrue()
    assertThat(updated.isDownloading).isFalse()
  }
}
