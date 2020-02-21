package alektas.pocketbasket.ui;

import android.view.View;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import alektas.pocketbasket.R;

import static alektas.pocketbasket.utils.ExtraViewMatchers.hasItemAtPosition;
import static alektas.pocketbasket.utils.ExtraViewMatchers.withoutItems;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // clear basket
        onView(withId(R.id.fab)).perform(longClick());
        onView(withId(R.id.fam_check_all)).perform(click());
        onView(withId(R.id.fam_del_all)).perform(click());
    }

    @Test
    public void launch_isMainActivity() {
        onView(withId(R.id.root_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void floatingMenuHidden_longClickOnFab_isFloatingMenuShown() {
        onView(withId(R.id.fab)).perform(longClick());
        onView(withId(R.id.fam_del_all)).check(matches(isDisplayed()));
        onView(withId(R.id.fam_check_all)).check(matches(isDisplayed()));
    }

    @Test
    public void floatingMenuShown_clickOnFab_isFloatingMenuHidden() {
        onView(withId(R.id.fab)).perform(longClick()); // show FAM

        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fam_del_all)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fam_check_all)).check(matches(not(isDisplayed())));
    }

    @Test
    public void uncheckedItemsInBasket_clickFamCheckBtn_allBasketItemsChecked() {
        onView(withId(R.id.showcase_list))
                .perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.showcase_list))
                .perform(actionOnItemAtPosition(1, click()));

        onView(withId(R.id.fab)).perform(longClick());
        onView(withId(R.id.fam_check_all)).perform(click());

        onView(withId(R.id.basket_list)).check(matches(hasCheckedItemAtPosition(0)));
        onView(withId(R.id.basket_list)).check(matches(hasCheckedItemAtPosition(1)));
    }

    @Test
    public void checkedItemsInBasket_clickFamDelBtn_allCheckedBasketItemsDeleted() {
        onView(withId(R.id.showcase_list))
                .perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.showcase_list))
                .perform(actionOnItemAtPosition(1, click()));

        onView(withId(R.id.basket_list))
                .perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.basket_list))
                .perform(actionOnItemAtPosition(1, click()));

        onView(withId(R.id.fab)).perform(longClick());
        onView(withId(R.id.fam_del_all)).perform(click());

        onView(withId(R.id.basket_list)).check(matches(withoutItems()));
    }

    private Matcher<View> hasCheckedItemAtPosition(int itemPosition) {
        return hasItemAtPosition(itemPosition,
                        hasDescendant(allOf(withId(R.id.check_image), isDisplayed())));
    }

}