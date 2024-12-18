package com.tms.dicodingstory.ui.auth.login

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
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.tms.dicodingstory.R
import org.hamcrest.core.AllOf


class LoginActivityTest {

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


    @Test
    fun loginSuccess() {
        // Write email to the edit text
        onView(
            AllOf.allOf(
                isDescendantOfA(withId(R.id.login_email_form)),
                withId(R.id.email_edit_text)
            )
        ).perform(typeText("valin@gmail.com"), closeSoftKeyboard())

        // Write password to the edit text
        onView(
            AllOf.allOf(
                isDescendantOfA(withId(R.id.login_password_form)),
                withId(R.id.password_edit_text)
            )
        ).perform(typeText("123qweasd"), closeSoftKeyboard())

        // Click the sign in button and wait
        onView(withId(R.id.btn_sign_in)).perform(click())
        // Check if the RecyclerView in HomeActivity is displayed
        onView(withId(R.id.home_rv)).check(matches(isDisplayed()))
    }

}

