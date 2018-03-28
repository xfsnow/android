package com.example.cognitodev;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    Button btnLogin, btnCancel;
    EditText inputUsername, inputPassword;

    TextView txtHint;

     public void setHint(final TextView txt, final String hint) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt.setText(hint);
            }
        });
    }

    /**
     * 测试方法
     * 调用 API Gateway
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
     * 测试方法
     * 以 CognitoCredentialsProvider 来验证 S3 客户端，演示获取到的权限
     */
    private void getCredential() {
        String username = "Dhruv";
        String password = "8743b52063cd84097a65d1633f5c74f5";
        DeveloperAuthenticationProvider developerProvider = new DeveloperAuthenticationProvider(null, COGNITO_POOL_ID, context, REGION, username, password);
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

        btnLogin = (Button) findViewById(R.id.login);
        btnCancel = (Button) findViewById(R.id.reset);
        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);

        txtHint = (TextView) findViewById(R.id.hint);
        txtHint.setText("");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = inputUsername.getText().toString();
                final String password = inputPassword.getText().toString();
                if (!username.equals("") && !password.equals("")) {
                    txtHint.setText("Logging...");
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            DeveloperAuthenticationProvider developerProvider = new DeveloperAuthenticationProvider(null, COGNITO_POOL_ID, context, REGION, username, password);
                            CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider(developerProvider, REGION);
                            String identity = credentialsProvider.getIdentityId();
                            if (null == identity) {
                                setHint(txtHint, "Login failed.");
                            }
                            else {
                                AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                                List<Bucket> bucketList = s3.listBuckets();
                                StringBuilder bucketNameList = new StringBuilder("My S3 buckets are:\n");
                                for (Bucket bucket : bucketList) {
                                    bucketNameList.append(bucket.getName()).append("\n");
                                }
                                setHint(txtHint, "Login succeeded.\n"+bucketNameList);
                            }
                        }
                    }.start();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputUsername.setText("");
                inputPassword.setText("");
            }
        });

// 分别测试单个步骤
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                getAuth();
//                getCredential();
//            }
//        }.start();
    }
}