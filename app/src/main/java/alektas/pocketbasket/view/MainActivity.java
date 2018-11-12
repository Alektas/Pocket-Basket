package alektas.pocketbasket.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.widget.SearchView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.viewmodel.ItemsViewModel;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity
        implements ResetDialog.ResetDialogListener,
        AddItemDialog.AddItemDialogListener,
        DeleteModeListener {

    private static final String TAG = "PocketBasketApp";

    private int mDisplayWidth;
    private float mCategNarrowWidth;
    private float mShowcaseWideWidth;
    private float mShowcaseNarrowWidth;
    private float mBasketNarrowWidth;
    private boolean isMenuShown;

    private RecyclerView mBasket;
    private RecyclerView mShowcase;
    private ViewGroup mDelModePanel;
    private RadioGroup mCategories;
    private FloatingActionButton mAddBtn;
    private SearchView mSearchView;
    private View mDelAllBtn;
    private View mCheckAllBtn;
    private View mCancelDmBtn;
    private BasketRvAdapter mBasketAdapter;
    private ShowcaseRvAdapter mShowcaseAdapter;
    private Transition mTransitionSet;
    private GestureDetector mGestureDetector;
    private ConstraintLayout mConstraintLayout;

    private ItemsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        App.getComponent().inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        handleSearch(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchView.setQuery("", false);
        View root = findViewById(R.id.root_layout);
        root.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.reset_btn == item.getItemId()) {
            DialogFragment dialog = new ResetDialog();
            dialog.show(getSupportFragmentManager(), "ResetDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleSearch(intent);
    }

    private void handleSearch(Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            addItem(query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String itemName = intent.getDataString();
            addItem(itemName);
        }
    }

    private void addItem(String query) {
        mViewModel.addItem(query, R.string.other);
    }

    /* Init methods */

    private void init() {
        initDisplayWidth();
        initAnimTransition();
        initDimensions();
        initSearch();

        mConstraintLayout = findViewById(R.id.root_layout);
        mGestureDetector =
                new GestureDetector(this, new SlideListener());

        mBasket = findViewById(R.id.basket_list);
        mBasket.setLayoutManager(new LinearLayoutManager(this));

        mShowcase = findViewById(R.id.showcase_list);
        mShowcase.setLayoutManager(new LinearLayoutManager(this));

        mCategories = findViewById(R.id.categ_group);

        mDelModePanel = findViewById(R.id.del_mode_panel);
        mCancelDmBtn = findViewById(R.id.cancel_dm_btn);

        mAddBtn = findViewById(R.id.add_item_btn);
        mCheckAllBtn = findViewById(R.id.check_all_btn);
        mAddBtn.setOnLongClickListener(view -> {
            if (!isMenuShown) showMenu();
            return true;
        });

        mDelAllBtn = findViewById(R.id.del_all_btn);

        mViewModel = ViewModelProviders.of(this).get(ItemsViewModel.class);

        mBasketAdapter = new BasketRvAdapter(this, mViewModel);
        mBasket.setAdapter(mBasketAdapter);

        mShowcaseAdapter = new ShowcaseRvAdapter(this, this, mViewModel);
        mShowcase.setAdapter(mShowcaseAdapter);

        mViewModel.getBasketData().observe(this,
                mBasketAdapter::setItems);
        mViewModel.getShowcaseData().observe(this,
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
        mTransitionSet = TransitionInflater.from(this)
                .inflateTransition(R.transition.change_mode_transition);
    }

    private void initDimensions() {
        mCategNarrowWidth = getResources().getDimension(R.dimen.categ_narrow_size);
        mShowcaseNarrowWidth = getResources().getDimension(R.dimen.showcase_narrow_size);
        mShowcaseWideWidth = getResources().getDimension(R.dimen.showcase_wide_size);
        mBasketNarrowWidth = getResources().getDimension(R.dimen.basket_narrow_size);
    }

    private void initSearch() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = findViewById(R.id.menu_search);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
    }

    /* Layout changes methods */

    private void setLandscapeLayout() {
        changeLayoutState(WRAP_CONTENT,
                mShowcaseWideWidth,
                0);
        resizeRadioText(mCategories, 14f);
        ((View)mAddBtn).setVisibility(View.VISIBLE);
        mCancelDmBtn.setVisibility(View.VISIBLE);

        mViewModel.setBasketNamesShow(true);
        mViewModel.setShowcaseNamesShow(true);
    }

    private void setBasketMode() {
        changeLayoutState(mCategNarrowWidth,
                mShowcaseNarrowWidth,
                0);
        resizeRadioText(mCategories, 0f);

        ((View)mAddBtn).setVisibility(View.VISIBLE);
        if (isMenuShown) { hideFloatingMenu(); }
        mCancelDmBtn.setVisibility(View.GONE);

        mViewModel.setBasketNamesShow(true);
        mViewModel.setShowcaseNamesShow(false);

        mViewModel.setShowcaseMode(false);
        mBasketAdapter.notifyDataSetChanged();
        mShowcaseAdapter.notifyDataSetChanged();
    }

    private void setShowcaseMode() {
        changeLayoutState(WRAP_CONTENT,
                0,
                mBasketNarrowWidth);
        resizeRadioText(mCategories, 14f);

        ((View)mAddBtn).setVisibility(View.GONE);
        if (isMenuShown) { hideFloatingMenu(); }
        mCancelDmBtn.setVisibility(View.VISIBLE);

        mViewModel.setBasketNamesShow(false);
        mViewModel.setShowcaseNamesShow(true);

        mViewModel.setShowcaseMode(true);
        mBasketAdapter.notifyDataSetChanged();
        mShowcaseAdapter.notifyDataSetChanged();
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

    private void showMenu() {
        mAddBtn.setImageResource(R.drawable.ic_close_white_24dp);

        runVisibilityAnim(mCheckAllBtn, 0, R.animator.check_all_show_anim);
        runVisibilityAnim(mDelAllBtn, 0, R.animator.delete_all_show_anim);

        isMenuShown = true;
    }

    private void hideFloatingMenu() {
        mAddBtn.setImageResource(R.drawable.ic_edit_24dp);

        runVisibilityAnim(mCheckAllBtn, View.INVISIBLE, R.animator.check_all_hide_anim);
        runVisibilityAnim(mDelAllBtn, View.INVISIBLE, R.animator.delete_all_hide_anim);

        isMenuShown = false;
    }

    private void runVisibilityAnim(View view, int endVis, int animId) {
        Animator anim = AnimatorInflater.loadAnimator(this, animId);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(endVis);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);
            }
        });
        anim.setTarget(view);
        anim.start();
    }

    private void setFilter(int tag) {
        mViewModel.setFilter(tag);
    }

    /* Interfaces methods */

    @Override
    public void onDialogAcceptReset(boolean fullReset) {
        mViewModel.resetShowcase(fullReset);
    }

    @Override
    public void onDialogAddItem(String itemName, int tagRes) {
        mViewModel.addItem(itemName, tagRes);
    }

    @Override
    public void onDelModeEnable() {
        TransitionManager.beginDelayedTransition(mConstraintLayout, mTransitionSet);
        mDelModePanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDelModeDisable() {
        TransitionManager.beginDelayedTransition(mConstraintLayout, mTransitionSet);
        mDelModePanel.setVisibility(View.GONE);
    }

    /* On buttons click methods */

    public void onBtnClick(View view) {
        if (isMenuShown) {
            if (view.getId() == R.id.add_item_btn) {
                hideFloatingMenu();
            }
            if (view.getId() == R.id.del_all_btn) {
                mViewModel.clearBasket();
                hideFloatingMenu();
            }
            if (view.getId() == R.id.check_all_btn) {
                mViewModel.checkAll();
            }
        }
        else if (view.getId() == R.id.add_item_btn){
            if (mSearchView.hasFocus()) {
                if (!TextUtils.isEmpty(mSearchView.getQuery())) {
                    addItem(mSearchView.getQuery().toString());
                }
                cancelSearch();
            } else {
                mSearchView.setIconified(false);
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

    public void onDelDmBtnClick(View view) {
        mShowcaseAdapter.deleteChoosedItems();
    }

    public void onCancelDmBtnClick(View view) {
        mShowcaseAdapter.cancelDel();
    }

    /* Touch events */

    class SlideListener extends GestureDetector.SimpleOnGestureListener {
        private final double MIN_FLING_X;
        private final double MAX_FLING_Y;
        private final double LEFT_FLING_EDGE;
        private final double RIGHT_FLING_EDGE;

        SlideListener() {
            LEFT_FLING_EDGE = getResources().getDimension(R.dimen.change_mode_left_edge);
            RIGHT_FLING_EDGE = getResources().getDimension(R.dimen.change_mode_right_edge);
            MIN_FLING_X = getResources().getDimension(R.dimen.fling_X_min);
            MAX_FLING_Y = getResources().getDimension(R.dimen.fling_Y_max);
        }

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
                        e1.getX() > mDisplayWidth - RIGHT_FLING_EDGE) {
                    setBasketMode();
                }
                else if (!mViewModel.isShowcaseMode() &&
                        velocityX > 0 &&
                        e1.getX() < LEFT_FLING_EDGE){
                    setShowcaseMode();
                }
                return true;
            }
            return false;
        }
    }

    /* If fling occurred, don't dispatch touch event further
     * to avoid conflict with scrolling in recyclerviews */
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (mGestureDetector.onTouchEvent(event)) return true;
//        else return super.dispatchTouchEvent(event);
//    }

    /* !!! Cause conflict with scrolling in recyclerviews */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    /* Private methods */

    private void cancelSearch() {
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
    }
}
