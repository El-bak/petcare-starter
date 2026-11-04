package com.example.petcare

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Test
import org.junit.runner.RunWith

/** Test du socle fourni ; il ne valide pas les exercices à réaliser. */
@RunWith(AndroidJUnit4::class)
class StarterSmokeTest {
    @Test fun ecranEtSelectionDemarrent() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.animalText)).check(matches(isDisplayed()))
            onView(withId(R.id.nextButton)).perform(click())
            onView(withId(R.id.countText)).check(matches(isDisplayed()))
        }
    }

    @Test fun boutonsIncompletsNePlantentPas() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.careButton)).perform(click())
            onView(withId(R.id.snackButton)).perform(click())
            onView(withId(R.id.workButton)).perform(click())
            onView(withId(R.id.animalText)).check(matches(isDisplayed()))
        }
    }
}
