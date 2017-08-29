package com.harrison.foodonmymind;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import static android.content.ContentValues.TAG;

/**
 * Implementation of App Widget functionality.
 */
public class FoodWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
//        this is setting the base layout for the widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.food_widget);
//        We then take that views base layout to an adapter to fit the R.ID.WIDGET_LIST (which
//        is some sort of view compabitible with adapters (this case a list view) and then you
//        populate that listview with data retrieved from the INTENT created in the second arg
//        this intent is source from the context that is fed into the onUpdate method and then the
//        destination is the WidgetService class created by us which is a service that creates
//        a widgetDataProvider object which actually retrieves the data to be shown in our widget
        views.setRemoteAdapter(R.id.appwidget_list, new Intent(context, WidgetService.class));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

//    method called when the widget is first created / whenever the widget receives updated data
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

//    need to override the onReceive() method. It is called when an intent is sent (one of the
//    intent filters listed on the WidgetProvider section of the Manifest) Then in this method
//    we have logic to take certain actions depending what intent was received. Then have the
//    appWidgetManager call the below given method and then this will make the widget that received
//    this call its onDataSetChanged method and correctly update the widget
//    appWidgetManager.notifyAppWidgetViewDataChanged. Calling this method then well call the
//    WidgetDatProvider class' onDataSetChanged() method which in my case will update the whole
//    widget list view for the latest in the shared preferences so any changes in the DB of tickers
//    or whether display mode is % or $ will be updated once you view the widget again. Does the
//    actual onDataSetChange() method call only once I exit the application and can see the widget
//    in view EXTRA NOTE: need to override this even if we are just sending a normal action of
//    AppWidgetManager.ACTION_APPWIDGET_UPDATE because need to tell the WidgetProvider what to do
//    once it receives an update action. In this case want to tell the WidgetProvider that the
//    underlying data has changed so that the WidgetFactory objects onDataSetChanged() method is
//    called and therefore a cursor query will be executed

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive: ");
        int[] ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(ids,
                R.id.appwidget_list);
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

