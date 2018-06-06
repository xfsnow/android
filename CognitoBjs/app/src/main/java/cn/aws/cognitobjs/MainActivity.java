package cn.aws.cognitobjs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;

import java.util.List;

import cn.aws.cognitobjs.model.AuthenticationRequestModel;
import cn.aws.cognitobjs.model.AuthenticationResponseModel;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "cogdev_Main";
    // 请在这里写你的 Cognito Pool Id
    public static final String COGNITO_POOL_ID = "cn-north-1:";
    public static final Regions REGION = Regions.CN_NORTH_1;

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
    private void getAuth(String username, String password) {
        // 使用Cognito进行验证
        AWSCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),  //当前活动的Context
                COGNITO_POOL_ID, // Cognito identity pool id
                REGION
        );
        ApiClientFactory factory = new ApiClientFactory().region(REGION.getName()).credentialsProvider(credentialsProvider);
        CognitoDevClient client = factory.build(CognitoDevClient.class);
        AuthenticationRequestModel authRequest = new AuthenticationRequestModel();
        authRequest.setUserName(username);
        authRequest.setPasswordHash(password);
        AuthenticationResponseModel authResponse = client.loginPost(authRequest);
        Log.d(TAG, "authResponse"+authResponse.getUserId()+" "+authResponse.getIdentityId()+" "+authResponse.getOpenIdToken());

    }

    /**
     * 测试方法
     * 以 CognitoCredentialsProvider 来验证 S3 客户端，演示获取到的权限
     */
    private void getCredential(String username, String password) {
        DeveloperAuthenticationProvider developerProvider = new DeveloperAuthenticationProvider(null, COGNITO_POOL_ID, context, REGION, username, password);
//        HashMap logins = new HashMap<String, String>();
//        logins.put("cognito-identity.amazonaws.com.cn", developerProvider.getToken());
//        developerProvider.setLogins(logins);
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider(developerProvider, REGION);
        String identity = credentialsProvider.getIdentityId();
        Log.d(TAG, "getIdentityId()= " +identity);
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        // BJS 区需要特别指定一下 region　参数
        s3.setRegion(Region.getRegion(REGION));
        List<Bucket> bucketList = s3.listBuckets();
        StringBuilder bucketNameList = new StringBuilder("My S3 buckets are:\n");
        for (Bucket bucket: bucketList) {
            bucketNameList.append(bucket.getName()).append("\n");
        }
        Log.d(TAG, "s3 bucket" +bucketNameList);
    }

    /**
     * 测试方法
     * 以 CognitoCredentialsProvider 来验证 S3 客户端，演示获取到的权限
     */
//    private void getCredentialForIdentity(String username, String password) {
//        Log.d(TAG, "getCredentialForIdentity: ");
//        AWSCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//                getApplicationContext(),  //当前活动的Context
//                COGNITO_POOL_ID, // Cognito identity pool id
//                REGION
//        );
//        ApiClientFactory factory = new ApiClientFactory().region(REGION.getName()).credentialsProvider(credentialsProvider);
//        CognitoDevClient client = factory.build(CognitoDevClient.class);
//        AuthenticationRequestModel authRequest = new AuthenticationRequestModel();
//        authRequest.setUserName(username);
//        authRequest.setPasswordHash(password);
//        AuthenticationResponseModel authResponse = client.loginPost(authRequest);
//        String cognitoIdentityId = authResponse.getIdentityId();
//        String cognitoOpenIdToken = authResponse.getOpenIdToken();
//        Log.d(TAG, "authResponse"+authResponse.getUserId()
//                +" cognitoIdentityId="+cognitoIdentityId
//                +" cognitoOpenIdToken="+cognitoOpenIdToken);
//
//
//        Map<String, String> logins = new HashMap();
////        logins.put("cognito-identity.amazonaws.com.cn", cognitoOpenIdToken);
//        GetCredentialsForIdentityRequest getCredentialsRequest =
//                new GetCredentialsForIdentityRequest()
//                        .withIdentityId(cognitoIdentityId)
//                        .withLogins(logins);
//        AmazonCognitoIdentityClient cognitoIdentityClient =
//                new AmazonCognitoIdentityClient();
//        GetCredentialsForIdentityResult getCredentialsResult =
//                cognitoIdentityClient.getCredentialsForIdentity(getCredentialsRequest);
//        Credentials credentials = getCredentialsResult.getCredentials();
//        Log.d(TAG, "ak= " +credentials.getAccessKeyId() +", sk="+credentials.getSecretKey());
////        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
////        List<Bucket> bucketList = s3.listBuckets();
////        StringBuilder bucketNameList = new StringBuilder("My S3 buckets are:\n");
////        for (Bucket bucket: bucketList) {
////            bucketNameList.append(bucket.getName()).append("\n");
////        }
////        Log.d(TAG, "s3 bucket" +bucketNameList);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        java.util.logging.Logger.getLogger("com.amazonaws").setLevel(java.util.logging.Level.FINEST);
//        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.FINEST);
        this.context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


// 分别测试单个步骤
        new Thread() {
            @Override
            public void run() {
                super.run();
                String username = "user";
                String password = "1234";
//                getAuth(username, password);
                getCredential(username, password);


//                getCredentialForIdentity(username, password);
            }
        }.start();


//        btnLogin = (Button) findViewById(R.id.login);
//        btnCancel = (Button) findViewById(R.id.reset);
//        inputUsername = (EditText) findViewById(R.id.username);
//        inputPassword = (EditText) findViewById(R.id.password);
//
//        txtHint = (TextView) findViewById(R.id.hint);
//        txtHint.setText("");
//
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String username = inputUsername.getText().toString();
//                final String password = inputPassword.getText().toString();
//                if (username.equals("") || password.equals("")) {
//                    txtHint.setText("Please input username and password.");
//                }
//                else {
//                    txtHint.setText("Logging...");
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            super.run();
//                            DeveloperAuthenticationProvider developerProvider = new DeveloperAuthenticationProvider(null, COGNITO_POOL_ID, context, REGION, username, password);
//                            CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider(developerProvider, REGION);
//                            String identity = credentialsProvider.getIdentityId();
//                            if (null == identity) {
//                                setHint(txtHint, "Login failed.");
//                            }
//                            else {
//                                AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
//                                List<Bucket> bucketList = s3.listBuckets();
//                                StringBuilder bucketNameList = new StringBuilder("My S3 buckets are:\n");
//                                for (Bucket bucket : bucketList) {
//                                    bucketNameList.append(bucket.getName()).append("\n");
//                                }
//                                setHint(txtHint, "Login succeeded.\n"+bucketNameList);
//                            }
//                        }
//                    }.start();
//                }
//            }
//        });
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                inputUsername.setText("");
//                inputPassword.setText("");
//            }
//        });
    }
}