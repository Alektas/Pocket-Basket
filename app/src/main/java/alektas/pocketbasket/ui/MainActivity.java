package alektas.pocketbasket.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.domain.utils.Event;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.ui.DisposableGuideCaseListener;
import alektas.pocketbasket.guide.ui.GuideCaseView;
import alektas.pocketbasket.guide.ui.GuidePresenter;
import alektas.pocketbasket.guide.ui.SequentialGuidePresenter;
import alektas.pocketbasket.ui.dialogs.AboutDialog;
import alektas.pocketbasket.ui.dialogs.GuideAcceptDialog;
import alektas.pocketbasket.ui.dialogs.ResetDialog;
import alektas.pocketbasket.ui.dialogs.ShareUnsuccessfulDialog;
import alektas.pocketbasket.ui.utils.SmoothDecelerateInterpolator;
import alektas.pocketbasket.utils.ResourcesUtils;
import alektas.pocketbasket.widget.BasketWidget;

public class MainActivity extends AppCompatActivity implements
        ResetDialog.ResetDialogListener,
        GuideAcceptDialog.GuideAcceptDialogListener,
        DisposableGuideCaseListener,
        ItemSizeProvider {

    private static final String TAG = "MainActivity";
    private static final String SAVED_CATEGORY_KEY = "saved_category";
    private static final long CHANGE_MODE_TIME = 250;
    private static final float CHANGE_MODE_MIN_VELOCITY = 150;
    /**
     * Affect to interpolator selection for the mode change.
     * The smaller the divider, the easier faster interpolator is selecting.
     */
    private static final float CHANGE_MODE_VELOCITY_DIVIDER = 1000;

    private int mCategNarrowWidth;
    private int mCategWideWidth;
    private int mShowcaseWideWidth;
    private int mShowcaseNarrowWidth;
    private int mBasketNarrowWidth;
    private int mBasketWideWidth;

    private int initX;
    private int initY;
    private int movX;
    private float mMaxVelocity;
    private float protectedInterval;
    private float changeModeDistance;
    private int changeModeStartDistance;

    private boolean allowChangeMode = true;
    private boolean isChangeModeHandled = false;

    private SharedPreferences mGuidePrefs;
    private SharedPreferences mPrefs;

    private ActivityViewModel mViewModel;
    private VelocityTracker mVelocityTracker;
    private ShareActionProvider mShareActionProvider;
    private ViewGroup mRootLayout;
    private View mBasketContainer;
    private View mShowcaseContainer;
    private View mCategoriesContainer;
    private RecyclerView mShowcase;
    private RecyclerView mBasket;
    private SearchView mSearchView;
    private TransitionSet mChangeModeTransition;
    private Transition mChangeBounds;
    private Transition mDelToolbarTransition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Main); // Remove splash screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.getComponent().inject(this);
        BottomAppBar bar = findViewById(R.id.appbar);
        setSupportActionBar(bar);

        init();

        /* Update items in the database when other app version is launched or locale is changed.
         * It allow to display correct correct item names */
        String curLang = ResourcesUtils.getCurrentLocale().getLanguage();
        String savedLang = mPrefs.getString(getString(R.string.LOCALE_KEY), "lang");

        int vc = ResourcesUtils.getVersionCode();
        boolean isVersionChanged =
                mPrefs.getInt(getString(R.string.VERSION_CODE_KEY), 1) != vc;

        if (!savedLang.equals(curLang) || isVersionChanged) {
            mPrefs.edit()
                    .putString(getString(R.string.LOCALE_KEY), curLang)
                    .putInt(getString(R.string.VERSION_CODE_KEY), vc)
                    .apply();
            mViewModel.updateLocaleNames();
        }

        // If it is the first app launch offer to start the guide
        if (mPrefs.getBoolean(getString(R.string.FIRST_START_KEY), true)) {
            mPrefs.edit().putBoolean(getString(R.string.FIRST_START_KEY), false).apply();
            showHintsAcceptDialog();
        }
    }

    @Override
    protected void onStop() {
        mPrefs.edit().putInt(SAVED_CATEGORY_KEY, getSelectedCategoryId()).apply();
        BasketWidget.updateItems(this);
        super.onStop();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        // Show icons in toolbar menu
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        // Locate MenuItem with ShareActionProvider
        MenuItem shareItem = menu.findItem(R.id.menu_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        // Log analytic event on share
        mShareActionProvider.setOnShareTargetSelectedListener((source, intent) -> {
            String sharedItems = intent.getStringExtra(Intent.EXTRA_TEXT);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT, sharedItems);
            App.getAnalytics().logEvent(FirebaseAnalytics.Event.SHARE, bundle);
            return false;
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_check_all:
                mViewModel.onCheckAllBtnClick();
                return true;

            case R.id.menu_del_checked:
                mViewModel.onDelCheckedBtnClick();
                return true;

            case R.id.menu_share:
                onShareBasketItems(mViewModel.getBasketItems());
                return true;

            case R.id.menu_reset:
                DialogFragment resetDialog = new ResetDialog();
                resetDialog.show(getSupportFragmentManager(), "ResetDialog");
                return true;

            case R.id.menu_guide:
                showHintsAcceptDialog();
                return true;

            case R.id.menu_about:
                DialogFragment aboutDialog = new AboutDialog();
                aboutDialog.show(getSupportFragmentManager(), "AboutDialog");
                // Log analytic event
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "read about app");
                App.getAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showHintsAcceptDialog() {
        DialogFragment dialog = new GuideAcceptDialog();
        dialog.show(getSupportFragmentManager(), "GuideAcceptDialog");
    }

    // Used for search handle
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleSearch(intent);
    }


    /* Init methods */

    private void init() {
        mGuidePrefs = getSharedPreferences(getString(R.string.GUIDE_PREFERENCES_FILE_KEY), MODE_PRIVATE);
        mPrefs = getSharedPreferences(getString(R.string.PREFERENCES_FILE_KEY), MODE_PRIVATE);

        initSearch();
        initTransitions();

        mRootLayout = findViewById(R.id.root_layout);
        mCategoriesContainer = findViewById(R.id.fragment_categories);
        mShowcaseContainer = findViewById(R.id.fragment_showcase);
        mShowcase = mShowcaseContainer.findViewById(R.id.showcase_list);
        mBasketContainer = findViewById(R.id.fragment_basket);
        mBasket = mBasketContainer.findViewById(R.id.basket_list);
        initDimensions();

        mViewModel = ViewModelProviders.of(this).get(ActivityViewModel.class);
        mViewModel.setOrientationState(isLandscape());
        GuidePresenter guidePresenter = buildGuide();

        subscribeOnModel(mViewModel, mGuidePrefs, guidePresenter);

        RadioGroup rg = mCategoriesContainer.findViewById(R.id.categories_radiogroup);
        initCategorySelection(mPrefs, rg);

        if (isLandscape()) {
            applyLandscapeLayout();
        }
    }

    private void initTransitions() {
        mDelToolbarTransition = TransitionInflater.from(this)
                .inflateTransition(R.transition.transition_del_toolbar);

        mChangeBounds = new ChangeBounds();

        mChangeModeTransition = new TransitionSet();
        mChangeModeTransition.setDuration(CHANGE_MODE_TIME)
                .setOrdering(TransitionSet.ORDERING_TOGETHER)
                .addTransition(mChangeBounds);
    }

    private void initDimensions() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
        int minDisplaySize = Math.min(screenHeight, screenWidth);
        mCategNarrowWidth = (int) getResources().getDimension(R.dimen.width_categ_narrow);
        mShowcaseNarrowWidth = (int) getResources().getDimension(R.dimen.width_showcase_narrow);
        mBasketNarrowWidth = (int) getResources().getDimension(R.dimen.width_basket_narrow);
        mCategWideWidth = (int) getResources().getDimension(R.dimen.width_categ_wide);
        mShowcaseWideWidth = minDisplaySize - mCategWideWidth - mBasketNarrowWidth;
        if (isLandscape()) {
            mBasketWideWidth = screenWidth - mCategWideWidth - mShowcaseWideWidth;
        } else {
            mBasketWideWidth = screenWidth - mCategNarrowWidth - mShowcaseNarrowWidth;
        }

        changeModeDistance = getResources().getDimension(R.dimen.change_mode_distance);
        protectedInterval = getResources().getDimension(R.dimen.protected_interval);
        changeModeStartDistance =
                (int) getResources().getDimension(R.dimen.change_mode_start_distance);

        mMaxVelocity = ViewConfiguration.get(this).getScaledMaximumFlingVelocity();
    }

    private void initSearch() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = findViewById(R.id.menu_search);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
        // Remove focus from search view and hide keyboard
        if (TextUtils.isEmpty(mSearchView.getQuery())) {
            cancelSearch();
            View root = findViewById(R.id.root_layout);
            root.requestFocus();
        }
    }

    private void subscribeOnModel(ActivityViewModel viewModel,
                                  SharedPreferences guidePrefs,
                                  GuidePresenter guidePresenter) {
        viewModel.curGuideCaseData().observe(this, caseKey -> {
            guidePresenter.hideCurrentCase();
            if (caseKey == null) {
                return;
            }
            if (guidePrefs.getBoolean(caseKey, false)) {
                viewModel.onEventHappened(caseKey);
                return;
            }
            guidePresenter.showCase(caseKey);
        });

        viewModel.completedGuideCaseData().observe(this, finishedCase -> {
            guidePrefs.edit().putBoolean(finishedCase, true).apply();
        });

        View delModeToolbar = findViewById(R.id.toolbar_del_mode);
        viewModel.deleteModeData().observe(this, delMode -> {
            TransitionManager.beginDelayedTransition(mRootLayout, mDelToolbarTransition);
            delModeToolbar.setVisibility(delMode ? View.VISIBLE : View.GONE);
            if (delMode) cancelSearch();
        });

        viewModel.getDeleteCheckedEvent().observe(this, event -> {
            showEventSnackbar(event,
                    R.string.remove_checked_items_success,
                    R.string.remove_checked_items_fail);
        });

        viewModel.getResetShowcaseEvent().observe(this, event -> {
            showEventSnackbar(event,
                    R.string.reset_showcase_success,
                    R.string.reset_showcase_fail);
        });

        TextView counter = findViewById(R.id.toolbar_del_mode_counter);
        viewModel.deleteItemsCountData().observe(this, delCount -> {
            counter.setText(String.valueOf(delCount));
        });

        viewModel.showcaseModeState().observe(this, isShowcase -> {
            // Change mode enabled only if it's not a landscape layout
            if (isLandscape()) return;
            if (isShowcase) {
                applyShowcaseModeLayout();
            } else {
                applyBasketModeLayout();
            }
        });
    }

    /**
     * Create guide cases and build the guide presenter with them.
     *
     * @return guide presenter instance
     */
    private GuidePresenter buildGuide() {
        //  Guide: change mode
        View changeModeImg = findViewById(R.id.guide_scroll_hor_img);
        Animator scrollHorizAnim = AnimatorInflater
                .loadAnimator(this, R.animator.anim_guide_scroll_horiz);
        GuideCaseView changeModeCase = new GuideCaseView
                .Builder(GuideContract.GUIDE_CHANGE_MODE)
                .addViews(changeModeImg, findViewById(R.id.guide_change_mode_body))
                .setAnimation(scrollHorizAnim, changeModeImg)
                .build();

        //  Guide: add item
        View addItemImg = findViewById(R.id.guide_tap_add_img);
        Animator tapAnim = AnimatorInflater
                .loadAnimator(this, R.animator.anim_guide_tap);
        GuideCaseView addItemCase = new GuideCaseView
                .Builder(GuideContract.GUIDE_ADD_ITEM_BY_TAP)
                .addViews(addItemImg, findViewById(R.id.guide_add_by_tap_body))
                .setAnimation(tapAnim, addItemImg)
                .build();

        //  Guide: mark item in Basket
        Animator tapAnim2 = AnimatorInflater
                .loadAnimator(this, R.animator.anim_guide_tap);
        View checkItemImg = findViewById(R.id.guide_tap_check_img);
        GuideCaseView checkItemCase = new GuideCaseView
                .Builder(GuideContract.GUIDE_CHECK_ITEM)
                .addViews(checkItemImg, findViewById(R.id.guide_check_body))
                .setAnimation(tapAnim2, checkItemImg)
                .build();

        //  Guide: move item in Basket
        Animator scrollVertAnim = AnimatorInflater
                .loadAnimator(this, R.animator.anim_guide_scroll_vert);
        View moveItemImg = findViewById(R.id.guide_scroll_vert_img);
        GuideCaseView moveItemCase = new GuideCaseView
                .Builder(GuideContract.GUIDE_MOVE_ITEM)
                .addViews(moveItemImg, findViewById(R.id.guide_move_item_body))
                .setAnimation(scrollVertAnim, moveItemImg)
                .build();

        //  Guide: remove item from Basket
        View removeItemImg = findViewById(R.id.guide_swipe_right_img);
        Animator swipeRightAnim = AnimatorInflater
                .loadAnimator(this, R.animator.anim_guide_swipe_right);
        GuideCaseView removeItemCase = new GuideCaseView
                .Builder(GuideContract.GUIDE_SWIPE_REMOVE_ITEM)
                .addViews(removeItemImg, findViewById(R.id.guide_swipe_remove_body))
                .setAnimation(swipeRightAnim, removeItemImg)
                .build();

        //  Guide: turn on delete mode
        View longPressImg = findViewById(R.id.guide_del_mode_img);
        Animator longPressAnim = AnimatorInflater
                .loadAnimator(this, R.animator.anim_guide_long_press);
        GuideCaseView delModeCase = new GuideCaseView
                .Builder(GuideContract.GUIDE_DEL_MODE)
                .addViews(longPressImg, findViewById(R.id.guide_del_mode_body))
                .setAnimation(longPressAnim, longPressImg)
                .build();

        //  Guide: delete items from Showcase
        Animator pointAnim = AnimatorInflater
                .loadAnimator(this, R.animator.anim_guide_point_down);
        View tapToDelImg = findViewById(R.id.guide_tap_delete_img);
        GuideCaseView deleteItemsCase = new GuideCaseView
                .Builder(GuideContract.GUIDE_DEL_SELECTED_ITEMS)
                .addViews(tapToDelImg, findViewById(R.id.guide_delete_items_body))
                .setAnimation(pointAnim, tapToDelImg)
                .build();

        //  Guide: basket menu help
        Animator pointAnim2 = AnimatorInflater
                .loadAnimator(this, R.animator.anim_guide_point_down);
        View tapBasketMenuButtons = findViewById(R.id.guide_basket_menu_img);
        GuideCaseView floatingMenuHelpCase = new GuideCaseView
                .Builder(GuideContract.GUIDE_BASKET_MENU_HELP)
                .addViews(tapBasketMenuButtons, findViewById(R.id.guide_basket_menu_body))
                .setAnimation(pointAnim2, tapBasketMenuButtons)
                .build();

        GuidePresenter guidePresenter = new SequentialGuidePresenter(this);
        guidePresenter
                .addCase(changeModeCase)
                .addCase(addItemCase)
                .addCase(checkItemCase)
                .addCase(moveItemCase)
                .addCase(removeItemCase)
                .addCase(delModeCase)
                .addCase(deleteItemsCase)
                .addCase(floatingMenuHelpCase);

        return guidePresenter;
    }

    private void initCategorySelection(SharedPreferences prefs, RadioGroup rg) {
        int catId = prefs.getInt(SAVED_CATEGORY_KEY, 0);
        if (catId == 0) return;
        rg.check(catId);
        onFilterClick(rg.findViewById(catId));
    }

    private int getSelectedCategoryId() {
        if (mCategoriesContainer == null) return 0;
        RadioGroup rg = mCategoriesContainer.findViewById(R.id.categories_radiogroup);
        return rg.getCheckedRadioButtonId();
    }


    /* Layout changes (size and visibility) methods */

    /**
     * Set Landscape Mode: categories, showcase and basket expanded
     */
    private void applyLandscapeLayout() {
        changeLayoutSize(mCategWideWidth,
                mShowcaseWideWidth,
                0);
    }

    /**
     * Set current mode to the Basket mode.
     * Mode state is observed, so this method also invoke {@link #applyBasketModeLayout()}
     * which actually resize views to consist the mode.
     */
    private void setBasketMode() {
        if (isLandscape()) return;
        mViewModel.setShowcaseMode(false);
    }

    /**
     * Apply sizes of the layout parts in consist of the Basket Mode:
     * categories and showcase narrowed, basket expanded.
     * Warning! This method don't change global mode state and it should be
     * invoked only when the mode is not changed, but it's necessary to apply appropriate sizes.
     * <p>
     * To actually change mode invoke {@link #setBasketMode() setBasketMode} instead.
     */
    private void applyBasketModeLayout() {
        TransitionManager.beginDelayedTransition(mRootLayout, mChangeModeTransition);
        changeLayoutSize(mCategNarrowWidth,
                mShowcaseNarrowWidth,
                0);
    }

    /**
     * Set current mode to the Showcase mode.
     * Mode state is observed, so this method also invoke {@link #applyShowcaseModeLayout()}
     * which actually resize views to consist the mode.
     */
    private void setShowcaseMode() {
        if (isLandscape()) return;
        mViewModel.setShowcaseMode(true);
    }

    /**
     * Apply sizes of the layout parts in consist of the Showcase Mode:
     * categories and showcase expanded, basket narrowed.
     * Warning! This method don't change global mode state and it should be
     * invoked only when the mode is not changed, but it's necessary to apply appropriate sizes.
     * <p>
     * To actually change mode invoke {@link #setShowcaseMode() setShowcaseMode} instead.
     */
    private void applyShowcaseModeLayout() {
        TransitionManager.beginDelayedTransition(mRootLayout, mChangeModeTransition);
        changeLayoutSize(mCategWideWidth,
                0,
                mBasketNarrowWidth);
    }

    /**
     * Change size of the layout parts: Categories, Showcase and Basket.
     * If one of the width equal '0' then the corresponding layout part fills in the free space.
     *
     * @param categWidth    width of the Categories in pixels
     * @param showcaseWidth width of the Showcase in pixels
     * @param basketWidth   width of the Basket in pixels
     */
    private void changeLayoutSize(int categWidth, int showcaseWidth, int basketWidth) {
        changeViewWidth(mCategoriesContainer, categWidth);
        changeViewWidth(mShowcaseContainer, showcaseWidth);
        changeViewWidth(mBasketContainer, basketWidth);
    }

    /**
     * Change size of the view.
     * If width equal '0' then view fills in the free space.
     *
     * @param view view which width need change
     * @param width width of the view in pixels
     */
    private void changeViewWidth(View view, int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params.width == width) return;
        params.width = width;
        view.setLayoutParams(params);
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
    public void onDialogAcceptHints() {
        mPrefs.edit().putBoolean(getString(R.string.SHOW_HINTS_KEY), true).apply();
        mGuidePrefs.edit().clear().apply();
        mViewModel.startGuide(mGuidePrefs);

        Bundle startGuide = new Bundle();
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, startGuide);
    }

    @Override
    public void onDialogRejectHints() {
        mPrefs.edit().putBoolean(getString(R.string.SHOW_HINTS_KEY), false).apply();
        mGuidePrefs.edit().clear().apply();
        mViewModel.stopGuide();

        Bundle endGuide = new Bundle();
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, endGuide);
    }

    @Override
    public void onDisposableCaseFinish(String caseKey) {
        mViewModel.onEventHappened(caseKey);
    }

    @Override
    public int getItemWidth() {
        return mShowcaseWideWidth;
    }

    @Override
    public int getBasketItemWidth() {
        return mBasketWideWidth;
    }


    /* On click methods */

    public void onHintClick(View view) {
        mViewModel.onHintClick();
    }

    public void onDelModeBtnClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_del_mode_btn_delete:
                mViewModel.onDeleteSelectedShowcaseItems();
                break;
            case R.id.toolbar_del_mode_btn_close:
                mViewModel.onCloseDelMode();
                break;
        }
    }

    public void onFilterClick(View view) {
        if (view == null) {
            setFilter(R.string.all);
            return;
        }

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

    private void setFilter(int tag) {
        mViewModel.setFilter(ResourcesUtils.getResIdName(tag));
    }

    public void onLinkClick(View view) {
        String link = null;

        switch (view.getId()) {
            case R.id.apache_link:
            case R.id.apache2_link: {
                link = getString(R.string.apache_license_link);
                break;
            }

            case R.id.cc_link: {
                link = getString(R.string.cc_license_link);
                break;
            }

            case R.id.jeff_link: {
                link = getString(R.string.jeff_link);
                break;
            }

            case R.id.bom_link: {
                link = getString(R.string.bom_link);
                break;
            }

            case R.id.google_link: {
                link = getString(R.string.google_material_link);
                break;
            }

            case R.id.privacy_policy_link: {
                link = getString(R.string.privacy_policy_link);
                break;
            }
        }

        if (link == null) return;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }


    /* Touch events */

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Do not allow a mode change in the landscape orientation
        if (!isLandscape()) {
            handleChangeModeByTouch(event);
            // When changing mode is active cancel all other actions to avoid fake clicks
            // Also stop scrolling to avoid crashing
            if (isModeChanging()) {
                mShowcase.stopScroll();
                mBasket.stopScroll();
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private boolean isModeChanging() {
        return allowChangeMode && isChangeModeHandled;
    }

    private void handleChangeModeByTouch(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                initX = (int) (event.getX() + 0.5f);
                initY = (int) (event.getY() + 0.5f);

                /* Disable the change mode from the basket in the basket mode
                 * because direction of the swipe is match to direction of the item delete swipe */
                if (!mViewModel.isShowcaseMode()
                        && initX > (mCategNarrowWidth + mShowcaseNarrowWidth)) {
                    allowChangeMode = false;
                    isChangeModeHandled = true;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Do not handle change mode if it didn't allowed
                if (!allowChangeMode && isChangeModeHandled) {
                    return;
                }

                movX = (int) (event.getX() + 0.5f - initX);
                int movY = (int) (event.getY() + 0.5f - initY);

                // Allow or disallow changing mode
                if (!isChangeModeHandled) {
                    if (Math.abs(movY) > Math.abs(movX)) {
                        // Vertical direction is dominate so change mode is not allowed
                        isChangeModeHandled = true;
                        allowChangeMode = false;
                        return;
                    }
                    if (Math.abs(movX) < changeModeStartDistance) {
                        isChangeModeHandled = false;
                        return; // gesture length is not enough to start change mode handling
                    }
                    isChangeModeHandled = true;
                    allowChangeMode = true;
                }

                changeLayoutSizeByTouch(movX);
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                finishModeChange();
                break;
            }
        }
    }

    /**
     * Set size of the layout parts in depends of touch moving distance
     *
     * @param movX touch moving distance
     */
    private void changeLayoutSizeByTouch(int movX) {
        int showcaseOffset;
        int categOffset;
        if (mViewModel.isShowcaseMode()) {
            showcaseOffset = mShowcaseWideWidth - mShowcaseNarrowWidth + movX / 2 + changeModeStartDistance;
            categOffset = mCategWideWidth - mCategNarrowWidth + movX / 2 + changeModeStartDistance;
        } else {
            showcaseOffset = movX / 2 - changeModeStartDistance;
            categOffset = movX / 2 - changeModeStartDistance;
        }

        int showcaseWidth = calculateLayoutSize(showcaseOffset,
                mShowcaseNarrowWidth,
                mShowcaseWideWidth);
        int categWidth = calculateLayoutSize(categOffset,
                mCategNarrowWidth,
                mCategWideWidth);

        changeLayoutSize(categWidth, showcaseWidth, 0);
    }

    /**
     * Calculate layout size according to touch gesture distance.
     *
     * @param movX    touch distance in pixels
     * @param minSize minimum size of the layout
     * @param maxSize maximum size of the layout
     * @return value for size of layout between minSize and maxSize according to movX
     */
    private int calculateLayoutSize(int movX, int minSize, int maxSize) {
        if (movX <= 0) {
            return minSize;
        } else if (movX < maxSize - minSize) {
            return minSize + movX;
        } else {
            return maxSize;
        }
    }

    /**
     * Set appropriate mode (Basket or Showcase), cancel touch handling and clear the state
     */
    private void finishModeChange() {
        if (isChangeModeHandled && allowChangeMode) {
            mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
            float velocity = mVelocityTracker.getXVelocity();
            Interpolator interpolator = getInterpolator(velocity);
            mChangeBounds.setInterpolator(interpolator);
            setMode(movX, velocity);
        }

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        allowChangeMode = true;
        isChangeModeHandled = false;
        initX = 0;
        movX = 0;
    }

    /**
     * Gives appropriate animation interpolator according to the gesture velocity
     *
     * @param velocity speed of the user gesture
     * @return appropriate decelerate interpolator
     */
    private Interpolator getInterpolator(float velocity) {
        int factor = (int) (Math.abs(velocity) / CHANGE_MODE_VELOCITY_DIVIDER);
        switch (factor) {
            case 0: {
                return new AccelerateDecelerateInterpolator();
            }
            case 1: {
                // through it
            }
            case 2: {
                return new SmoothDecelerateInterpolator();
            }
            case 3: {
                return new DecelerateInterpolator(1.5f);
            }
            default: {
                return new DecelerateInterpolator(2.5f);
            }
        }
    }

    /**
     * Set basket or showcase mode in depends of the touch moving distance and velocity
     *
     * @param velocity touch moving velocity
     */
    private void setMode(int movX, float velocity) {
        if (mViewModel.isShowcaseMode()) {
            trySetBasketMode(movX, velocity);
        } else {
            trySetShowcaseMode(movX, velocity);
        }
    }

    private void trySetBasketMode(int movX, float velocity) {
        if (velocity < CHANGE_MODE_MIN_VELOCITY && movX < -changeModeDistance) {
            setBasketMode();
        } else {
            recoverShowcaseMode(movX);
        }
    }

    private void trySetShowcaseMode(int movX, float velocity) {
        if (velocity > -CHANGE_MODE_MIN_VELOCITY && movX > changeModeDistance) {
            setShowcaseMode();
        } else {
            recoverBasketMode(movX);
        }
    }

    private void recoverShowcaseMode(int movX) {
        if (movX < -protectedInterval) {
            TransitionManager.beginDelayedTransition(mRootLayout, mChangeModeTransition);
        }
        changeLayoutSize(mCategWideWidth,
                0,
                mBasketNarrowWidth);
    }

    private void recoverBasketMode(int movX) {
        if (movX > protectedInterval) {
            TransitionManager.beginDelayedTransition(mRootLayout, mChangeModeTransition);
        }
        changeLayoutSize(mCategNarrowWidth,
                mShowcaseNarrowWidth,
                0);
    }


    /* Other methods */

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void handleSearch(Intent intent) {
        /* Intent.ACTION_SEARCH - on enter text typed in search view
         * Intent.ACTION_VIEW - on click in search suggestions  */
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            onSearch(query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String itemName = intent.getDataString();
            onSearch(itemName);
        }
        cancelSearch();
    }

    private void cancelSearch() {
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
    }

    /**
     * Add item to the Basket and (if it's a new) to the Showcase
     *
     * @param query name of the item
     */
    private void onSearch(String query) {
        mViewModel.onSearch(query);

        Bundle search = new Bundle();
        search.putString(FirebaseAnalytics.Param.SEARCH_TERM, query);
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.SEARCH, search);
    }

    private void toggleSearchView() {
        if (mSearchView.hasFocus()) {
            if (!TextUtils.isEmpty(mSearchView.getQuery())) {
                onSearch(mSearchView.getQuery().toString());
            }
            cancelSearch();
        } else {
            mSearchView.setIconified(false); // give focus
        }
    }

    /**
     * Rewrite shared basket items in the share intent.
     * If there are items, share selector is shown, else alert dialog is shown.
     *
     * @param items shared basket items
     */
    private void onShareBasketItems(List<? extends ItemModel> items) {
        if (mShareActionProvider != null && items != null && !items.isEmpty()) {

            StringBuilder sb = new StringBuilder(getString(R.string.share_intro));
            for (ItemModel item : items) {
                sb.append("\n - ").append(item.getName());
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());

            mShareActionProvider.setShareIntent(shareIntent);
        } else {
            DialogFragment dialog = new ShareUnsuccessfulDialog();
            dialog.show(getSupportFragmentManager(), "ShareUnsuccessfulDialog");
        }
    }

    private void showEventSnackbar(Event<Boolean> event, @StringRes int successMsg, @StringRes int failMsg) {
        Boolean isSuccess = event.getValue();
        if (isSuccess == null) return;
        String msg = isSuccess ?
                getString(successMsg) :
                getString(failMsg);
        Snackbar.make(mRootLayout, msg, Snackbar.LENGTH_SHORT)
                .setAnchorView(findViewById(R.id.appbar))
                .show();
    }

}
