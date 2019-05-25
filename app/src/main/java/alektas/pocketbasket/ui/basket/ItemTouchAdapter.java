package alektas.pocketbasket.ui.basket;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemMoveEnd();

    void onItemDismiss(int position);

    void onSwipeStart(RecyclerView.ViewHolder viewHolder);
    void onSwipeEnd(RecyclerView.ViewHolder viewHolder);

}
