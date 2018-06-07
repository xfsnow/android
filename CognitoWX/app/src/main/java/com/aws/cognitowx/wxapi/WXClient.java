package com.aws.cognitowx.wxapi;

import java.util.HashMap;
import java.util.Map;

public class WXClient {

    public static final String TAG = "WXC";

    // 以下常量替换为你的应用从微信开放平台申请到的应用 ID 和密钥
    public static final String WX_APP_ID = "";
    public static final String WX_APP_SECRET = "";

    public static final String WX_AUTH_SCOPE = "snsapi_userinfo";
    public static final String WX_AUTH_STATE = "wechat_sdk_demo_test";

    public static final String WX_GRANT_TYPE = "authorization_code";
}
