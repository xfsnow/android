package com.aws.cognitowx.wxapi;

import android.content.Context;
import android.content.Intent;

import com.aws.cognitowx.MainActivity;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXClient {

    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String WX_APP_ID = "";

    public static final String WX_AUTH_SCOPE = "snsapi_userinfo";
    public static final String WX_AUTH_STATE = "wechat_sdk_demo_test";

}
