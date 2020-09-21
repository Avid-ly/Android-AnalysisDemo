package sdk.analysis.avidly.com.avidlyanalysissdk;

import android.app.Application;

/**
 * Created by sam on 2017/6/2.
 */

public class MyApp extends Application {
    public static Application sApp;
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }
}
