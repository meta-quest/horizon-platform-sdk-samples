/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.iap

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class IapViewModelTest {

  @Test
  fun `initial ui state has correct defaults`() {
    val state = IapUiState()

    assertThat(state.isLoading).isFalse()
    assertThat(state.resultMessage).isNull()
    assertThat(state.errorMessage).isNull()
    assertThat(state.availableSkus)
        .containsExactly("SKU-sub-1", "sku-consumable-1", "sku-dur-1", "sku-dur-2", "sku1", "sku2")
    assertThat(state.selectedSkus).isEmpty()
  }

  @Test
  fun `ui state copy updates fields correctly`() {
    val state = IapUiState()
    val updated = state.copy(isLoading = true, selectedSkus = listOf("sku1"))

    assertThat(updated.isLoading).isTrue()
    assertThat(updated.selectedSkus).containsExactly("sku1")
    assertThat(updated.resultMessage).isNull()
    assertThat(updated.errorMessage).isNull()
  }

  @Test
  fun `ui state copy preserves result message`() {
    val state = IapUiState(resultMessage = "test result")
    val updated = state.copy(isLoading = false)

    assertThat(updated.resultMessage).isEqualTo("test result")
    assertThat(updated.isLoading).isFalse()
  }

  @Test
  fun `ui state copy preserves error message`() {
    val state = IapUiState(errorMessage = "test error")
    val updated = state.copy(isLoading = false)

    assertThat(updated.errorMessage).isEqualTo("test error")
  }

  @Test
  fun `ui state copy preserves available skus`() {
    val state = IapUiState()
    val updated = state.copy(selectedSkus = listOf("sku1", "sku2"))

    assertThat(updated.availableSkus)
        .containsExactly("SKU-sub-1", "sku-consumable-1", "sku-dur-1", "sku-dur-2", "sku1", "sku2")
    assertThat(updated.selectedSkus).containsExactly("sku1", "sku2")
  }
}
