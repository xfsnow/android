package com.aws.amplify;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    Button btnClick;
    public static PinpointManager pinpointManager;

    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            // Initialize the AWS Mobile Client
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i(TAG, String.valueOf(userStateDetails.getUserState()));
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);
        }
        return pinpointManager;
    }

    /**
     * Call this method to log a custom event to the analytics client.
     */
    public void logEvent(String eventSourceId, Double demoMetric) {
        String eventTime = String.valueOf(new Date().getTime());
        AnalyticsClient analyticsClient = pinpointManager.getAnalyticsClient();
        final AnalyticsEvent event =
                analyticsClient.createEvent("CustomEvent")
                        .withAttribute("EventSource", eventSourceId)
                        .withAttribute("EventTime", eventTime)
                        .withMetric("DemoMetric", demoMetric);
        Log.d(TAG, "logEvent: EventSource="+eventSourceId+", EventTime="+eventTime+", rand="+demoMetric);
        Toast.makeText(this, "logEvent: EventSource="+eventSourceId+", EventTime="+eventTime+", rand="+demoMetric, Toast.LENGTH_SHORT).show();
        analyticsClient.recordEvent(event);
        //调试时为了及时看到事件结果，每次记录都发送
//        pinpointManager.getAnalyticsClient().submitEvents();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        final PinpointManager pinpointManager = getPinpointManager(getApplicationContext());
        pinpointManager.getSessionClient().startSession();


        btnClick = (Button) findViewById(R.id.click);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                logEvent(String.valueOf(btnClick.getId()), Math.random());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pinpointManager.getSessionClient().stopSession();
        pinpointManager.getAnalyticsClient().submitEvents();
    }

}
