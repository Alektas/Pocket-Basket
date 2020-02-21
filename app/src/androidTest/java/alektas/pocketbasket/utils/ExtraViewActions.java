package alektas.pocketbasket.utils;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class ExtraViewActions {

    public static ViewAction clickChild(@IdRes int childId) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on the child view";
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.findViewById(childId).performClick();
            }
        };
    }
}
