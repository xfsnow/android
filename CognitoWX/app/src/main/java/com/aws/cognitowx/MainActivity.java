package com.aws.cognitowx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aws.cognitowx.wxapi.WXClient;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "WXMA";

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    /**
     * 创建微信API实例
     */
    public void regToWx() {
        // 通过 WXAPIFactory 工厂创建 WXAPI 实例
        api = WXAPIFactory.createWXAPI(this, WXClient.WX_APP_ID, true);
        // 将应用 APP_ID 注册到微信
        api.registerApp(WXClient.WX_APP_ID);
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
                Log.d(TAG, "req: ");
            }
        });

    }
}