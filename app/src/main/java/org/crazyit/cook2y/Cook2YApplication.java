package org.crazyit.cook2y;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.litepal.LitePalApplication;

/**
 * Created by chenti on 2016/4/12.
 */
public class Cook2YApplication extends LitePalApplication {

    public static Context AppContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = getApplicationContext();
        Fresco.initialize(AppContext);
    }
}
