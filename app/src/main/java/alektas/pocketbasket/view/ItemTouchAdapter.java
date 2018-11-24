package alektas.pocketbasket.view;

public interface ItemTouchAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onDragCancel();

    void onDragStarted(int fromPosition);

    void onItemDismiss(int position);
}
