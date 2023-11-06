package sdk.analysis.avidly.com.avidlyanalysissdk.app;

import android.app.Application;

import com.aly.sdk.ALYAppLifecyleListener;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ALYAppLifecyleListener());
    }
}
