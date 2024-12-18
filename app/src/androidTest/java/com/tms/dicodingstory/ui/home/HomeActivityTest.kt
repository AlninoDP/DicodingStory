package com.tms.dicodingstory.ui.home

import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.tms.dicodingstory.ui.auth.login.LoginActivity
import com.tms.dicodingstory.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule

class HomeActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}