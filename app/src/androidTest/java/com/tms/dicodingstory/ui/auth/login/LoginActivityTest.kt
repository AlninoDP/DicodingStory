package com.tms.dicodingstory.ui.auth.login

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.tms.dicodingstory.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.tms.dicodingstory.R
import com.tms.dicodingstory.utils.wrapEspressoIdlingResource
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
//        onView(withId(R.id.email_edit_text)).perform(
//            typeText("valin@gmail.com"),
//            closeSoftKeyboard()
//        )

        // Alternatively, directly target the child EditText:
        onView(
            AllOf.allOf(
            isDescendantOfA(withId(R.id.login_email_form)),
            withId(R.id.email_edit_text)
        )).perform(typeText("test@example.com"), closeSoftKeyboard())

        onView(withId(R.id.login_password_form)).perform(typeText("123qweasd"), closeSoftKeyboard())

        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withId(R.id.login_progress_bar)).check(matches(isDisplayed()))

        onView(withId(R.id.home_main)).check(matches(isDisplayed()))

    }

}