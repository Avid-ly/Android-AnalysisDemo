package sdk.analysis.avidly.com.avidlyanalysissdk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aly.account.ALYLogin;
import com.aly.analysis.basicdata.conversion.AFConversionDataResultListener;
import com.aly.analysis.basicdata.payuserlayer.PayUserLayerDataListener;
import com.aly.duration.DurationReport;
import com.aly.sdk.ALYAnalysis;
import com.aly.zflog.ZFLogReport;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    //private AvidlyPlayableInterstitialAd playILAd;
    private static final String AF_DEV_KEY = "fZvuk792H9hJQKmaTwuXxA";
    public static final String TAG = "aly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ALYAnalysis.enalbeDebugMode(true);
        ALYAnalysis.init(getApplicationContext(), "999999", "32408");
        DurationReport.initReport("uid001");
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    Log.d(TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
                conversionData.put("Campaign", "600107_DW1_GP_L7_0410_WW_VO_Casino/Gambling");
                ALYAnalysis.getConversionData(conversionData, new AFConversionDataResultListener() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i(TAG, "onSuccess: " + s);
                    }

                    @Override
                    public void onFail(String s) {
                        Log.i(TAG, "onFail: " + s);
                    }
                });
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.d(TAG, "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> conversionData) {

                for (String attrName : conversionData.keySet()) {
                    Log.d(TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                }

            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.d(TAG, "error onAttributionFailure : " + errorMessage);
            }
        };
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(getApplication());

        Button button = (Button) findViewById(R.id.btn_log);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                huaWeiLogin();
            }
        });


        button = (Button) findViewById(R.id.btn_logpay);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap map = new HashMap();
                map.put("key1", "value1");
                map.put("key2", "value2");
                ZFLogReport.logReportWithServerAndExtraMap("imaccountid", "imaccountserver", "impurchasejson", "impurchasesignature", map);
                ZFLogReport.logReportWithExtraMap("imaccountid", "impurchasejson", "impurchasesignature", map);
            }
        });


    }



    private void huaWeiLogin() {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                ALYLogin.huaWeiLogin("101051233",
                        "890852100000001660",
                        "5623509",
                        "1181552208101434",
                        "gG1zuh9ewsztd5dXam+sNmgiGnqqzRirYw+Q0FA8FP6EqBs5BPJCYBQIQHhexemsDxZW\\/1ODrfBGD0M0gd77UciQ8moFMJfAQaWmdwzoP44Ii6TMwElsD7r8HfthdPQZ7bBo3yYThmLRwAA0hEy0QejxXYR170LlsharWyK5zkW50s9rjhOX4WrBMYxKkXXje8qyGT5e9qconxBUHFHeRgANZ6x0oepSwYwqC3xCPUH5fNWiEqXEW5t0MdwJpTDFUF144ZU9w5UwcSiwbJNgK+zxKmqlP\\/9wOS9kPSxWO2mHPmxLvxpxpTsMm4IKy4+obDevEXWU3GE\\/PXZ+GhK0Xw==",
                        "1",
                        "1569304494696",
                        "");
            }
        })).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        DurationReport.onAppResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DurationReport.onAppPause();
    }

    public void login(View view) {
        ALYLogin.loginWithAASDK("facebook","player001","ggid1111111","logintoken",null);

    }


    public void getPayUserLayer(View view) {
        ALYAnalysis.getPayUserLayerData(new PayUserLayerDataListener() {
            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "onSuccess: PayUserLayer :"+s);
            }

            @Override
            public void onFail(String s) {
                Log.i(TAG, "onFail: PayUserLayer :"+s);
            }
        });
    }
}
