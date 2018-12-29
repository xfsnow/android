package com.aws.cognitowechat;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.regions.Regions;
import com.aws.cognitowechat.model.AuthenticationRequestModel;
import com.aws.cognitowechat.model.AuthenticationResponseModel;

/**
 * 开发人员验证提供商
 */
public class DeveloperAuthenticationProvider extends AWSAbstractCognitoDeveloperIdentityProvider {

    public static final String TAG = "DAP";
    
    private static final String developerProvider = "cn.aws.cognitowx";

    private CognitoWechatClient apiClient;
    private String code;

    public DeveloperAuthenticationProvider(String accountId, String identityPoolId, Context context, Regions region, String code) {
        super(accountId, identityPoolId, region);
        // 把调用 API Gateway 相关的客户端初始化出来
        AWSCredentialsProvider apiCredentialsProvider = new CognitoCachingCredentialsProvider(context, identityPoolId, region);
        // 使用中国的区域时 ApiClientFactory 不能自动识别出区域来，需要自己再用 region() 方法指定一下
        ApiClientFactory factory = new ApiClientFactory().region(MainActivity.REGION.getName()).credentialsProvider(apiCredentialsProvider);
        Log.d(TAG, "DeveloperAuthenticationProvider: code="+code);
        this.apiClient = factory.build(CognitoWechatClient.class);
        this.code = code;
    }

    // Return the developer provider name which you choose while setting up the
    // identity pool in the &COG; Console
    @Override
    public String getProviderName() {
        return developerProvider;
    }

    // Use the refresh method to communicate with your backend to get an
    // identityId and token.

    @Override
    public String refresh() {
        // Override the existing token
        setToken(null);

        // Get the identityId and token by making a call to your backend
        // (Call to your backend)

        // Call the update method with updated identityId and token to make sure
        // these are ready to be used from Credentials Provider.
        AuthenticationRequestModel authRequest = new AuthenticationRequestModel();
        authRequest.setCode(code);
        AuthenticationResponseModel authResponse = apiClient.loginwechatPost(authRequest);
        Log.d(TAG, "refresh: userid=" + authResponse.getUserId() + " IdentityId= " + authResponse.getIdentityId() + " OpenIdToken=" + authResponse.getOpenIdToken());
        identityId = authResponse.getIdentityId();
        String token = authResponse.getOpenIdToken();

        update(identityId, token);
        return token;

    }

    // If the app has a valid identityId return it, otherwise get a valid
    // identityId from your backend.

    @Override
    public String getIdentityId() {
        // Load the identityId from the cache
//        identityId = cachedIdentityId;

//        if (identityId == null) {
//            // Call to your backend
//        } else {
//            return identityId;
//        }
//        AuthenticationRequestModel authRequest = new AuthenticationRequestModel();
//        authRequest.setCode(code);
//        AuthenticationResponseModel authResponse = apiClient.loginwxPost(authRequest);
//        Log.d(TAG, "getIdentityId: userid=" + authResponse.getUserId() + " IdentityId= " + authResponse.getIdentityId() + " OpenIdToken=" + authResponse.getOpenIdToken());
//        identityId = authResponse.getIdentityId();
        return identityId;
    }
}