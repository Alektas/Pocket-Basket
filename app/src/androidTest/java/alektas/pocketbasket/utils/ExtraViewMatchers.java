package alektas.pocketbasket.utils;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ExtraViewMatchers {

    public static Matcher<View> hasItemAtPosition(int position, Matcher<View> matcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                matcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView rv) {
                RecyclerView.ViewHolder vh = rv.findViewHolderForAdapterPosition(position);
                if (vh == null) return false;
                return matcher.matches(vh.itemView);
            }
        };
    }

    public static Matcher<View> withoutItems() {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("without items");
            }

            @Override
            protected boolean matchesSafely(RecyclerView rv) {
                RecyclerView.Adapter adapter = rv.getAdapter();
                if (adapter == null) return false;
                return adapter.getItemCount() == 0;
            }
        };
    }

    public static Matcher<View> dummy() {
        return new BaseMatcher<View>() {
            @Override
            public boolean matches(Object item) {
                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}
