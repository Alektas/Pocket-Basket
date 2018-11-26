package alektas.pocketbasket.view;

public interface ItemTouchAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void clearView();
}
