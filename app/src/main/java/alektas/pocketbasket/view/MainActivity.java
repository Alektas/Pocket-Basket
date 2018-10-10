package alektas.pocketbasket.view;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.res.Configuration;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PocketBasketApp";
    private static final int ADD_ITEM_REQUEST = 101;
    private int mDisplayWidth;
    private float mCategNarrowWidth;
    private float mShowcaseWideWidth;
    private float mShowcaseNarrowWidth;
    private float mBasketNarrowWidth;

    private ViewGroup mBasket;
    private ViewGroup mShowcase;
    private ViewGroup mDelModePanel;
    private RadioGroup mCategories;
    private View mAddBtn;
    private View mCancelDmBtn;
    private BasketAdapter mBasketAdapter;
    private ShowcaseAdapter mShowcaseAdapter;
    private TransitionSet mTransitionSet;
    private GestureDetector mGestureDetector;
    private ConstraintLayout mConstraintLayout;

    private ItemsViewModel mViewModel;
    private LiveData<List<Item>> mShowcaseItems;

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

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                setLandscapeLayout();
                return false;
            }

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
                return true;
            }
            return false;
        }
    }

    private void init() {
        initDisplayWidth();
        initAnimTransition();
        initDimensions();

        mConstraintLayout = findViewById(R.id.root_layout);
        mGestureDetector =
                new GestureDetector(this, new SlideListener());

        mBasket = findViewById(R.id.basket_list);
        mShowcase = findViewById(R.id.showcase_list);
        mCategories = findViewById(R.id.categ_group);

        mDelModePanel = findViewById(R.id.del_mode_panel);
        mCancelDmBtn = findViewById(R.id.cancel_dm_btn);

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

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            setLandscapeLayout();
        } else {
            setShowcaseMode();
        }
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
        mTransitionSet.addTransition(new Fade());
        mTransitionSet.setInterpolator(new DecelerateInterpolator());
        mTransitionSet.setDuration(200);
    }

    private void initDimensions() {
        mCategNarrowWidth = getResources().getDimension(R.dimen.categ_narrow_size);
        mShowcaseNarrowWidth = getResources().getDimension(R.dimen.showcase_narrow_size);
        mShowcaseWideWidth = getResources().getDimension(R.dimen.showcase_wide_size);
        mBasketNarrowWidth = getResources().getDimension(R.dimen.basket_narrow_size);
    }

    private void setLandscapeLayout() {
        changeLayoutState(WRAP_CONTENT,
                mShowcaseWideWidth,
                0);
        resizeRadioText(mCategories, 14f);
        mAddBtn.setVisibility(View.VISIBLE);
        mCancelDmBtn.setVisibility(View.VISIBLE);

        mViewModel.setBasketNamesShow(true);
        mViewModel.setShowcaseNamesShow(true);
    }

    private void setBasketMode() {
        changeLayoutState(mCategNarrowWidth,
                mShowcaseNarrowWidth,
                0);
        resizeRadioText(mCategories, 0f);

        mAddBtn.setVisibility(View.VISIBLE);
        mCancelDmBtn.setVisibility(View.GONE);

        mViewModel.setBasketNamesShow(true);
        mViewModel.setShowcaseNamesShow(false);

        mViewModel.setShowcaseMode(false);
    }

    private void setShowcaseMode() {
        changeLayoutState(WRAP_CONTENT,
                0,
                mBasketNarrowWidth);
        resizeRadioText(mCategories, 14f);

        mAddBtn.setVisibility(View.GONE);
        mCancelDmBtn.setVisibility(View.VISIBLE);

        mViewModel.setBasketNamesShow(false);
        mViewModel.setShowcaseNamesShow(true);

        mViewModel.setShowcaseMode(true);
    }

    private void changeLayoutState(float categWidth, float showcaseWidth, float basketWidth) {
        ViewGroup.LayoutParams categParams = mCategories.getLayoutParams();
        ViewGroup.LayoutParams showcaseParams = mShowcase.getLayoutParams();
        ViewGroup.LayoutParams basketParams = mBasket.getLayoutParams();

        categParams.width = (int) categWidth;
        showcaseParams.width = (int) showcaseWidth;
        basketParams.width = (int) basketWidth;

        mCategories.setLayoutParams(categParams);
        mShowcase.setLayoutParams(showcaseParams);
        mBasket.setLayoutParams(basketParams);

        if (mViewModel.isDelMode()) {
            mDelModePanel.setVisibility(View.VISIBLE);
        }
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

    public void onAddItemBtnClick(View view) {
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivityForResult(intent, ADD_ITEM_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ITEM_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                String itemName = data.getStringExtra(AddItemActivity.ITEM_NAME);
                if (mViewModel.getBasketItem(itemName) == null) {
                    Item item = new Item(itemName);
                    item.setInBasket(true);
                    item.setImgRes(data.getIntExtra(AddItemActivity.ITEM_IMG_RES, 0));
                    item.setTagRes(data.getIntExtra(AddItemActivity.ITEM_CATEGORY_RES, 0));
                    mViewModel.insertItem(item);
                }
            }
        }
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

    public void onDelModeEnable() {
        TransitionManager.beginDelayedTransition(mConstraintLayout, mTransitionSet);
        mDelModePanel.setVisibility(View.VISIBLE);
    }

    public void onDelModeDisable() {
        TransitionManager.beginDelayedTransition(mConstraintLayout, mTransitionSet);
        mDelModePanel.setVisibility(View.GONE);
    }

    public void onDelDmBtnClick(View view) {
        mShowcaseAdapter.deleteItems();
    }

    public void onCancelDmBtnClick(View view) {
        mShowcaseAdapter.cancelDel();
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
