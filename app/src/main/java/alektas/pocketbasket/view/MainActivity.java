package alektas.pocketbasket.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PocketBasketApp";
    private int mDisplayWidth;
    private TransitionSet mTransitionSet;
    private ViewGroup mBasket;
    private ViewGroup mShowcase;
    private RadioGroup mCategories;
    private EditText mNameField;
    private View mAddBtn;
    private ItemsViewModel mViewModel;
    private BasketAdapter mBasketAdapter;
    private ShowcaseAdapter mShowcaseAdapter;
    private LiveData<List<Item>> mShowcaseItems;

    private GestureDetector mGestureDetector;
    private ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        App.getComponent().inject(this);
        init();
    }

    class SlideListener extends GestureDetector.SimpleOnGestureListener {
        private static final double MIN_FLING_X = 150d;
        private static final double MAX_FLING_Y = 100d;
        private static final double LEFT_FLING_EDGE = 0.3d; // 1d = display width
        private static final double RIGHT_FLING_EDGE = 0.7d;

        private ViewGroup.LayoutParams categParams;
        private ViewGroup.LayoutParams showcaseParams;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                return false;
            }

            categParams = mCategories.getLayoutParams();
            showcaseParams = mShowcase.getLayoutParams();

            double dY = Math.abs(e2.getY() - e1.getY());
            double dX = Math.abs(e2.getX() - e1.getX());
            if (dY < MAX_FLING_Y && dX > MIN_FLING_X) {
                TransitionManager.beginDelayedTransition(mConstraintLayout, mTransitionSet);
                if (mViewModel.isShowcaseMode() &&
                        velocityX < 0 &&
                        e1.getX() > RIGHT_FLING_EDGE*mDisplayWidth) {
                    setBasketMode();
                }
                else if (!mViewModel.isShowcaseMode() &&
                        velocityX > 0 &&
                        e1.getX() < LEFT_FLING_EDGE*mDisplayWidth){
                    setShowcaseMode();
                }
                mCategories.setLayoutParams(categParams);
                mShowcase.setLayoutParams(showcaseParams);
                return true;
            }
            return false;
        }

        private void setBasketMode() {
            categParams.width = getResources()
                    .getDimensionPixelSize(R.dimen.categ_basket_mode_size);
            showcaseParams.width = getResources()
                    .getDimensionPixelSize(R.dimen.showcase_basket_mode_size);
            resizeRadioText(mCategories, 0f);
            mNameField.setVisibility(View.VISIBLE);
            mAddBtn.setVisibility(View.VISIBLE);
            mViewModel.setShowcaseMode(false);
        }

        private void setShowcaseMode() {
            categParams.width = getResources()
                    .getDimensionPixelSize(R.dimen.categ_sc_mode_size);
            showcaseParams.width = getResources()
                    .getDimensionPixelSize(R.dimen.showcase_sc_mode_size);
            resizeRadioText(mCategories, 14f);
            mNameField.setVisibility(View.GONE);
            mAddBtn.setVisibility(View.GONE);
            mViewModel.setShowcaseMode(true);
        }
    }

    private void init() {
        initDisplayWidth();
        initAnimTransition();

        mConstraintLayout = findViewById(R.id.root_layout);
        mGestureDetector =
                new GestureDetector(this, new SlideListener());

        mBasket = findViewById(R.id.basket_list);
        mShowcase = findViewById(R.id.showcase_list);
        mCategories = findViewById(R.id.categ_group);

        mNameField = findViewById(R.id.add_item_field);
        mAddBtn = findViewById(R.id.add_item_btn);


        mViewModel = ViewModelProviders.of(this).get(ItemsViewModel.class);

        mBasketAdapter = new BasketAdapter(this, mViewModel);
        ((ListView) mBasket).setAdapter(mBasketAdapter);
        mShowcaseAdapter = new ShowcaseAdapter(this, mViewModel);
        ((ListView) mShowcase).setAdapter(mShowcaseAdapter);

        mViewModel.getBasketData().observe(this,
                mBasketAdapter::setItems);
        mShowcaseItems = mViewModel.getShowcaseData();
        mShowcaseItems.observe(this,
                mShowcaseAdapter::setItems);

    }

    private void initDisplayWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mDisplayWidth = metrics.widthPixels;
    }

    private void initAnimTransition() {
        mTransitionSet = new TransitionSet();
        mTransitionSet.addTransition(new ChangeBounds());
        mTransitionSet.setInterpolator(new DecelerateInterpolator());
        mTransitionSet.setDuration(200);
    }

    private void resizeRadioText(RadioGroup group, float textSize) {
        for (int i = 0; i < group.getChildCount(); i++) {
            ((RadioButton) group.getChildAt(i)).setTextSize(textSize);
        }
    }

    private void setFilter(int tag) {
        mShowcaseItems.removeObservers(this);
        mShowcaseItems = mViewModel.getByTag(tag);
        mShowcaseItems.observe(this, mShowcaseAdapter::setItems);
    }

    public void onAddBtnClick(View view) {
        String itemName = mNameField.getText().toString();
        mNameField.setText("");
        if (mViewModel.getBasketItem(itemName) == null) {
            Item item = new Item(itemName);
            item.setInBasket(true);
            mViewModel.insertItem(item);
        }
    }

    public void onClearBtnClick(View view) {
        mViewModel.clearBasket();
    }

    public void onFilterClick(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.all_rb:
                if (checked)
                    setFilter(0);
                break;
            case R.id.drink_rb:
                if (checked)
                    setFilter(R.string.drink);
                break;
            case R.id.fruits_rb:
                if (checked)
                    setFilter(R.string.fruit);
                break;
            case R.id.veg_rb:
                if (checked)
                    setFilter(R.string.vegetable);
                break;
            case R.id.groats_rb:
                if (checked)
                    setFilter(R.string.groats);
                break;
            case R.id.milky_rb:
                if (checked)
                    setFilter(R.string.milky);
                break;
            case R.id.floury_rb:
                if (checked)
                    setFilter(R.string.floury);
                break;
            case R.id.sweets_rb:
                if (checked)
                    setFilter(R.string.sweets);
                break;
            case R.id.meat_rb:
                if (checked)
                    setFilter(R.string.meat);
                break;
            case R.id.seafood_rb:
                if (checked)
                    setFilter(R.string.seafood);
                break;
            case R.id.semis_rb:
                if (checked)
                    setFilter(R.string.semis);
                break;
            case R.id.sauce_n_oil_rb:
                if (checked)
                    setFilter(R.string.sauce_n_oil);
                break;
            case R.id.household_rb:
                if (checked)
                    setFilter(R.string.household);
                break;
            case R.id.other_rb:
                if (checked)
                    setFilter(R.string.other);
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
