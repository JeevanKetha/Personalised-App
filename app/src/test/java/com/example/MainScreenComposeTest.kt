package com.example

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screen.JeevanMainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.JeevanViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w800dp-h2400dp")
class MainScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTabNavigationAndRendering() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = JeevanViewModel(application)

        composeTestRule.setContent {
            MyApplicationTheme {
                JeevanMainScreen(viewModel = viewModel)
            }
        }

        // 1. Verify dashboard rendering under default state
        composeTestRule.onNodeWithTag("jeevan_main_scaffold").assertExists()
        composeTestRule.onNodeWithTag("tab_dashboard").assertExists()

        // 2. Direct set to Finance tab, advance clock past Crossfade transition, and verify
        viewModel.setActiveTab("FINANCE")
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("portfolio_snapshot_card").assertExists()

        // 3. Direct set to Career tab, advance clock past transition, wait for DB seed, and verify
        viewModel.setActiveTab("CAREER")
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()
        
        // Wait up to 5000ms for database seeding to complete in background task
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag("career_scaffold_list").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        composeTestRule.onNodeWithTag("career_scaffold_list").assertExists()

        // 4. Direct set to Health tab and verify
        viewModel.setActiveTab("HEALTH")
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("start_timer_button").assertExists()

        // 5. Direct set to News tab and verify
        viewModel.setActiveTab("NEWS")
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("news_center_hub").assertExists()
    }
}
