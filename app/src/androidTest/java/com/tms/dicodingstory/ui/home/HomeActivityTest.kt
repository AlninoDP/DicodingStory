package com.tms.dicodingstory.ui.home

import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.tms.dicodingstory.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.tms.dicodingstory.R

class HomeActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(HomeActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun logoutSuccess(){
        // Open the overflow menu
        onView(withContentDescription("More options")).perform(click())

        // Click on a specific menu item (e.g., Settings)
        onView(withText(R.string.log_out)).perform(click())

        // Check if the text view Header in LoginActivity is displayed
        onView(withId(R.id.tv_header_login)).check(matches(isDisplayed()))
    }
}