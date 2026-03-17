/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.abusereport

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AbuseReportViewModelTest {

  @Test
  fun `initial ui state has correct defaults`() {
    val state = AbuseReportUiState()

    assertThat(state.isLoading).isFalse()
    assertThat(state.isListening).isFalse()
    assertThat(state.resultMessage).isNull()
    assertThat(state.errorMessage).isNull()
  }

  @Test
  fun `ui state copy updates fields correctly`() {
    val state = AbuseReportUiState()
    val updated = state.copy(isLoading = true, isListening = true)

    assertThat(updated.isLoading).isTrue()
    assertThat(updated.isListening).isTrue()
    assertThat(updated.resultMessage).isNull()
    assertThat(updated.errorMessage).isNull()
  }

  @Test
  fun `ui state copy preserves result message`() {
    val state = AbuseReportUiState(resultMessage = "test result")
    val updated = state.copy(isLoading = false)

    assertThat(updated.resultMessage).isEqualTo("test result")
    assertThat(updated.isLoading).isFalse()
  }

  @Test
  fun `ui state copy preserves error message`() {
    val state = AbuseReportUiState(errorMessage = "test error")
    val updated = state.copy(isListening = false, isLoading = false)

    assertThat(updated.errorMessage).isEqualTo("test error")
  }
}
