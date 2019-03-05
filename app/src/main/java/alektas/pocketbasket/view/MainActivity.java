package alektas.pocketbasket.view;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.widget.SearchView;

import alektas.pocketbasket.Utils;
import alektas.pocketbasket.async.getAllAsync;
import alektas.pocketbasket.db.AppDatabase;
import alektas.pocketbasket.db.dao.ItemsDao;
import alektas.pocketbasket.guide.Guide;
import alektas.pocketbasket.guide.GuideCase;
import alektas.pocketbasket.guide.GuideHelperImpl;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.viewmodel.ItemsViewModel;
import alektas.pocketbasket.db.entity.Item;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity
        implements ResetDialog.ResetDialogListener,
        DeleteModeListener,
        OnStartDragListener,
        ItemSizeProvider {

    private static final String TAG = "PocketBasketApp";

    private int mCategNarrowWidth;
    private int mCategWideWidth;
    private int mShowcaseWideWidth;
    private int mShowcaseNarrowWidth;
    private int mBasketNarrowWidth;
    private int mBasketWideWidth;
    private int basketTextMarginEnd;
    private int initX;
    private int initY;
    private int movX;
    private float changeModeDistance;
    private boolean isMenuShown;
    private boolean allowChangeMode = true;
    private boolean alreadySetChangeModeAllowing = false;
    private boolean allowChooseCategory = true;

    private RecyclerView mBasket;
    private RecyclerView mShowcase;
    private ViewGroup mDelModePanel;
    private View mCategoriesWrapper;
    private FloatingActionButton mAddBtn;
    private SearchView mSearchView;
    private View mDelAllBtn;
    private View mCheckAllBtn;
    private View mCancelDmBtn;
    private View mSkipGuideBtn;
    private BasketRvAdapter mBasketAdapter;
    private ShowcaseRvAdapter mShowcaseAdapter;
    private Transition mTransitionSet;
    private ConstraintLayout mConstraintLayout;
    private ShareActionProvider mShareActionProvider;
    private ItemTouchHelper mTouchHelper;
    private ItemsViewModel mViewModel;
    private GuideHelperImpl mGuideHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        App.getComponent().inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        handleSearch(getIntent());

        if (mViewModel.isGuideStarted()) mViewModel.finishGuide();

        SharedPreferences prefs =
                getSharedPreferences(getString(R.string.PREFERENCES_FILE_KEY), MODE_PRIVATE);
        if (prefs.getBoolean(getString(R.string.FIRST_START_KEY), true)) {
            prefs.edit().putBoolean(getString(R.string.FIRST_START_KEY), false).apply();
            mViewModel.putToBasket(getString(R.string.cabbage));
            startGuide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchView.setQuery("", false);
        View root = findViewById(R.id.root_layout);
        root.requestFocus();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        // Locate MenuItem with ShareActionProvider
        MenuItem shareItem = menu.findItem(R.id.menu_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setOnShareTargetSelectedListener((source, intent) -> {
            String sharedItems = intent.getStringExtra(Intent.EXTRA_TEXT);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT, sharedItems);
            App.getAnalytics().logEvent(FirebaseAnalytics.Event.SHARE, bundle);
            return false;
        });
        updateShareIntent(mViewModel.getBasketData().getValue());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_reset: {
                DialogFragment dialog = new ResetDialog();
                dialog.show(getSupportFragmentManager(), "ResetDialog");
                return true;
            }

            case R.id.menu_load_new_ver: {
                loadNewVersion();

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "check for new app version");
                App.getAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                return true;
            }

            case R.id.menu_guide: {
                startGuide();
                return true;
            }

            case R.id.menu_about: {
                DialogFragment dialog = new AboutDialog();
                dialog.show(getSupportFragmentManager(), "AboutDialog");

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "read about app");
                App.getAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleSearch(intent);
    }

    /* Init methods */

    private void init() {
        initAnimTransition();
        initSearch();
        initFloatingActionMenu();

        mConstraintLayout = findViewById(R.id.root_layout);

        mCategoriesWrapper = findViewById(R.id.categories_wrapper);

        initDimensions();

        mDelModePanel = findViewById(R.id.del_mode_panel);
        mCancelDmBtn = findViewById(R.id.cancel_dm_btn);

        mViewModel = ViewModelProviders.of(this).get(ItemsViewModel.class);

        initGuide(mViewModel);
        initBasket(mViewModel);
        initShowcase(mViewModel);

        mViewModel.getBasketData().observe(this, (items -> {
            mBasketAdapter.setItems(items);
            updateShareIntent(items);
        }));
        mViewModel.getShowcaseData().observe(this,
                mShowcaseAdapter::setItems);

        if (isLandscape()) {
            setLandscapeLayout();
        } else {
            setShowcaseMode();
        }
    }

    private void initAnimTransition() {
        mTransitionSet = TransitionInflater.from(this)
                .inflateTransition(R.transition.change_mode_transition);
    }

    private void initDimensions() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
        int minDisplaySize = Math.min(screenHeight, screenWidth);
        mCategNarrowWidth = (int) getResources().getDimension(R.dimen.categ_narrow_size);
        mShowcaseNarrowWidth = (int) getResources().getDimension(R.dimen.showcase_narrow_size);
        mBasketNarrowWidth = (int) getResources().getDimension(R.dimen.basket_narrow_size);
        mCategWideWidth = (int) getResources().getDimension(R.dimen.categ_wide_size);
        mShowcaseWideWidth = minDisplaySize - mCategWideWidth - mBasketNarrowWidth;
        if (isLandscape()) {
            mBasketWideWidth = screenWidth - mCategWideWidth - mShowcaseWideWidth;
        } else {
            mBasketWideWidth = screenWidth - mCategNarrowWidth - mShowcaseNarrowWidth;
        }

        changeModeDistance = getResources().getDimension(R.dimen.change_mode_distance);
        basketTextMarginEnd = (int) getResources().getDimension(R.dimen.basket_item_text_margin_end);
    }

    private void initSearch() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = findViewById(R.id.menu_search);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
    }

    private void initFloatingActionMenu() {
        mAddBtn = findViewById(R.id.fab);
        mCheckAllBtn = findViewById(R.id.check_all_btn);
        mDelAllBtn = findViewById(R.id.del_all_btn);
        mAddBtn.setOnLongClickListener(view -> {
            if (!isMenuShown) {
                showFloatingMenu();
            }
            return true;
        });
    }

    private void initBasket(ItemsViewModel model) {
        mBasket = findViewById(R.id.basket_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mBasket.setLayoutManager(layoutManager);
        mBasketAdapter = new BasketRvAdapter(this, model,
                this,this);
        mBasket.setAdapter(mBasketAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchCallback(mBasketAdapter);
        mTouchHelper = new ItemTouchHelper(callback);
        mTouchHelper.attachToRecyclerView(mBasket);

        mBasket.addOnItemTouchListener(new ItemTouchListener());
    }

    private void initShowcase(ItemsViewModel model) {
        mShowcase = findViewById(R.id.showcase_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mShowcase.setLayoutManager(layoutManager);
        mShowcaseAdapter = new ShowcaseRvAdapter(this, this,
                this, model);
        mShowcase.setAdapter(mShowcaseAdapter);

        mShowcase.addOnItemTouchListener(new ItemTouchListener());
    }

    private void initGuide(ItemsViewModel model) {
        mSkipGuideBtn = findViewById(R.id.skip_guide_btn);
        View bgTop = findViewById(R.id.guide_bg_top_img);
        View bgBottom = findViewById(R.id.guide_bg_bottom_img);

        // Guide: categories help
        GuideCase categoriesHelpCase = new GuideCase(
                GuideHelperImpl.GUIDE_CATEGORIES_HELP,
                bgBottom,
                findViewById(R.id.guide_bg_right_large_img),
                findViewById(R.id.guide_categories_help_text),
                findViewById(R.id.guide_categories_help_sub_text));

        // Guide: showcase help
        GuideCase showcaseHelpCase = new GuideCase(
                GuideHelperImpl.GUIDE_SHOWCASE_HELP,
                bgBottom,
                findViewById(R.id.guide_bg_right_small_img),
                findViewById(R.id.guide_bg_left_small_img),
                findViewById(R.id.guide_showcase_help_text),
                findViewById(R.id.guide_showcase_help_sub_text));

        // Guide: basket help
        GuideCase basketHelpCase = new GuideCase(
                GuideHelperImpl.GUIDE_BASKET_HELP,
                bgBottom,
                findViewById(R.id.guide_bg_left_large_img),
                findViewById(R.id.guide_basket_help_text),
                findViewById(R.id.guide_basket_help_sub_text));

        //  Guide: change mode
        View changeModeImg = findViewById(R.id.guide_scroll_hor_img);
        AnimatorSet scrollHorizAnim = (AnimatorSet) AnimatorInflater
                .loadAnimator(this, R.animator.scroll_horiz);
        scrollHorizAnim.setTarget(changeModeImg);
        GuideCase changeModeCase = new GuideCase(
                GuideHelperImpl.GUIDE_CHANGE_MODE,
                scrollHorizAnim,
                changeModeImg,
                bgBottom,
                findViewById(R.id.guide_change_mode_text),
                findViewById(R.id.guide_change_mode_sub_text));

        //  Guide: add item
        View addItemImg = findViewById(R.id.guide_tap_add_img);
        AnimatorSet tapAnim = (AnimatorSet) AnimatorInflater
                .loadAnimator(this, R.animator.tap);
        GuideCase addItemCase = new GuideCase(
                GuideHelperImpl.GUIDE_ADD_ITEM,
                tapAnim,
                true,
                addItemImg,
                bgBottom,
                findViewById(R.id.guide_add_item_text),
                findViewById(R.id.guide_add_item_sub_text));

        //  Guide: check item in Basket
        View checkItemImg = findViewById(R.id.guide_tap_check_img);
        GuideCase checkItemCase = new GuideCase(
                GuideHelperImpl.GUIDE_CHECK_ITEM,
                tapAnim,
                true,
                checkItemImg,
                bgBottom,
                findViewById(R.id.guide_check_item_text));

        //  Guide: move item in Basket
        AnimatorSet scrollVertAnim = (AnimatorSet) AnimatorInflater
                .loadAnimator(this, R.animator.scroll_vert);
        View moveItemImg = findViewById(R.id.guide_scroll_vert_img);
        scrollVertAnim.setTarget(moveItemImg);
        GuideCase moveItemCase = new GuideCase(
                GuideHelperImpl.GUIDE_MOVE_ITEM,
                scrollVertAnim,
                moveItemImg,
                bgBottom,
                findViewById(R.id.guide_move_item_text),
                findViewById(R.id.guide_move_item_sub_text));

        //  Guide: remove item from Basket
        View removeItemImg = findViewById(R.id.guide_swipe_right_img);
        AnimatorSet swipeRightAnim = (AnimatorSet) AnimatorInflater
                .loadAnimator(this, R.animator.swipe_right);
        swipeRightAnim.setTarget(removeItemImg);
        GuideCase removeItemCase = new GuideCase(
                GuideHelperImpl.GUIDE_REMOVE_ITEM,
                swipeRightAnim,
                removeItemImg,
                bgBottom,
                findViewById(R.id.guide_remove_text),
                findViewById(R.id.guide_remove_sub_text));

        //  Guide: turn on delete mode
        View longPressImg = findViewById(R.id.guide_del_mode_img);
        AnimatorSet longPressAnim = (AnimatorSet) AnimatorInflater
                .loadAnimator(this, R.animator.long_press);
        GuideCase delModeCase = new GuideCase(
                GuideHelperImpl.GUIDE_DEL_MODE,
                longPressAnim,
                true,
                longPressImg,
                bgBottom,
                findViewById(R.id.guide_del_mode_text));

        //  Guide: delete items from Showcase
        View tapToDelImg = findViewById(R.id.guide_tap_delete_img);
        GuideCase deleteItemsCase = new GuideCase(
                GuideHelperImpl.GUIDE_DEL_ITEMS,
                tapAnim,
                true,
                tapToDelImg,
                bgTop,
                findViewById(R.id.guide_delete_text),
                findViewById(R.id.guide_delete_sub_text));

        //  Guide: floating menu invoke
        View pressFabImg = findViewById(R.id.guide_show_floating_menu_img);
        GuideCase floatingMenuCase = new GuideCase(
                GuideHelperImpl.GUIDE_FLOATING_MENU,
                longPressAnim,
                true,
                pressFabImg,
                bgTop,
                findViewById(R.id.guide_show_floating_menu_text));

        //  Guide: floating menu help
        GuideCase floatingMenuHelpCase = new GuideCase(
                GuideHelperImpl.GUIDE_FLOATING_MENU_HELP,
                findViewById(R.id.guide_bg_full_img),
                findViewById(R.id.guide_floating_menu_close_text),
                findViewById(R.id.guide_floating_menu_check_all_text),
                findViewById(R.id.guide_floating_menu_del_checked_text));

        //  Guide: finish case
        View finishImg = findViewById(R.id.guide_finish_img);
        AnimatorSet finishAnim = (AnimatorSet) AnimatorInflater
                .loadAnimator(this, R.animator.finish_guide);
        finishAnim.setTarget(finishImg);
        GuideCase finishCase = new GuideCase(
                GuideHelperImpl.GUIDE_FINISH,
                finishAnim,
                finishImg);
        finishCase.setAutoNext(true);

        Guide guide = new Guide();
        guide.addCase(categoriesHelpCase)
                .addCase(showcaseHelpCase)
                .addCase(basketHelpCase)
                .addCase(changeModeCase)
                .addCase(addItemCase)
                .addCase(checkItemCase)
                .addCase(moveItemCase)
                .addCase(removeItemCase)
                .addCase(delModeCase)
                .addCase(deleteItemsCase)
                .addCase(floatingMenuCase)
                .addCase(floatingMenuHelpCase)
                .addCase(finishCase);

        guide.setGuideListener(new Guide.GuideListener() {
            @Override
            public void onGuideFinish() {
                mSkipGuideBtn.setVisibility(View.INVISIBLE);
                model.setGuideStarted(false);
            }

            @Override
            public void onGuideCaseStart(String caseKey) {
                if (isLandscape() && GuideHelperImpl.GUIDE_CHANGE_MODE.equals(caseKey))
                    mGuideHelper.nextCase();
            }
        });

        mGuideHelper = new GuideHelperImpl(guide);

        model.setGuide(mGuideHelper);
    }

    /* Layout changes methods */

    @SuppressLint("RestrictedApi")
    private void setLandscapeLayout() {
        changeLayoutSize(mCategWideWidth,
                WRAP_CONTENT,
                0);

        mAddBtn.setVisibility(View.VISIBLE);
        mCancelDmBtn.setVisibility(View.VISIBLE);
    }

    // Set basket or showcase mode in depends of touch moving distance (movX)
    private void setMode(int movX) {
        if (mViewModel.isShowcaseMode()) {
            if (movX < -changeModeDistance) {
                setBasketMode();
            } else {
                TransitionManager.beginDelayedTransition(mConstraintLayout, mTransitionSet);
                changeLayoutSize(mCategWideWidth,
                        0,
                        mBasketNarrowWidth);
            }
        } else {
            if (movX > changeModeDistance) {
                setShowcaseMode();
            } else {
                TransitionManager.beginDelayedTransition(mConstraintLayout, mTransitionSet);
                changeLayoutSize(mCategNarrowWidth,
                        mShowcaseNarrowWidth,
                        0);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void setBasketMode() {
        changeLayoutSize(mCategNarrowWidth,
                mShowcaseNarrowWidth,
                0);

        mAddBtn.setVisibility(View.VISIBLE);
        if (isMenuShown) { hideFloatingMenu(); }
        mCancelDmBtn.setVisibility(View.GONE);

        mViewModel.setShowcaseMode(false);
    }

    @SuppressLint("RestrictedApi")
    private void setShowcaseMode() {
        changeLayoutSize(mCategWideWidth,
                0,
                mBasketNarrowWidth);

        mAddBtn.setVisibility(View.GONE);
        if (isMenuShown) { hideFloatingMenu(); }
        mCancelDmBtn.setVisibility(View.VISIBLE);

        mViewModel.setShowcaseMode(true);
    }

    // Set layouts' size in depends of touch moving distance (movX)
    private void changeLayoutSizeByTouch(int movX) {
        int showcaseWidth;
        int categWidth;

        if (mViewModel.isShowcaseMode()) {
            showcaseWidth = calculateLayoutSize(
                    mShowcaseWideWidth - mShowcaseNarrowWidth + movX/2,
                    mShowcaseNarrowWidth,
                    mShowcaseWideWidth);
            categWidth = calculateLayoutSize(
                    mCategWideWidth - mCategNarrowWidth + movX/2,
                    mCategNarrowWidth,
                    mCategWideWidth);
        } else {
            showcaseWidth = calculateLayoutSize(
                    movX/2,
                    mShowcaseNarrowWidth,
                    mShowcaseWideWidth);
            categWidth = calculateLayoutSize(
                    movX/2,
                    mCategNarrowWidth,
                    mCategWideWidth);
        }

        changeLayoutSize(categWidth, showcaseWidth, 0);
    }

    private void changeLayoutSize(int categWidth, int showcaseWidth, int basketWidth) {
        ViewGroup.LayoutParams categWrapParams = mCategoriesWrapper.getLayoutParams();
        ViewGroup.LayoutParams showcaseParams = mShowcase.getLayoutParams();
        ViewGroup.LayoutParams basketParams = mBasket.getLayoutParams();

        categWrapParams.width = categWidth;
        showcaseParams.width = showcaseWidth;
        basketParams.width = basketWidth;

        mCategoriesWrapper.setLayoutParams(categWrapParams);
        mShowcase.setLayoutParams(showcaseParams);
        mBasket.setLayoutParams(basketParams);
    }

    // Return value for size of layout between minSize and maxSize corresponding to movX
    private int calculateLayoutSize(int movX, int minSize, int maxSize) {
        if (movX <= 0) {
            return minSize;
        } else if (movX < maxSize - minSize) {
            return minSize + movX;
        } else {
            return maxSize;
        }
    }

    private void showFloatingMenu() {
        mAddBtn.setImageResource(R.drawable.ic_close_white_24dp);

        mCheckAllBtn.setVisibility(View.VISIBLE);
        mDelAllBtn.setVisibility(View.VISIBLE);

        isMenuShown = true;

        mViewModel.onFloatingMenuShown();
    }

    private void hideFloatingMenu() {
        mAddBtn.setImageResource(R.drawable.ic_edit_24dp);

        mCheckAllBtn.setVisibility(View.INVISIBLE);
        mDelAllBtn.setVisibility(View.INVISIBLE);

        isMenuShown = false;
    }

    private void setFilter(int tag) {
        mViewModel.setFilter(Utils.getIdName(tag));
    }

    /* Interfaces methods */

    @Override
    public void onDialogAcceptReset(boolean fullReset) {
        mViewModel.resetShowcase(fullReset);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "reset showcase");
        bundle.putString(FirebaseAnalytics.Param.CHECKOUT_OPTION, "is full reset: " + fullReset);
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void onDelModeEnable() {
        boolean dmAllowed = mViewModel.setDelMode(true);
        if (dmAllowed) mDelModePanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDelModeDisable() {
        boolean dmAllowed = mViewModel.setDelMode(false);
        if (dmAllowed) mDelModePanel.setVisibility(View.GONE);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mTouchHelper.startDrag(viewHolder);
    }

    @Override
    public int getItemWidth() {
        return mShowcaseWideWidth;
    }

    @Override
    public int getBasketItemWidth() {
        return mBasketWideWidth;
    }

    @Override
    public int getBasketTextMarginEnd() {
        return basketTextMarginEnd;
    }

    /* On buttons click methods */

    public void onFabClick(View view) {
        mViewModel.onFabClick();
        if (isMenuShown) {
            if (view.getId() == R.id.fab) {
                hideFloatingMenu();
            }
            if (view.getId() == R.id.del_all_btn) {
                mViewModel.deleteChecked();
                hideFloatingMenu();
            }
            if (view.getId() == R.id.check_all_btn) {
                mViewModel.checkAll();
            }
        }
        else if (view.getId() == R.id.fab){
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

    public void onSkipGuideBtnClick(View view) {
        mViewModel.onSkipGuideBtnClick();
    }

    public void onFilterClick(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.all_rb:
                if (checked)
                    setFilter(R.string.all);
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
        if (isLandscape() && mViewModel.isGuideStarted()) return;
        mShowcaseAdapter.cancelDel();
    }

    /* Touch events */

    // Avoid animation and touch conflict by intercept event if changing mode
    class ItemTouchListener extends RecyclerView.SimpleOnItemTouchListener {
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {
            return alreadySetChangeModeAllowing && allowChangeMode;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Do not allow touch in some guide cases.
        if (!mViewModel.isTouchAllowed()) {
            int[] loc = new int[2];
            mSkipGuideBtn.getLocationOnScreen(loc);
            if (event.getX() < loc[0] + mSkipGuideBtn.getWidth()
                    && event.getY() > loc[1]) {
                // allow touch on skip button
                mSkipGuideBtn.onTouchEvent(event);
            } else {
                // touch did not occur over the skip button, so the current case is confirmed
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mViewModel.nextGuideCase();
                }
            }
            return true;
        }
        // do not allow mode change in the landscape orientation
        if (!isLandscape() && mViewModel.isModeChangedAllowed()) {
            handleChangeModeByTouch(event);
        }

        // disallow category selection when resizing layout
        if (!isAllowChooseCategory(event)) {
            /* Provide a touch to showcase, otherwise delete mode would be triggered on each swipe
             * (touch would be perceived as a long press on items in showcase) */
            mShowcase.dispatchTouchEvent(event);
            return true;
        }

        return super.dispatchTouchEvent(event);
    }

    private void handleChangeModeByTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                initX = (int) (event.getX() + 0.5f);
                initY = (int) (event.getY() + 0.5f);

                /* Disable change mode from basket in basket mode
                 * because direction of swipe is match to direction of item delete swipe */
                if (!mViewModel.isShowcaseMode()
                        && initX > (mCategNarrowWidth + mShowcaseNarrowWidth)) {
                    allowChangeMode = false;
                    alreadySetChangeModeAllowing = true;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Do not handle change mode if it didn't allowed
                if (!allowChangeMode && alreadySetChangeModeAllowing) {
                    return;
                }

                movX = (int) (event.getX() + 0.5f - initX);
                int movY = (int) (event.getY() + 0.5f - initY);

                // Allow or disallow changing mode
                if (!alreadySetChangeModeAllowing) {
                    alreadySetChangeModeAllowing = true;
                    if (Math.abs(movY) > Math.abs(movX)) {
                        allowChangeMode = false;
                        return;
                    } else {
                        allowChangeMode = true;
                    }
                }

                changeLayoutSizeByTouch(movX);

                break;
            }

            case MotionEvent.ACTION_UP: {
                if (allowChangeMode) {
                    setMode(movX);
                    mShowcase.onTouchEvent(event);
                    mBasket.onTouchEvent(event);
                } else {
                    allowChangeMode = true;
                }

                alreadySetChangeModeAllowing = false;
                initX = 0;
                movX = 0;

                break;
            }
        }
    }

    private boolean isAllowChooseCategory(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                allowChooseCategory = true;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (allowChangeMode && alreadySetChangeModeAllowing) {
                    allowChooseCategory = false;
                }
                break;
            }
        }
        return allowChooseCategory;
    }

    /* Private methods */

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void startGuide() {
        if (!isLandscape() && !mViewModel.isShowcaseMode()) setShowcaseMode();
        if (mViewModel.isDelMode()) {
            onDelModeDisable();
            mShowcaseAdapter.notifyDataSetChanged(); // update icons
        }
        mSkipGuideBtn.setVisibility(View.VISIBLE);
        mViewModel.startGuide();
    }

    private void handleSearch(Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (query.toLowerCase().equals("all")) {
                addAllItems();
                return;
            }
            addItem(query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String itemName = intent.getDataString();
            addItem(itemName);
        }
    }

    private void cancelSearch() {
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
    }

    private void addItem(String query) {
        mViewModel.addNewItem(query, Utils.getIdName(R.string.other));

        Bundle search = new Bundle();
        search.putString(FirebaseAnalytics.Param.SEARCH_TERM, query);
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.SEARCH, search);
    }

    private void addAllItems() {
        ItemsDao dao = AppDatabase.getInstance(this, null).getDao();
        List<Item> items = new ArrayList<>();
        try {
            items = new getAllAsync(dao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        for (Item item : items) {
            addItem(item.getName());
        }
    }

    private void loadNewVersion() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://drive.google.com/open?id=1HPHjTYmi7xlY6XO6w2QozXg8c_lyh2-9"));
        startActivity(browserIntent);
    }

    private void updateShareIntent(List<Item> items) {
        if (mShareActionProvider != null && items != null) {

            StringBuilder sb = new StringBuilder(getString(R.string.share_intro));
            for (Item item : items) {
                sb.append("\n - ").append(item.getName());
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());

            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
