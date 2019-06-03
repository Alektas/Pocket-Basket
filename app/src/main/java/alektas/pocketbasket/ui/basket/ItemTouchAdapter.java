package alektas.pocketbasket.ui.basket;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemMoveStart(RecyclerView.ViewHolder viewHolder);
    void onItemMoveEnd(RecyclerView.ViewHolder viewHolder);

    void onItemDismiss(int position);

    void onSwipeStart(RecyclerView.ViewHolder viewHolder);
    void onSwipeEnd(RecyclerView.ViewHolder viewHolder);

}
