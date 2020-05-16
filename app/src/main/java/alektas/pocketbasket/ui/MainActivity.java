package alektas.pocketbasket.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.data.AppPreferences;
import alektas.pocketbasket.domain.entities.ItemModel;
import alektas.pocketbasket.guide.GuideContract;
import alektas.pocketbasket.guide.ui.DisposableGuideCaseListener;
import alektas.pocketbasket.guide.ui.GuideCaseView;
import alektas.pocketbasket.guide.ui.GuidePresenter;
import alektas.pocketbasket.guide.ui.SequentialGuidePresenter;
import alektas.pocketbasket.ui.dialogs.AboutDialog;
import alektas.pocketbasket.ui.dialogs.GuideAcceptDialog;
import alektas.pocketbasket.ui.dialogs.ResetDialog;
import alektas.pocketbasket.ui.dialogs.ShareUnsuccessfulDialog;
import alektas.pocketbasket.utils.ResourcesUtils;
import alektas.pocketbasket.widget.BasketWidget;

import static alektas.pocketbasket.di.StorageModule.GUIDE_PREFERENCES_NAME;

public class MainActivity extends AppCompatActivity implements
        ResetDialog.ResetDialogListener,
        GuideAcceptDialog.GuideAcceptDialogListener,
        DisposableGuideCaseListener,
        ItemSizeProvider {

    @Inject
    @Named(GUIDE_PREFERENCES_NAME)
    SharedPreferences mGuidePrefs;
    @Inject
    AppPreferences mPrefs;
    @Inject
    ActivityViewModel mViewModel;
    private ViewModeDelegate mViewModeDelegate;
    private DimensionsProvider mDimens;
    private ShareActionProvider mShareActionProvider;
    private ViewGroup mRootLayout;
    private SearchView mSearchView;
    private Transition mDelToolbarTransition;
    private boolean isDelMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getComponent().inject(this);
        setTheme(R.style.Theme_Main); // Remove splash screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomAppBar bar = findViewById(R.id.bottom_appbar);
        setSupportActionBar(bar);

        init();

        /* Update items in the database when other app version is launched or locale is changed.
         * It allow to display correct correct item names */
        String curLang = ResourcesUtils.getCurrentLanguage();
        String savedLang = mPrefs.getLanguage();

        int vc = ResourcesUtils.getVersionCode();
        boolean isVersionChanged = mPrefs.getVersionCode() != vc;

        if (!savedLang.equals(curLang) || isVersionChanged) {
            mPrefs.saveLanguage(curLang);
            mPrefs.saveVersionCode(vc);
            mViewModel.updateLocaleNames();
        }

        // If it is the first app launch offer to start the guide
        if (mPrefs.isFirstLaunch()) {
            mPrefs.unsetFirstLaunch();
            showHintsAcceptDialog();
        }
    }

    @Override
    protected void onStop() {
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
                mViewModel.onCheckBasket();
                return true;

            case R.id.menu_del_checked:
                mViewModel.onDelCheckedBasketItems();
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

    @Override
    public void onBackPressed() {
        if (isDelMode) {
            mViewModel.onCloseDelMode();
            return;
        }
        super.onBackPressed();
    }

    /* Init methods */

    private void init() {
        initSearch();
        initTransitions();

        mRootLayout = findViewById(R.id.root_layout);

        GuidePresenter guidePresenter = buildGuide();

        subscribeOnModel(mViewModel, mGuidePrefs, guidePresenter);

        mDimens = new DimensionsProvider(this);
        mViewModeDelegate = new ViewModeDelegate(this, mViewModel, mDimens);
    }

    private void initTransitions() {
        mDelToolbarTransition = TransitionInflater.from(this)
                .inflateTransition(R.transition.transition_del_toolbar);
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
            isDelMode = delMode;
            TransitionManager.beginDelayedTransition(mRootLayout, mDelToolbarTransition);
            delModeToolbar.setVisibility(delMode ? View.VISIBLE : View.GONE);
            if (delMode) cancelSearch();
        });

        viewModel.getRemoveCheckedBasketItemsEvent().observe(this, isSuccess -> {
            showEventSnackbar(isSuccess,
                    R.string.remove_checked_items_success,
                    R.string.remove_checked_items_fail);
        });

        viewModel.getResetShowcaseEvent().observe(this, isSuccess -> {
            showEventSnackbar(isSuccess,
                    R.string.reset_showcase_success,
                    R.string.reset_showcase_fail);
        });

        TextView counter = findViewById(R.id.toolbar_del_mode_counter);
        viewModel.deleteItemsCountData().observe(this, delCount -> {
            counter.setText(String.valueOf(delCount));
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
        mPrefs.setHintsShown(true);
        mGuidePrefs.edit().clear().apply();
        mViewModel.startGuide(mGuidePrefs);

        Bundle startGuide = new Bundle();
        App.getAnalytics().logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, startGuide);
    }

    @Override
    public void onDialogRejectHints() {
        mPrefs.setHintsShown(false);
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
        return mDimens.getShowcaseWideWidth();
    }

    @Override
    public int getBasketItemWidth() {
        return mDimens.getBasketWideWidth();
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


    /* Touch events */

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mViewModeDelegate.onTouch(event);
        return super.dispatchTouchEvent(event);
    }


    /* Other methods */

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

    private void showEventSnackbar(Boolean isSuccess, @StringRes int successMsg, @StringRes int failMsg) {
        Log.d("MainActivity", "show snack: " + isSuccess);
        String msg = getString(isSuccess ? successMsg : failMsg);
        Snackbar.make(mRootLayout, msg, Snackbar.LENGTH_SHORT)
                .setAnchorView(findViewById(R.id.bottom_appbar))
                .show();
    }

}