package com.aws.cognitowx.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aws.cognitowx.MainActivity;
import com.aws.cognitowx.R;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

    public static final String TAG = "WXEA";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通过 WXAPIFactory 工厂创建 WXAPI 实例
        api = WXAPIFactory.createWXAPI(this, MainActivity.WX_APP_ID, true);
        api.handleIntent(getIntent(), this);
    }

        @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG, "onReq: ");
    }

    @Override
    public void onResp(BaseResp resp) {

        if(resp instanceof SendAuth.Resp){
            SendAuth.Resp newResp = (SendAuth.Resp) resp;

            //获取微信传回的code
            String code = newResp.code;
            Toast.makeText(this, "code="+code, Toast.LENGTH_LONG).show();
//            Toast.makeText(this, resp.errStr, Toast.LENGTH_LONG).show();
        }
    }

}

//public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
//    public static final String TAG = "WXEA";
//
//    // APP_ID 替换为你的应用从官方网站申请到的合法appId
//    public static final String WX_APP_ID = "wx8d52df84e93e18b3";
//
//    public static final String WX_AUTH_SCOPE = "snsapi_userinfo";
//    public static final String WX_AUTH_STATE = "wechat_sdk_demo_test";
//
//    // IWXAPI 是第三方app和微信通信的openapi接口
//    private IWXAPI api;
//
//    /**
//     * 创建微信API实例
//     */
//    private void regToWx() {
//        // 通过 WXAPIFactory 工厂创建 WXAPI 实例
//        api = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
//        // 将应用 APP_ID 注册到微信
//        api.registerApp(WX_APP_ID);
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        regToWx();
//        // send to weixin
//        findViewById(R.id.login_wx_btn).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                // send oauth request
//                SendAuth.Req req = new SendAuth.Req();
//                req.scope = WX_AUTH_SCOPE;
//                req.state = WX_AUTH_STATE;
//                api.sendReq(req);
//                Log.d(TAG, "onClick: ");
//            }
//        });
//    }
//
//    @Override
//    public void onReq(BaseReq baseReq) {
//        Log.d(TAG, "onReq: ");
//    }
//
//    @Override
//    public void onResp(BaseResp resp) {
//        Log.d(TAG, "onResp: ");
//        int errorCode = resp.errCode;
//        String code="";
//        switch (errorCode) {
//            case BaseResp.ErrCode.ERR_OK:
//                //用户同意
//                code = ((SendAuth.Resp) resp).code;
//                break;
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                //用户拒绝
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                //用户取消
//                break;
//            default:
//                break;
//        }
//        Toast.makeText(this, code, Toast.LENGTH_LONG).show();
//        Toast.makeText(this, resp.errStr, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        handleIntent(intent);
//    }
//
//    private void handleIntent(Intent intent) {
//        SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
//        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
//            //用户同意
//        }
//    }
//
//}
