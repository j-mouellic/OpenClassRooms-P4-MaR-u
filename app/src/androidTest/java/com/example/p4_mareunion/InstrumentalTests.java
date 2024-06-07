package com.example.p4_mareunion;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.p4_mareunion.utils.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNull.notNullValue;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.example.p4_mareunion.utils.DeleteViewAction;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.AllOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4ClassRunner.class)
public class InstrumentalTests {

    private ReunionListActivity activity;
    private int ITEMS_COUNT = 15;
    @Rule
    public ActivityTestRule<ReunionListActivity> activityRule =
            new ActivityTestRule(ReunionListActivity.class);

    @Before
    public void setUp() {
        activity = activityRule.getActivity();
        assertThat(activity, notNullValue());
    }

    @Test
    public void myReunionList_deleteAction_shouldRemoveItem() {
        onView(allOf(ViewMatchers.withId(R.id.reunionRecyclerView), isDisplayed())).check(withItemCount(ITEMS_COUNT));
        onView(allOf(ViewMatchers.withId(R.id.reunionRecyclerView), isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, new DeleteViewAction()));
        onView(allOf(ViewMatchers.withId(R.id.reunionRecyclerView), isDisplayed())).check(withItemCount(ITEMS_COUNT-1));
    }

    @Test
    public void myReunionList_addAction_shouldAddItem() {
        onView(allOf(ViewMatchers.withId(R.id.reunionRecyclerView), isDisplayed())).check(withItemCount(ITEMS_COUNT-1));
        ViewInteraction floatingActionButton = onView(
                Matchers.allOf(withId(R.id.floatingActionButton),
                        childAtPosition(
                                Matchers.allOf(withId(R.id.constraintLayoutMainActivity),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                Matchers.allOf(withId(R.id.subjectReunionText),
                        childAtPosition(
                                Matchers.allOf(withId(R.id.inputLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                4)),
                                3),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("test"), closeSoftKeyboard());

        ViewInteraction appCompatMultiAutoCompleteTextView = onView(
                Matchers.allOf(withId(R.id.multiSelectionParticipant),
                        isDisplayed()));
        appCompatMultiAutoCompleteTextView.perform(replaceText("julien@lamzone.com"), closeSoftKeyboard());


        ViewInteraction appCompatTextView = onView(
                Matchers.allOf(withId(R.id.saveButton), withText("Enregistrer"),
                        isDisplayed()));
        appCompatTextView.perform(click());

        onView(allOf(ViewMatchers.withId(R.id.reunionRecyclerView), isDisplayed())).check(withItemCount(ITEMS_COUNT));
    }

    @Test
    public void myReunionList_filterAction_shouldFilterList() {
        ViewInteraction actionMenuItemView = onView(
                Matchers.allOf(withId(R.id.menu_activity_main_filter), withContentDescription("filter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(androidx.appcompat.R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction materialTextView = onView(
                Matchers.allOf(withId(R.id.title), withText("Filtrer les réunions"),
                        childAtPosition(
                                childAtPosition(
                                        withId(androidx.appcompat.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction materialTextView2 = onView(
                Matchers.allOf(withId(R.id.btnSelectDate), withText("Sélectionner une date"), withContentDescription("Sélectionnez la date de la réunion"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.popupFilterLayout),
                                        0),
                                3),
                        isDisplayed()));
        materialTextView2.perform(click());

        ViewInteraction checkableImageButton = onView(
                Matchers.allOf(withId(com.google.android.material.R.id.mtrl_picker_header_toggle), withContentDescription("Switch to text input mode"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.fullscreen_header),
                                        2),
                                1),
                        isDisplayed()));
        checkableImageButton.perform(click());

        ViewInteraction textInputEditText = onView(
                childAtPosition(
                        childAtPosition(
                                withId(com.google.android.material.R.id.mtrl_picker_text_input_range_start),
                                0),
                        0));
        textInputEditText.perform(scrollTo(), replaceText("01/06/2024"), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                childAtPosition(
                        childAtPosition(
                                withId(com.google.android.material.R.id.mtrl_picker_text_input_range_end),
                                0),
                        0));
        textInputEditText2.perform(scrollTo(), replaceText("30/06/2024"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                Matchers.allOf(withId(com.google.android.material.R.id.confirm_button), withText("Save"), withContentDescription("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.fullscreen_header),
                                        2),
                                0),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatSpinner = onView(
                Matchers.allOf(withId(R.id.maxHourSpinner), withContentDescription("Sélectionnez l'heure maximale"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        6),
                                3),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(11);
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatMultiAutoCompleteTextView = onView(
                Matchers.allOf(withId(R.id.multiSelectRoom), withContentDescription("Salles disponibles"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.popupFilterLayout),
                                        0),
                                9),
                        isDisplayed()));
        appCompatMultiAutoCompleteTextView.perform(replaceText("salle A"), closeSoftKeyboard());

        ViewInteraction materialTextView4 = onView(
                Matchers.allOf(withId(R.id.buttonFilterAction), withText("Filtrer"), withContentDescription("Sauvegarder"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        10),
                                1),
                        isDisplayed()));
        materialTextView4.perform(click());

        onView(AllOf.allOf(ViewMatchers.withId(R.id.reunionRecyclerView), isDisplayed())).check(withItemCount(lessThan(ITEMS_COUNT)) );

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static Matcher<Integer> lessThan(final int value) {
        return new TypeSafeMatcher<Integer>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("a value less than " + value);
            }

            @Override
            protected boolean matchesSafely(Integer item) {
                return item < value;
            }
        };
    }
}