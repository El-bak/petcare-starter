package com.example.petcare

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.containsString

/** Régression du socle M2 ; ne valide pas les exercices à réaliser. */
@RunWith(AndroidJUnit4::class)
class StarterSmokeTest {
    @Test fun ecranEtSelectionDemarrent() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.animalText)).check(matches(isDisplayed()))
            onView(withId(R.id.nextButton)).perform(click())
            onView(withId(R.id.countText)).check(matches(isDisplayed()))
        }
    }

    @Test fun acquisM1RestentDisponibles() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.careButton)).perform(scrollTo(), click())
            onView(withId(R.id.countText)).check(matches(withText(containsString("1"))))
            onView(withId(R.id.snackButton)).perform(scrollTo(), click())
            onView(withText(R.string.undo)).perform(click())
            onView(withId(R.id.countText)).check(matches(withText(containsString("1"))))
        }
    }

    @Test fun pagesIncompletesRestentNavigables() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.aboutButton)).perform(scrollTo(), click())
            onView(withText(R.string.about_pending)).check(matches(isDisplayed()))
            onView(withId(R.id.closeButton)).perform(click())
            onView(withId(R.id.animalsButton)).perform(scrollTo(), click())
            onView(withId(R.id.listStatus)).check(matches(withText(R.string.list_pending)))
            pressBack()
            onView(withId(R.id.nextButton)).perform(scrollTo(), click())
            onView(withId(R.id.animalText)).check(matches(withText(containsString("Oslo"))))
        }
    }

    @Test fun tiroirEtRecreationNePlantentPas() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withContentDescription(R.string.drawer_open)).perform(click())
            onView(withId(R.id.navigationView)).check(matches(isDisplayed()))
            pressBack()
            scenario.recreate()
            onView(withId(R.id.animalText)).check(matches(isDisplayed()))
        }
    }
}
