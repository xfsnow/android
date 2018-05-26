package com.aws.cognitowx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.aws.cognitowx.wxapi.WXClient;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "WXMA";

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    private static boolean isWXLogin = false;
    public static String WX_CODE = "";

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
                    String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WXClient.WX_APP_ID + "&secret=" + WXClient.WX_APP_SECRET + "&code=" + WX_CODE + "&grant_type=authorization_code";
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        regToWx();

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
            Toast.makeText(this, "微信code为"+WX_CODE, Toast.LENGTH_LONG).show();
            getWxUser();
        }
    }


}