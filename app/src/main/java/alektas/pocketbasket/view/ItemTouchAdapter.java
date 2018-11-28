package alektas.pocketbasket.view;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void clearView();

    void onSwipeStart(RecyclerView.ViewHolder viewHolder);
    void onSwipeEnd(RecyclerView.ViewHolder viewHolder);

}
