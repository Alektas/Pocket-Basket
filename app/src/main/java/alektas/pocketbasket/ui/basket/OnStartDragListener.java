package alektas.pocketbasket.ui.basket;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Listener for items which dragged by the drag handler
 */
public interface OnStartDragListener {
    /**
     * Invoked when an item start drag by drag handler.
     *
     * @param viewHolder holder for dragged item
     * @return true if a drag handled
     */
    boolean onStartDrag(RecyclerView.ViewHolder viewHolder);
}
