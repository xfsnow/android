package com.example.cognitodev;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.regions.Regions;
import com.example.cognitodev.model.AuthenticationRequestModel;
import com.example.cognitodev.model.AuthenticationResponseModel;

/**
 * Created by xuef on 2018-3-28.
 */
public class DeveloperAuthenticationProvider extends AWSAbstractCognitoDeveloperIdentityProvider {
    public static final String TAG = "cogdev_DevAuthProvider";
    
    private static final String developerProvider = "login.company.developer";

    private Context context;

    public DeveloperAuthenticationProvider(String accountId, String identityPoolId, Context context, Regions region) {
        super(accountId, identityPoolId, region);
        this.context = context;

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
        AWSCredentialsProvider apiCredentialsProvider = new CognitoCachingCredentialsProvider(context, MainActivity.COGNITO_POOL_ID, MainActivity.REGION);
        // 使用 ApiClientFactory 工厂方法来生成SDK 的客户端实例。
        ApiClientFactory factory = new ApiClientFactory().credentialsProvider(apiCredentialsProvider);
        CognitoauthClient client = factory.build(CognitoauthClient.class);
        AuthenticationRequestModel authRequest = new AuthenticationRequestModel();
        authRequest.setUserName("Dhruv");
        authRequest.setPasswordHash("8743b52063cd84097a65d1633f5c74f5");
        AuthenticationResponseModel authResponse = client.loginPost(authRequest);
        Log.d(TAG, "refresh: " + authResponse.getUserId() + " " + authResponse.getIdentityId() + " " + authResponse.getOpenIdToken());
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

        AWSCredentialsProvider apiCredentialsProvider = new CognitoCachingCredentialsProvider(context, MainActivity.COGNITO_POOL_ID, MainActivity.REGION);
        // 使用 ApiClientFactory 工厂方法来生成SDK 的客户端实例。
        ApiClientFactory factory = new ApiClientFactory().credentialsProvider(apiCredentialsProvider);
        CognitoauthClient client = factory.build(CognitoauthClient.class);
        AuthenticationRequestModel authRequest = new AuthenticationRequestModel();
        authRequest.setUserName("Dhruv");
        authRequest.setPasswordHash("8743b52063cd84097a65d1633f5c74f5");
        AuthenticationResponseModel authResponse = client.loginPost(authRequest);
        Log.d(TAG, "getIdentityId: " + authResponse.getUserId() + " " + authResponse.getIdentityId() + " " + authResponse.getOpenIdToken());
        identityId = authResponse.getIdentityId();
        return identityId;
    }
}