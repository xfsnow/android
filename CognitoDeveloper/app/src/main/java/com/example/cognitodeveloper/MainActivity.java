package com.example.cognitodeveloper;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.regions.Regions;
import com.example.cognitodeveloper.model.AuthenticationRequestModel;
import com.example.cognitodeveloper.model.AuthenticationResponseModel;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "cognitodeveloper_Main";
    private static final String IDENTITY_POOL_ID = "us-west-2:6383cf10-d3d7-4ae4-992d-24fb43fc3e4e";
    private static final Regions REGION = Regions.US_WEST_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        java.util.logging.Logger.getLogger("com.amazonaws").setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.FINEST);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
//        DeveloperAuthenticationProvider developerProvider = new DeveloperAuthenticationProvider( null, IDENTITY_POOL_ID, context, REGION);
//        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider( context, developerProvider, REGION);


        AWSCredentialsProvider apiCredentialsProvider = new CognitoCachingCredentialsProvider(context, IDENTITY_POOL_ID, REGION);

        // 使用 ApiClientFactory 工厂方法来生成SDK 的客户端实例。
        ApiClientFactory factory = new ApiClientFactory().credentialsProvider(apiCredentialsProvider);
        final CognitoauthClient client = factory.build(CognitoauthClient.class);
        // 对API接口的调用已经封装成相应的方法，比如 /pets 接口的GET方法，对应 petsGet() 方法。
        // 具体的方法说明可以点击petsGet 方法名上的链接，跳转到PetstoreClient.java中查看源码。
        final AuthenticationRequestModel authRequest = new AuthenticationRequestModel();
        authRequest.setUserName("Dhruv");
        authRequest.setPasswordHash("8743b52063cd84097a65d1633f5c74f5");
//        authRequest.setUserName("Test");
//        authRequest.setPasswordHash("b52063cd84097a65d1633f5c74f58743");
        new Thread(){
            @Override
            public void run() {
                super.run();
                Log.d(TAG, "run");
                AuthenticationResponseModel authResponse = client.loginPost(authRequest);
                Log.d(TAG, "authResponse: "+authResponse.getUserId()+" "+authResponse.getIdentityId()+" "+authResponse.getOpenIdToken());
            }
        }.start();
    }
}
