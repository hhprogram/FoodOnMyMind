package com.harrison.foodonmymind;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by harrison on 8/22/17.
 */

public class WidgetService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetFactory(this, intent);
    }
}