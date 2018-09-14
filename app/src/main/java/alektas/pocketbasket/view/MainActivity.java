package alektas.pocketbasket.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import alektas.pocketbasket.App;
import alektas.pocketbasket.IPresenter;
import alektas.pocketbasket.R;
import alektas.pocketbasket.db.entity.BasketItem;
import alektas.pocketbasket.db.entity.Item;
import alektas.pocketbasket.viewmodel.ItemsViewModel;

public class MainActivity extends AppCompatActivity implements IView {
    private static final String TAG = "PocketBasketApp";
    private int mDisplayWidth;
    private TransitionSet mTransitionSet;
    private ViewGroup mBasket;
    private ViewGroup mShowcase;
    private RadioGroup mCategories;
    private EditText mNameField;
    private View mAddBtn;
    private BaseAdapter mBasketAdapter;
    private BaseAdapter mShowcaseAdapter;

    private GestureDetector mGestureDetector;
    private ConstraintLayout mConstraintLayout;

    @Inject
    IPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        App.getComponent().inject(this);
        init();
    }

    class SlideListener extends GestureDetector.SimpleOnGestureListener {
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
            if (dY < 100 && dX > 150) {
                TransitionManager.beginDelayedTransition(mConstraintLayout, mTransitionSet);
                if (mPresenter.isShowcaseMode() &&
                        velocityX < 0 &&
                        e1.getX() > 0.9* mDisplayWidth) {
                    setBasketMode();
                }
                else if (!mPresenter.isShowcaseMode() &&
                        velocityX > 0 &&
                        e1.getX() < 0.1* mDisplayWidth){
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
            mPresenter.setShowcaseMode(false);
        }

        private void setShowcaseMode() {
            categParams.width = getResources()
                    .getDimensionPixelSize(R.dimen.categ_sc_mode_size);
            showcaseParams.width = getResources()
                    .getDimensionPixelSize(R.dimen.showcase_sc_mode_size);
            resizeRadioText(mCategories, 14f);
            mNameField.setVisibility(View.GONE);
            mAddBtn.setVisibility(View.GONE);
            mPresenter.setShowcaseMode(true);
        }
    }

    private void init() {
        initDisplayWidth();
        initAnimTransition();

        mConstraintLayout = findViewById(R.id.root_layout);
        mGestureDetector =
                new GestureDetector(this, new SlideListener());

        mPresenter.attachView(this);

        mBasket = findViewById(R.id.basket_list);
        mShowcase = findViewById(R.id.showcase_list);
        mCategories = findViewById(R.id.categ_group);

        mNameField = findViewById(R.id.add_item_field);
        mAddBtn = findViewById(R.id.add_item_btn);

        mBasketAdapter = new BasketAdapter(this, mPresenter);
        ((ListView) mBasket).setAdapter(mBasketAdapter);

        mShowcaseAdapter = new ShowcaseAdapter(this, mPresenter);
        ((ListView) mShowcase).setAdapter(mShowcaseAdapter);

//        ItemsViewModel viewModel = ViewModelProviders.of(this).get(ItemsViewModel.class);
//        viewModel.getBasketData().observe(this, new Observer<List<BasketItem>>() {
//            @Override
//            public void onChanged(@Nullable List<BasketItem> basketItems) {
////                mBasketAdapter.notifyDataSetChanged();
//            }
//        });
//        viewModel.getShowcaseData().observe(this, new Observer<List<Item>>() {
//            @Override
//            public void onChanged(@Nullable List<Item> items) {
////                mShowcaseAdapter.notifyDataSetChanged();
//            }
//        });
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

    public void onAddBtnClick(View view) {
        String itemName = mNameField.getText().toString();
        mNameField.setText("");
        mNameField.clearFocus();
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus())
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        mPresenter.addData(itemName);
    }

    public void onClearBtnClick(View view) {
        mPresenter.deleteAll();
    }

    public void onFilterClick(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.all_rb:
                if (checked)
                    mPresenter.setCategory(0);
                break;
            case R.id.drink_rb:
                if (checked)
                    mPresenter.setCategory(R.string.drink);
                break;
            case R.id.fruits_rb:
                if (checked)
                    mPresenter.setCategory(R.string.fruit);
                break;
            case R.id.veg_rb:
                if (checked)
                    mPresenter.setCategory(R.string.vegetable);
                break;
            case R.id.groats_rb:
                if (checked)
                    mPresenter.setCategory(R.string.groats);
                break;
            case R.id.milky_rb:
                if (checked)
                    mPresenter.setCategory(R.string.milky);
                break;
            case R.id.floury_rb:
                if (checked)
                    mPresenter.setCategory(R.string.floury);
                break;
            case R.id.sweets_rb:
                if (checked)
                    mPresenter.setCategory(R.string.sweets);
                break;
            case R.id.meat_rb:
                if (checked)
                    mPresenter.setCategory(R.string.meat);
                break;
            case R.id.seafood_rb:
                if (checked)
                    mPresenter.setCategory(R.string.seafood);
                break;
            case R.id.semis_rb:
                if (checked)
                    mPresenter.setCategory(R.string.semis);
                break;
            case R.id.sauce_n_oil_rb:
                if (checked)
                    mPresenter.setCategory(R.string.sauce_n_oil);
                break;
            case R.id.household_rb:
                if (checked)
                    mPresenter.setCategory(R.string.household);
                break;
            case R.id.other_rb:
                if (checked)
                    mPresenter.setCategory(R.string.other);
                break;
        }
    }

    @Override
    public void updateBasket() {
        mBasketAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateShowcase() {
        mShowcaseAdapter.notifyDataSetChanged();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView(this);
    }
}
