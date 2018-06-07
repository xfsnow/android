package com.aws.cognitowx;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.aws.cognitowx.wxapi.WXClient;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.aws.cognitowx.model.AuthenticationRequestModel;
import com.aws.cognitowx.model.AuthenticationResponseModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "WXMA";
//    请把你的 Cognito Pool Id 写在这里
    public static final String COGNITO_POOL_ID = "cn-north-1:";
    public static final Regions REGION = Regions.CN_NORTH_1;

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    private static boolean isWXLogin = false;
    public static String WX_CODE = "";

    TextView txtHint;

    Context context;

    public void setHint(final TextView txt, final String hint) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt.setText(hint);
            }
        });
    }

    /**
     * 创建微信API实例
     */
    public void regToWx() {
        // 通过 WXAPIFactory 工厂创建 WXAPI 实例
        api = WXAPIFactory.createWXAPI(this, WXClient.WX_APP_ID, true);
        // 将应用 APP_ID 注册到微信
        api.registerApp(WXClient.WX_APP_ID);
    }


    private void getWxUser() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WXClient.WX_APP_ID + "&secret=" + WXClient.WX_APP_SECRET + "&code=" + WX_CODE + "&grant_type="+WXClient.WX_GRANT_TYPE;
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(accessTokenUrl).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    Log.d(TAG, "json: " + jsonObject.toString());
                    String access_token = jsonObject.getString("access_token");
                    String openid = jsonObject.getString("openid");
                    String refresh_token = jsonObject.getString("refresh_token");
                    Log.d(TAG, "access_token= " + access_token + ", openid= " + openid + ", refresh_token= " + refresh_token);


                    String userUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid;
                    Request requestUser = new Request.Builder().url(userUrl).build();
                    Response responseUser = client.newCall(requestUser).execute();
                    String responseDataUser = responseUser.body().string();
                    JSONObject jsonUser = new JSONObject(responseDataUser);
                    Log.d(TAG, "jsonUser: " + jsonUser.toString());
                    String nickname = jsonUser.getString("nickname");
                    String headimgurl = jsonUser.getString("headimgurl");
                    Log.d(TAG, "nickname= " + nickname + ", headimgurl= " + headimgurl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 测试方法
     * 调用 API Gateway
     */
    private void getAuth(String code) {
        // 使用Cognito进行验证
        AWSCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),  //当前活动的Context
                COGNITO_POOL_ID, // Cognito identity pool id
                REGION
        );
        ApiClientFactory factory = new ApiClientFactory().region(REGION.getName()).credentialsProvider(credentialsProvider);
        CognitoWXClient client = factory.build(CognitoWXClient.class);

        AuthenticationRequestModel authRequest = new AuthenticationRequestModel();
        authRequest.setCode(code);
        AuthenticationResponseModel authResponse = client.loginwxPost(authRequest);
        Log.d(TAG, "CODE="+code+", authResponse="+authResponse.getUserId()+" "+authResponse.getIdentityId()+" "+authResponse.getOpenIdToken());
    }

    /**
     * 测试方法
     * 以 CognitoCredentialsProvider 来验证 S3 客户端，演示获取到的权限
     */
    private void getCredential(String code) {
        DeveloperAuthenticationProvider developerProvider = new DeveloperAuthenticationProvider(null, COGNITO_POOL_ID, getApplicationContext(), REGION, code);
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider(developerProvider, REGION);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        regToWx();

        txtHint = (TextView) findViewById(R.id.hint);
        txtHint.setText("");
//        java.util.logging.Logger.getLogger("com.amazonaws").setLevel(java.util.logging.Level.FINEST);
//        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.FINEST);

        // 发送简单文本，测试微信API可用
//        String text = "test";
//        WXTextObject textObject = new WXTextObject();
//        textObject.text = text;
//
//        WXMediaMessage msg = new WXMediaMessage();
//        msg.mediaObject = textObject;
//        msg.description = text;
//
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        // transaction 属性用于唯一标识一个请求
//        req.transaction = String.valueOf(System.currentTimeMillis());
//        req.message = msg;
//        int mTargetScene = SendMessageToWX.Req.WXSceneSession;
//        req.scene = mTargetScene;
//
//        api.sendReq(req);

        // 分别测试单个步骤
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
////                getAuth(WX_CODE);
//                getCredential(WX_CODE);
//            }
//        }.start();

        // send to weixin
        findViewById(R.id.login_wx_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                // send oauth request
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = WXClient.WX_AUTH_SCOPE;
                req.state = WXClient.WX_AUTH_STATE;
                api.sendReq(req);
                isWXLogin = true;
                Log.d(TAG, "req: ");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isWXLogin) {
//            Toast.makeText(this, "微信code为" + WX_CODE, Toast.LENGTH_LONG).show();
//            getWxUser();
            txtHint.setText("Logging...");
            new Thread() {
                @Override
                public void run() {
                    super.run();
//                    getAuth(WX_CODE);
//                getCredential(WX_CODE);
                DeveloperAuthenticationProvider developerProvider = new DeveloperAuthenticationProvider
                        (null, COGNITO_POOL_ID, getApplicationContext(), REGION, WX_CODE);
                    CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider(developerProvider, REGION);
                    AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                    // BJS 区需要特别指定一下 region　参数
                    s3.setRegion(Region.getRegion(REGION));
                    List<Bucket> bucketList = s3.listBuckets();
                    StringBuilder bucketNameList = new StringBuilder("My S3 buckets are:\n");
                    for (Bucket bucket: bucketList) {
                        bucketNameList.append(bucket.getName()).append("\n");
                    }
                    setHint(txtHint, "Login succeeded.\n"+bucketNameList);
                }
            }.start();
        }
    }

}