package alektas.pocketbasket.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.R;
import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.data.db.entities.BasketItem;
import alektas.pocketbasket.utils.ResourcesUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class BasketWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BasketWidgetFactory(getApplicationContext(), intent);
    }
}

class BasketWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<WidgetBasketItem> widgetItems;
    private int widgetId;

    BasketWidgetFactory(Context context, Intent intent) {
        mContext = context;
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        widgetItems = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {
        updateItems();
    }

    private void updateItems() {
        widgetItems.clear();
        List<BasketItem> items = mRepository.getBasketData().blockingFirst(new ArrayList<>());
        for (BasketItem item : items) {
            WidgetBasketItem widgetItem = new WidgetBasketItem(item.getName(), item.getImgRes());
            if (item.getName().equals(BasketWidget.REMOVAL_ITEM)) {
                widgetItem.setRemoval(true);
            }
            widgetItems.add(widgetItem);
        }
    }

    @Override
    public void onDestroy() {
        widgetItems.clear();
    }

    @Override
    public int getCount() {
        return widgetItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        WidgetBasketItem item = widgetItems.get(position);
        Bundle options = AppWidgetManager.getInstance(mContext).getAppWidgetOptions(widgetId);
        int widgetWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        RemoteViews itemView = createView(widgetWidth, item);

        setIntent(itemView, item, R.id.widget_item_body, BasketWidget.ACTION_ITEM_TOUCH);
        setIntent(itemView, item, R.id.widget_btn_del, BasketWidget.ACTION_ITEM_DEL);

        return itemView;
    }

    private RemoteViews createView(int widgetWidth, WidgetBasketItem item) {
        if (widgetWidth <= BasketWidget.NARROW_MAX_WIDTH) {
            return createNarrowView(item);
        } else if (widgetWidth <= BasketWidget.MIDDLE_MAX_WIDTH) {
            return createMiddleView(item);
        } else {
            return createWideView(item);
        }
    }

    private RemoteViews createNarrowView(WidgetBasketItem item) {
        RemoteViews itemView = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_narrow);
        itemView.setViewVisibility(R.id.widget_btn_del,
                BasketWidget.REMOVAL_ITEM.equals(item.getName()) ? View.VISIBLE : View.GONE);
        int iconRes = ResourcesUtils.getImgId(item.getIconName());
        itemView.setImageViewResource(R.id.widget_item_icon, iconRes);
        itemView.setTextViewText(R.id.widget_item_text, iconRes == 0 ? item.getName() : "");
        return itemView;
    }

    private RemoteViews createMiddleView(WidgetBasketItem item) {
        RemoteViews itemView = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_mid);
        itemView.setTextViewText(R.id.widget_item_text, item.getName());
        int iconRes = ResourcesUtils.getImgId(item.getIconName());
        if (BasketWidget.REMOVAL_ITEM.equals(item.getName())) {
            itemView.setViewVisibility(R.id.widget_btn_del, View.VISIBLE);
//            itemView.setViewVisibility(iconRes == 0 ? R.id.widget_item_icon : R.id.widget_item_text,
//                    View.GONE);
        } else {
            itemView.setViewVisibility(R.id.widget_btn_del, View.GONE);
//            itemView.setViewVisibility(R.id.widget_item_icon, View.VISIBLE);
//            itemView.setViewVisibility(R.id.widget_item_text, View.VISIBLE);
        }
        if (iconRes == 0) iconRes = R.drawable.ic_launcher_foreground;
        itemView.setImageViewResource(R.id.widget_item_icon, iconRes);
        return itemView;
    }

    private RemoteViews createWideView(WidgetBasketItem item) {
        RemoteViews itemView = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_wide);
        itemView.setTextViewText(R.id.widget_item_text, item.getName());
        itemView.setViewVisibility(R.id.widget_btn_del,
                BasketWidget.REMOVAL_ITEM.equals(item.getName()) ? View.VISIBLE : View.GONE);
        int iconRes = ResourcesUtils.getImgId(item.getIconName());
        if (iconRes == 0) iconRes = R.drawable.ic_launcher_foreground;
        itemView.setImageViewResource(R.id.widget_item_icon, iconRes);
        return itemView;
    }

    // Set item info intent that provided for the item touch pending intent
    private void setIntent(RemoteViews itemView, WidgetBasketItem item, int viewId, String action) {
        Bundle extras = new Bundle();
        extras.putString(BasketWidget.EXTRA_ITEM_NAME, item.getName());
        extras.putString(BasketWidget.EXTRA_ACTION, action);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        itemView.setOnClickFillInIntent(viewId, fillInIntent);
    }

    @Override
    public RemoteViews getLoadingView() {
        Bundle options = AppWidgetManager.getInstance(mContext).getAppWidgetOptions(widgetId);
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        RemoteViews itemView;
        if (minWidth <= BasketWidget.NARROW_MAX_WIDTH) {
            itemView = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_narrow);
        } else {
            itemView = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_wide);
            itemView.setTextViewText(R.id.widget_item_text, mContext.getString(R.string.widget_loading_long));
        }

        return itemView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return widgetItems.get(0).getName().hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}

