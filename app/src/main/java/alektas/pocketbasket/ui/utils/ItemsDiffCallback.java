package alektas.pocketbasket.ui.utils;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import alektas.pocketbasket.ads.NativeAdWrapper;
import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.data.db.entities.ShowcaseItem;

class ItemsDiffCallback extends DiffUtil.Callback {
    private final List<Object> mOldList;
    private final List<Object> mNewList;

    ItemsDiffCallback(List<Object> oldList, List<Object> newList) {
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
        Object oldItem = mOldList.get(oldItemPosition);
        Object newItem = mNewList.get(newItemPosition);
        if (oldItem instanceof BasketItem) {
            BasketItem oldBasketItem = (BasketItem) oldItem;
            // New item also is BasketItem, because the basket don't have ads
            BasketItem newBasketItem = (BasketItem) newItem;
            return oldBasketItem.getName().equals(newBasketItem.getName())
                    && oldBasketItem.isMarked() == newBasketItem.isMarked();
        } else if (oldItem instanceof ShowcaseItem && newItem instanceof ShowcaseItem) {
            ShowcaseItem oldShowcaseItem = (ShowcaseItem) oldItem;
            ShowcaseItem newShowcaseItem = (ShowcaseItem) newItem;
            return oldShowcaseItem.getName().equals(newShowcaseItem.getName())
                    && oldShowcaseItem.isRemoval() == newShowcaseItem.isRemoval()
                    && oldShowcaseItem.isExistInBasket() == newShowcaseItem.isExistInBasket();
        } else if (oldItem instanceof NativeAdWrapper && newItem instanceof NativeAdWrapper) {
            NativeAdWrapper oldAd = (NativeAdWrapper) oldItem;
            NativeAdWrapper newAd = (NativeAdWrapper) newItem;
            return oldAd.getAd().getHeadline().equals(newAd.getAd().getHeadline());
        }
        return false;
    }
}
