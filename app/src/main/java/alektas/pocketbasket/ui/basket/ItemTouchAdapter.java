package alektas.pocketbasket.ui.basket;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemMoveEnd(RecyclerView.ViewHolder viewHolder);
    void onItemDismiss(int position);

}
