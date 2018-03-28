package com.example.cognitodev;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.example.cognitodev.model.AuthenticationRequestModel;
import com.example.cognitodev.model.AuthenticationResponseModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "cogdev_Main";
    public static final String COGNITO_POOL_ID = "";
    public static final Regions REGION = Regions.US_WEST_2;

    Context context;

    /**
     * 测试调用 API Gateway 的方法
     */
    private void getAuth() {
        // 使用Cognito进行验证
        AWSCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),  //当前活动的Context
                COGNITO_POOL_ID, // Cognito identity pool id
                REGION
        );
        ApiClientFactory factory = new ApiClientFactory().credentialsProvider(credentialsProvider);
        CognitoauthClient client = factory.build(CognitoauthClient.class);
        AuthenticationRequestModel authRequest = new AuthenticationRequestModel();
        authRequest.setUserName("Dhruv");
        authRequest.setPasswordHash("8743b52063cd84097a65d1633f5c74f5");
        AuthenticationResponseModel authResponse = client.loginPost(authRequest);
        Log.d(TAG, "authResponse"+authResponse.getUserId()+" "+authResponse.getIdentityId()+" "+authResponse.getOpenIdToken());

    }

    /**
     * 以 CognitoCredentialsProvider 来验证 S3 客户端，演示获取到的权限
     */
    private void getCredential() {
        DeveloperAuthenticationProvider developerProvider = new DeveloperAuthenticationProvider(null, COGNITO_POOL_ID, context, REGION);
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider(developerProvider, REGION);
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        List<Bucket> bucketList = s3.listBuckets();
        StringBuilder bucketNameList = new StringBuilder("My S3 buckets are:\n");
        for (Bucket bucket: bucketList) {
            bucketNameList.append(bucket.getName()).append("\n");
        }
        Log.d(TAG, "s3 bucket" +bucketNameList);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread() {
            @Override
            public void run() {
                super.run();
//                getAuth();
                getCredential();

            }
        }.start();
    }
}
