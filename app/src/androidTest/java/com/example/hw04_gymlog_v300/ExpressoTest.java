package com.example.hw04_gymlog_v300;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class ExpressoTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void fullFlowTest() throws InterruptedException {

        // Login
        onView(withId(R.id.usernameLoginEditText))
                .perform(replaceText("admin1"));
        closeSoftKeyboard();

        onView(withId(R.id.passwordLoginEditText))
                .perform(replaceText("admin1"));
        closeSoftKeyboard();

        onView(withId(R.id.loginButton))
                .perform(click());

        Thread.sleep(1000);

        // Add log
        onView(withId(R.id.exerciseInputEditText))
                .perform(replaceText("Espresso Squat"));
        closeSoftKeyboard();

        onView(withId(R.id.weightInputEditText))
                .perform(replaceText("315"));
        closeSoftKeyboard();

        onView(withId(R.id.repsInputEditText))
                .perform(replaceText("5"));
        closeSoftKeyboard();

        onView(withId(R.id.logButton))
                .perform(click());

        Thread.sleep(1000);

        // Verify log appears in RecyclerView item
        onView(withId(R.id.recyclerItemTextView))
                .check(matches(withText(containsString("Espresso Squat"))));

        // Logout
        onView(withId(R.id.logoutMenuItem))
                .perform(click());

        onView(withText("Logout"))
                .perform(click());

        Thread.sleep(1000);

        // Login again
        onView(withId(R.id.usernameLoginEditText))
                .perform(replaceText("admin1"));
        closeSoftKeyboard();

        onView(withId(R.id.passwordLoginEditText))
                .perform(replaceText("admin1"));
        closeSoftKeyboard();

        onView(withId(R.id.loginButton))
                .perform(click());

        Thread.sleep(1000);

        // Verify persistence
        onView(withId(R.id.recyclerItemTextView))
                .check(matches(withText(containsString("Espresso Squat"))))
                .check(matches(isDisplayed()));
    }
}