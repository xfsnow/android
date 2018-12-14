package com.aws.cognitowechat;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.regions.Region;
import com.aws.cognitowechat.wxapi.WXClient;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "WXMA";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        regToWx();

        txtHint = (TextView) findViewById(R.id.hint);
        txtHint.setText("");

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
            Toast.makeText(this, "微信code为" + WX_CODE, Toast.LENGTH_LONG).show();

        }
    }
}
