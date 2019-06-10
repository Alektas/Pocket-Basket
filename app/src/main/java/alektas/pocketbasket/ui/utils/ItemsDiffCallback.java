package alektas.pocketbasket.ui.utils;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;
import alektas.pocketbasket.domain.entities.ItemModel;

class ItemsDiffCallback extends DiffUtil.Callback {
    private final List<ItemModel> mOldList;
    private final List<ItemModel> mNewList;

    ItemsDiffCallback(List<ItemModel> oldList, List<ItemModel> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        if (mOldList == null) return 0;
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        if (mNewList == null) return 0;
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ItemModel oldItem = mOldList.get(oldItemPosition);
        ItemModel newItem = mNewList.get(newItemPosition);
        if (oldItem instanceof BasketItem) {
            BasketItem oldBasketItem = (BasketItem) oldItem;
            BasketItem newBasketItem = (BasketItem) newItem;
            return oldBasketItem.isMarked() == newBasketItem.isMarked();
        } else if (oldItem instanceof ShowcaseItem) {
            ShowcaseItem oldShowcaseItem = (ShowcaseItem) oldItem;
            ShowcaseItem newShowcaseItem = (ShowcaseItem) newItem;
            return oldShowcaseItem.isRemoval() == newShowcaseItem.isRemoval()
                    && oldShowcaseItem.isExistInBasket() == newShowcaseItem.isExistInBasket();
        }
        return false;
    }
}
