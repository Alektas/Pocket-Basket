package alektas.pocketbasket.ui.basket;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import alektas.pocketbasket.App;
import alektas.pocketbasket.R;
import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.ui.ItemSizeProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasketFragment extends Fragment implements OnStartDragListener {
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    private BasketViewModel mViewModel;
    private BasketRvAdapter mBasketAdapter;
    private ItemTouchHelper mTouchHelper;
    private RecyclerView mBasket;
    private LinearLayoutManager mLayoutManager;
    private ViewGroup mPlaceholder;
    private ItemSizeProvider mItemSizeProvider;
    /**
     * Cache of the last known basket items count
     */
    private int previousItemsCount = 0;

    public BasketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        App.getComponent().inject(this);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the interface so we can get data from the host
            mItemSizeProvider = (ItemSizeProvider) getContext();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getContext().toString()
                    + " must implement ItemSizeProvider");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_basket, container, false);
        mPlaceholder = root.findViewById(R.id.basket_placeholder);
        mBasket = root.findViewById(R.id.basket_list);
        mLayoutManager = new LinearLayoutManager(getContext());
        mBasket.setLayoutManager(mLayoutManager);
        mBasket.setHasFixedSize(true);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(BasketViewModel.class);
        mBasketAdapter = new BasketRvAdapter(getContext(), mViewModel,this, mItemSizeProvider);
        mBasket.setAdapter(mBasketAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchCallback(getContext(), mBasketAdapter);
        mTouchHelper = new ItemTouchHelper(callback);
        mTouchHelper.attachToRecyclerView(mBasket);

        subscribeOnModel();
    }

    private void subscribeOnModel() {
        mViewModel.getBasketData().observe(getViewLifecycleOwner(), items -> {
            mPlaceholder.setVisibility(items.isEmpty() ? View.VISIBLE : View.INVISIBLE);

            // Fix crashes when a lot of updating comes at the same time
            // (animation performed on the old items)
            RecyclerView.ItemAnimator animator = mBasket.getItemAnimator();
            if (animator == null) {
                setItems(items);
                return;
            }
            animator.isRunning(() -> setItems(items));
        });
    }

    private void setItems(List<BasketItem> items) {
        mBasketAdapter.setItems(new ArrayList<>(items), () -> scrollToTop(items.size()));
    }

    private void scrollToTop(int newSize) {
        // Need to scroll only if we were on the top of the list before
        if (mLayoutManager.findFirstVisibleItemPosition() > 0) {
            previousItemsCount = newSize;
            return;
        }

        // If added a new item to the basket then scroll to the beginning of the list
        if (newSize > previousItemsCount) mBasket.smoothScrollToPosition(0);
        previousItemsCount = newSize;
    }

    @Override
    public boolean onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mTouchHelper.startDrag(viewHolder);
        return true;
    }

}
