package alektas.pocketbasket.ui.basket;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import alektas.pocketbasket.R;
import alektas.pocketbasket.ui.ChangeModeListener;
import alektas.pocketbasket.ui.ItemSizeProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasketFragment extends Fragment implements OnStartDragListener {
    private BasketViewModel mViewModel;
    private RecyclerView mBasket;
    private BasketRvAdapter mBasketAdapter;
    private ItemTouchHelper mTouchHelper;

    private ItemSizeProvider mItemSizeProvider;
    private ChangeModeListener mModeListener;

    public BasketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ResetDialogListener so we can send events to the host
            mItemSizeProvider = (ItemSizeProvider) getContext();
            mModeListener = (ChangeModeListener) getContext();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getContext().toString()
                    + " must implement ItemSizeProvider and ChangeModeListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(BasketViewModel.class);
        mBasketAdapter = new BasketRvAdapter(getContext(), mViewModel,this, mItemSizeProvider);
        subscribeOnModel();
    }

    @Override
    public void onDestroy() {
        mViewModel.setGuide(null);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_basket, container, false);
        mBasket = root.findViewById(R.id.basket_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBasket.setLayoutManager(layoutManager);
        mBasket.setHasFixedSize(true);
        mBasket.setAdapter(mBasketAdapter);

        // Avoid animation and touch conflict by intercept event if changing mode
        mBasket.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {
                return mModeListener.isChangeModeHandled() && mModeListener.isChangeModeAllowed();
            }
        });

        ItemTouchHelper.Callback callback = new ItemTouchCallback(mBasketAdapter);
        mTouchHelper = new ItemTouchHelper(callback);
        mTouchHelper.attachToRecyclerView(mBasket);

        return root;
    }

    public BasketViewModel getViewModel() {
        return mViewModel;
    }

    private void subscribeOnModel() {
        mViewModel.getBasketData().observe(this, items -> {
            mBasketAdapter.setItems(new ArrayList<>(items));
        });
    }

    @Override
    public boolean onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mTouchHelper.startDrag(viewHolder);
        return true;
    }

}
