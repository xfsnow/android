package com.aws.cognitowx;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "WXMA";

    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String WX_APP_ID = "";

    public static final String WX_AUTH_SCOPE = "snsapi_userinfo";
    public static final String WX_AUTH_STATE = "wechat_sdk_demo_test";

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    /**
     * 创建微信API实例
     */
    public void regToWx() {
        // 通过 WXAPIFactory 工厂创建 WXAPI 实例
        api = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
        // 将应用 APP_ID 注册到微信
        api.registerApp(WX_APP_ID);
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
                req.scope = WX_AUTH_SCOPE;
                req.state = WX_AUTH_STATE;
                api.sendReq(req);
                Log.d(TAG, "req: ");
            }
        });

    }
}