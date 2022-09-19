package sdk.analysis.avidly.com.avidlyanalysissdk;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aly.account.ALYLogin;
import com.aly.analysis.basicdata.conversion.AFConversionDataResultListener;
import com.aly.analysis.basicdata.payuserlayer.PayUserLayerDataListener;
import com.aly.duration.DurationReport;
import com.aly.sdk.ALYAnalysis;
import com.aly.zflog.ZFLogReport;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    //private AvidlyPlayableInterstitialAd playILAd;
    private static final String AF_DEV_KEY = "fZvuk792H9hJQKmaTwuXxA";
    public static final String TAG = "tasdk_demo";
    private BillingClient billingClient;
    private String skuId = "Your goods id in google play console";
    private String productId = "600284";
    private TextView txtCont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtCont = findViewById(R.id.txtContent);
        // TODO:上线前请记得设置为false
        ALYAnalysis.enalbeDebugMode(true);
        ALYAnalysis.init(getApplicationContext(), productId, "32408", new ALYAnalysis.TasdkinitializdListener() {
            @Override
            public void onSuccess(final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtCont.setText("init onSuccess userId is: " + s);
                    }
                });
                DurationReport.initReport("uid001");
                initGoogleBilling();
                String openId = ALYAnalysis.getOpenId(MainActivity.this);
                //设置openId
                AppsFlyerLib.getInstance().setCustomerUserId("openId000001");
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
                AppsFlyerLib.getInstance().setDebugLog(true);
                AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, getApplicationContext());
                AppsFlyerLib.getInstance().start(getApplicationContext(), AF_DEV_KEY, new AppsFlyerRequestListener() {
                    @Override
                    public void onSuccess() {
                        // 设置AFID
                        ALYAnalysis.setAFId(AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()));
                    }

                    @Override
                    public void onError(int i, @NonNull String s) {

                    }
                });
            }


            @Override
            public void onFail(String s) {
                txtCont.setText("init fail " + s);
            }
        });


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
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
                int responseCode = billingClient.launchBillingFlow(MainActivity.this
                        , billingFlowParams).getResponseCode();

            }
        });


    }

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    ZFLogReport.logReport("user001", purchase.getOriginalJson(), purchase.getSignature());
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                logAndToast("onPurchasesUpdated USER_CANCELED");
            } else {
                // Handle any other error codes.
                logAndToast("onPurchasesUpdated NOK " + billingResult.getResponseCode() + " msg " + billingResult.getDebugMessage());
            }
        }
    };
    private SkuDetails skuDetails;

    /**
     * 初始化google pay
     */
    private void initGoogleBilling() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    logAndToast("onBillingSetupFinished " + billingResult.getResponseCode());
                    getGoodsSkuDetail();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                logAndToast("onBillingServiceDisconnected ");
            }
        });
    }

    private void getGoodsSkuDetail() {
        List<String> skuList = new ArrayList<>();
        skuList.add(skuId);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        logAndToast("onSkuDetailsResponse  billingResult is " + billingResult.getResponseCode() + " msg " + billingResult.getDebugMessage() + " details " + skuDetailsList.toString());
                        if (skuDetailsList != null && skuDetailsList.size() > 0) {
                            skuDetails = skuDetailsList.get(0);
                        }
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

        ALYLogin.guestLogin("player0000001");
//        ALYLogin.loginWithAASDK("facebook","player001","ggid1111111","logintoken",null);
        Log.d(TAG, "login: ");

    }


    public void getPayUserLayer(View view) {
        ALYAnalysis.getPayUserLayerData(new PayUserLayerDataListener() {
            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "onSuccess: PayUserLayer :" + s);
            }

            @Override
            public void onFail(String s) {
                Log.i(TAG, "onFail: PayUserLayer :" + s);
            }
        });
    }


    private void logAndToast(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
                Log.i(TAG, content);
            }
        });
    }
}
