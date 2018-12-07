package alektas.pocketbasket.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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

import alektas.pocketbasket.async.getAllAsync;
import alektas.pocketbasket.db.AppDatabase;
import alektas.pocketbasket.db.dao.ItemsDao;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.viewmodel.ItemsViewModel;
import alektas.pocketbasket.db.entity.Item;

public class MainActivity extends AppCompatActivity
        implements ResetDialog.ResetDialogListener,
        AddItemDialog.AddItemDialogListener,
        DeleteModeListener,
        OnStartDragListener {

    private static final String TAG = "PocketBasketApp";

    private int mCategNarrowWidth;
    private int mCategWideWidth;
    private int mShowcaseWideWidth;
    private int mShowcaseNarrowWidth;
    private int mBasketNarrowWidth;
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
    private ConstraintLayout mConstraintLayout;
    private ShareActionProvider mShareActionProvider;
    private ItemTouchHelper mTouchHelper;

    private ItemsViewModel mViewModel;
    private int initX;
    private int initY;
    private int movX;
    private float changeModeDistance;
    private boolean allowChangeMode = true;
    private boolean alreadySetChangeModeAllowing = false;

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
                return true;
            }

            case R.id.menu_about: {
                DialogFragment dialog = new AboutDialog();
                dialog.show(getSupportFragmentManager(), "AboutDialog");
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
        initDimensions();
        initSearch();
        initFloatingActionMenu();

        mConstraintLayout = findViewById(R.id.root_layout);

        mCategories = findViewById(R.id.categ_group);

        mDelModePanel = findViewById(R.id.del_mode_panel);
        mCancelDmBtn = findViewById(R.id.cancel_dm_btn);

        mViewModel = ViewModelProviders.of(this).get(ItemsViewModel.class);

        initBasket();
        initShowcase();

        mViewModel.getBasketData().observe(this, (items -> {

            mBasketAdapter.setItems(items);
            updateShareIntent(items);
        }));
        mViewModel.getShowcaseData().observe(this,
                mShowcaseAdapter::setItems);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            setLandscapeLayout();
        } else {
            setShowcaseMode();
        }
    }

    private void initAnimTransition() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTransitionSet = TransitionInflater.from(this)
                    .inflateTransition(R.transition.change_mode_transition);
        } else {
            mTransitionSet = TransitionInflater.from(this)
                    .inflateTransition(R.transition.change_mode_transition_compat);
        }
    }

    private void initDimensions() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        mCategNarrowWidth = (int) getResources().getDimension(R.dimen.categ_narrow_size);
        mShowcaseNarrowWidth = (int) getResources().getDimension(R.dimen.showcase_narrow_size);
        mBasketNarrowWidth = (int) getResources().getDimension(R.dimen.basket_narrow_size);
        mCategWideWidth = (int) getResources().getDimension(R.dimen.categ_wide_size);
        mShowcaseWideWidth = screenWidth - mCategWideWidth - mBasketNarrowWidth;

        changeModeDistance = getResources().getDimension(R.dimen.change_mode_distance);
    }

    private void initSearch() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = findViewById(R.id.menu_search);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
    }

    private void initFloatingActionMenu() {
        mAddBtn = findViewById(R.id.add_item_btn);
        mCheckAllBtn = findViewById(R.id.check_all_btn);
        mDelAllBtn = findViewById(R.id.del_all_btn);
        mAddBtn.setOnLongClickListener(view -> {
            if (!isMenuShown) showFloatingMenu();
            return true;
        });
    }

    private void initBasket() {
        mBasket = findViewById(R.id.basket_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mBasket.setLayoutManager(layoutManager);
        mBasketAdapter = new BasketRvAdapter(this, mViewModel, this);
        mBasket.setAdapter(mBasketAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchCallback(mBasketAdapter);
        mTouchHelper = new ItemTouchHelper(callback);
        mTouchHelper.attachToRecyclerView(mBasket);

        mBasket.addOnItemTouchListener(new ItemTouchListener());
    }

    private void initShowcase() {
        mShowcase = findViewById(R.id.showcase_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mShowcase.setLayoutManager(layoutManager);
        mShowcaseAdapter = new ShowcaseRvAdapter(this, this, mViewModel);
        mShowcase.setAdapter(mShowcaseAdapter);

        mShowcase.addOnItemTouchListener(new ItemTouchListener());
    }

    /* Layout changes methods */

    @SuppressLint("RestrictedApi")
    private void setLandscapeLayout() {
        changeLayoutSize(mCategWideWidth,
                mShowcaseWideWidth,
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
        ViewGroup.LayoutParams categParams = mCategories.getLayoutParams();
        ViewGroup.LayoutParams showcaseParams = mShowcase.getLayoutParams();
        ViewGroup.LayoutParams basketParams = mBasket.getLayoutParams();

        categParams.width = categWidth;
        showcaseParams.width = showcaseWidth;
        basketParams.width = basketWidth;

        mCategories.setLayoutParams(categParams);
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
        mViewModel.addNewItem(itemName, tagRes);
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mTouchHelper.startDrag(viewHolder);
    }

    /* On buttons click methods */

    public void onBtnClick(View view) {
        if (isMenuShown) {
            if (view.getId() == R.id.add_item_btn) {
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

    // Avoid animation and touch conflict by intercept event if changing mode
    class ItemTouchListener extends RecyclerView.SimpleOnItemTouchListener {
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {
            return alreadySetChangeModeAllowing && allowChangeMode;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        handleChangeModeByTouch(event);
        return super.dispatchTouchEvent(event);
    }

    private void handleChangeModeByTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                initX = (int) (event.getX() + 0.5f);
                initY = (int) (event.getY() + 0.5f);

                /* Disable change mode from basket in basket mode
                because direction of swipe is match to direction of item delete swipe */
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

    /* Private methods */

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
        mViewModel.addNewItem(query, R.string.other);
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
