package alektas.pocketbasket.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import alektas.pocketbasket.R;
import alektas.pocketbasket.data.RepositoryImpl;
import alektas.pocketbasket.domain.usecases.CleanBasketUseCase;
import alektas.pocketbasket.domain.usecases.RemoveItemFromBasket;
import alektas.pocketbasket.ui.MainActivity;

public class BasketWidget extends AppWidgetProvider {
    public static final String ACTION_UPDATE_ITEMS = "alektas.pocketbasket.action.ACTION_UPDATE_ITEMS";
    public static final String ACTION_CLEAN_BASKET = "alektas.pocketbasket.action.ACTION_CLEAN_BASKET";
    public static final String ACTION_ITEM_TOUCH = "alektas.pocketbasket.action.ACTION_ITEM_TOUCH";
    public static final String ACTION_ITEM_DEL = "alektas.pocketbasket.action.ACTION_ITEM_DEL";
    public static final String EXTRA_ITEM_NAME = "alektas.pocketbasket.extra.EXTRA_ITEM_NAME";
    public static final String EXTRA_ACTION = "alektas.pocketbasket.extra.EXTRA_ACTION";
    // If width of the widget is lesser than that, make a narrow widget version
    public static final int NARROW_MAX_WIDTH = 96;
    public static final int MIDDLE_MAX_WIDTH = 150;
    public static String REMOVAL_ITEM = "";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);
            updateWidget(widgetId, context, appWidgetManager, options);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        switch (action) {
            // Handle basket widget updating on app stop
            case ACTION_UPDATE_ITEMS:
                notifyWidgets(context);
                break;
            case ACTION_CLEAN_BASKET:
                new CleanBasketUseCase(RepositoryImpl.getInstance(context))
                        .execute(null, successfully -> notifyWidgets(context));
                break;
            case ACTION_ITEM_TOUCH:
                handleClick(context, intent.getExtras());
                break;
        }

        super.onReceive(context, intent);
    }

    // Handle on item click action (check item, remove item, etc)
    private void handleClick(Context context, Bundle bundle) {
        if (bundle == null) return;
        String itemName = bundle.getString(EXTRA_ITEM_NAME);
        String action = bundle.getString(EXTRA_ACTION);
        if (TextUtils.isEmpty(action) && TextUtils.isEmpty(itemName)) return;
        switch (action) {
            case ACTION_ITEM_DEL:
                new RemoveItemFromBasket(RepositoryImpl.getInstance(context), true)
                        .execute(itemName, successfully -> {
                            notifyWidgets(context);
                            String prefix = context.getResources().getString(R.string.widget_item_removed);
                            Toast.makeText(context, prefix + itemName, Toast.LENGTH_SHORT).show();
                            REMOVAL_ITEM = "";
                        });
                break;
            case ACTION_ITEM_TOUCH:
                REMOVAL_ITEM = itemName.equals(REMOVAL_ITEM) ? "" : itemName;
                notifyWidgets(context);
                break;
        }
    }

    public static void updateItems(Context context) {
        Intent intent = new Intent(BasketWidget.ACTION_UPDATE_ITEMS);
        intent.setComponent(new ComponentName(context, BasketWidget.class));
        context.sendBroadcast(intent);
    }

    // Notify all widgets that a data set is changed
    private void notifyWidgets(Context context) {
        ComponentName provider = new ComponentName(context, BasketWidget.class);
        int[] widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(provider);
        if (widgetIds != null && widgetIds.length > 0) {
            AppWidgetManager.getInstance(context)
                    .notifyAppWidgetViewDataChanged(widgetIds, R.id.widget_list);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int widgetId, Bundle newOptions) {
        // Handle widget resize
        updateWidget(widgetId, context, appWidgetManager, newOptions);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, widgetId, newOptions);
    }

    private void updateWidget(int widgetId, Context context,
                              AppWidgetManager appWidgetManager, Bundle options) {
        // Create widget view
        int layoutId = getLayoutId(options);
        RemoteViews widget = new RemoteViews(context.getPackageName(), layoutId);
        widget.setEmptyView(R.id.widget_list, R.id.widget_empty_view);

        // Intent for opening the app by click on header
        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent openPending =
                PendingIntent.getActivity(context, 0, openIntent, 0);
        widget.setOnClickPendingIntent(R.id.widget_header, openPending);

        // Intent for cleaning the basket by click on del button in header
        PendingIntent cleanPending = createBroadcastIntent(context, widgetId, ACTION_CLEAN_BASKET);
        widget.setOnClickPendingIntent(R.id.widget_header_btn_del, cleanPending);

        // Intent for broadcasting action event on item touch by user
        PendingIntent touchPending = createBroadcastIntent(context, widgetId, ACTION_ITEM_TOUCH);
        widget.setPendingIntentTemplate(R.id.widget_list, touchPending);

        // Intent for providing item views to the collection
        Intent dataIntent = new Intent(context, BasketWidgetService.class);
        dataIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        dataIntent.setData(Uri.parse(dataIntent.toUri(Intent.URI_INTENT_SCHEME)));
        widget.setRemoteAdapter(R.id.widget_list, dataIntent);

        appWidgetManager.updateAppWidget(widgetId, widget);
    }

    private int getLayoutId(Bundle options) {
        int widgetWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        if (widgetWidth <= NARROW_MAX_WIDTH) {
            return R.layout.widget_basket_narrow;
        } else if (widgetWidth <= MIDDLE_MAX_WIDTH) {
            return R.layout.widget_basket_mid;
        } else {
            return R.layout.widget_basket_wide;
        }
    }

    private PendingIntent createBroadcastIntent(Context context, int widgetId, String action) {
        Intent touchIntent = new Intent(context, BasketWidget.class);
        touchIntent.setAction(action);
        touchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        touchIntent.setData(Uri.parse(touchIntent.toUri(Intent.URI_INTENT_SCHEME)));
        return PendingIntent.getBroadcast( context, 0, touchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
