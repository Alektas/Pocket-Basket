package alektas.pocketbasket.ui.showcase;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import alektas.pocketbasket.R;
import alektas.pocketbasket.ui.ItemSizeProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowcaseFragment extends Fragment {
    private static final String TAG = "ShowcaseFragment";
    private ShowcaseViewModel mViewModel;
    private ShowcaseRvAdapter mShowcaseAdapter;
    private LinearLayout mDelModePanel;
    private Transition mDelPanelTransition;

    private ItemSizeProvider mItemSizeProvider;

    public ShowcaseFragment() {
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

        mViewModel = ViewModelProviders.of(this).get(ShowcaseViewModel.class);
        mShowcaseAdapter = new ShowcaseRvAdapter(mViewModel, mItemSizeProvider);
        subscribeOnModel();
        initTransitions();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_showcase, container, false);

        mDelModePanel = root.findViewById(R.id.del_panel);
        View delBtn = mDelModePanel.findViewById(R.id.btn_del);
        delBtn.setOnClickListener(view -> mViewModel.deleteSelectedItems());
        View closeBtn = mDelModePanel.findViewById(R.id.btn_close_panel);
        closeBtn.setOnClickListener(view -> mViewModel.cancelDel());

        RecyclerView showcase = root.findViewById(R.id.showcase_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        showcase.setLayoutManager(layoutManager);
        showcase.setHasFixedSize(true);
        showcase.setAdapter(mShowcaseAdapter);

        return root;
    }

    private void subscribeOnModel() {
        mViewModel.getShowcaseData().observe(this, items -> {
            mShowcaseAdapter.setItems(new ArrayList<>(items));
        });

        mViewModel.showcaseModeState().observe(this, isShowcaseMode -> {
            if (isShowcaseMode || isLandscape()) {
                setDelPanelOrientation(LinearLayout.HORIZONTAL);
            } else {
                setDelPanelOrientation(LinearLayout.VERTICAL);
            }
        });

        mViewModel.delModeState().observe(this, isDelMode -> {
            TransitionManager.beginDelayedTransition((ViewGroup) getView(), mDelPanelTransition);
            if (isDelMode) {
                mDelModePanel.setVisibility(View.VISIBLE);

            } else {
                mDelModePanel.setVisibility(View.GONE);
                // update icons (remove deleting selection)
                mShowcaseAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.getSelectedItemPosition().observe(this, position -> {
            mShowcaseAdapter.notifyItemChanged(position);
        });
    }

    private void initTransitions() {
        mDelPanelTransition = TransitionInflater.from(getContext())
                .inflateTransition(R.transition.transition_del_panel);
    }

    private void setDelPanelOrientation(int orientation) {
        ((LinearLayout) mDelModePanel.findViewById(R.id.del_panel_content))
                .setOrientation(orientation);
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

}
