/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.achievements

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AchievementsViewModelTest {

  @Test
  fun `initial ui state has correct defaults`() {
    val state = AchievementsUiState()

    assertThat(state.isLoading).isFalse()
    assertThat(state.achievementName).isEmpty()
    assertThat(state.achievementCount).isEmpty()
    assertThat(state.achievementFields).isEmpty()
    assertThat(state.resultMessage).isNull()
    assertThat(state.errorMessage).isNull()
  }

  @Test
  fun `ui state copy updates fields correctly`() {
    val state = AchievementsUiState()
    val updated =
        state.copy(
            isLoading = true,
            achievementName = "test_achievement",
            achievementCount = "5",
            achievementFields = "101",
        )

    assertThat(updated.isLoading).isTrue()
    assertThat(updated.achievementName).isEqualTo("test_achievement")
    assertThat(updated.achievementCount).isEqualTo("5")
    assertThat(updated.achievementFields).isEqualTo("101")
    assertThat(updated.resultMessage).isNull()
    assertThat(updated.errorMessage).isNull()
  }

  @Test
  fun `ui state copy preserves result message`() {
    val state = AchievementsUiState(resultMessage = "test result")
    val updated = state.copy(isLoading = false)

    assertThat(updated.resultMessage).isEqualTo("test result")
    assertThat(updated.isLoading).isFalse()
  }

  @Test
  fun `ui state copy preserves error message`() {
    val state = AchievementsUiState(errorMessage = "test error")
    val updated = state.copy(isLoading = false)

    assertThat(updated.errorMessage).isEqualTo("test error")
  }
}
