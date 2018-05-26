package com.aws.cognitowx.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.aws.cognitowx.MainActivity;
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
        api = WXAPIFactory.createWXAPI(this, WXClient.WX_APP_ID, true);
        api.handleIntent(getIntent(), this);
    }

        @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG, "onReq: ");
    }

    @Override
    public void onResp(BaseResp resp) {
        SendAuth.Resp newResp = (SendAuth.Resp) resp;
        //获取微信传回的code
        String code = "";
        String hint = "";
        int errorCode = resp.errCode;
        switch (errorCode) {
            case BaseResp.ErrCode.ERR_OK:
                //用户同意
                code = newResp.code;
                hint = "allow, code=" + code;
                MainActivity.WX_CODE = code;
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //用户拒绝
                hint = "deny";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
                hint = "cancel";
                break;
            default:
        }
//       Toast.makeText(this, hint, Toast.LENGTH_LONG).show();
        Log.d(TAG, "onResp: "+hint);
    }
}