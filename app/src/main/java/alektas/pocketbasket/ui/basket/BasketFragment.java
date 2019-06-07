package alektas.pocketbasket.ui.basket;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import alektas.pocketbasket.R;
import alektas.pocketbasket.ui.ItemSizeProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasketFragment extends Fragment implements OnStartDragListener {
    private BasketViewModel mViewModel;
    private BasketRvAdapter mBasketAdapter;
    private ItemTouchHelper mTouchHelper;

    private ItemSizeProvider mItemSizeProvider;

    public BasketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(BasketViewModel.class);
        mBasketAdapter = new BasketRvAdapter(getContext(), mViewModel,this, mItemSizeProvider);
        subscribeOnModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_basket, container, false);
        RecyclerView basket = root.findViewById(R.id.basket_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        basket.setLayoutManager(layoutManager);
        basket.setHasFixedSize(true);
        basket.setAdapter(mBasketAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchCallback(getContext(), mBasketAdapter);
        mTouchHelper = new ItemTouchHelper(callback);
        mTouchHelper.attachToRecyclerView(basket);

        return root;
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
