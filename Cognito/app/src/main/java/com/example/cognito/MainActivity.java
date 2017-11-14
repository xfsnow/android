package com.example.cognito;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.api.authorization.AuthCancellation;
import com.amazon.identity.auth.device.api.authorization.AuthorizationManager;
import com.amazon.identity.auth.device.api.authorization.AuthorizeListener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeRequest;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;
import com.amazon.identity.auth.device.api.authorization.ProfileScope;
import com.amazon.identity.auth.device.api.authorization.Scope;
import com.amazon.identity.auth.device.api.authorization.User;
import com.amazon.identity.auth.device.api.workflow.RequestContext;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CognitoLwA";
    private static final Regions MY_REGION = Regions.CN_NORTH_1;
    private String identityPoolId;
    private CognitoCachingCredentialsProvider credentialsProvider;

    private RequestContext requestContext;
    private TextView mIamText;
    private View mLoginButton;
    private View mLogoutButton;

    private void fetchUserProfile() {
        User.fetch(this, new Listener<User, AuthError>() {
            /* fetch completed successfully. */
            @Override
            public void onSuccess(User user) {
                final String name = user.getUserName();
                final String email = user.getUserEmail();
                final String accountId = user.getUserId();
                final String zipCode = user.getUserPostalCode();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder profileBuilder = new StringBuilder("Profile Response: ");
                        profileBuilder.append(String.format("Welcome, %s!\n", name));
                        profileBuilder.append(String.format("Your Account Id is %s\n", accountId));
                        profileBuilder.append(String.format("Your email is %s\n", email));
                        profileBuilder.append(String.format("Your zipCode is %s\n", zipCode));
                        String profile = profileBuilder.toString();
                        Toast.makeText(MainActivity.this, profile, Toast.LENGTH_LONG).show();
                        setLoggedInState();
                    }
                });
            }
            /* There was an error during the attempt to get the profile. */
            @Override
            public void onError(AuthError ae) {
         /* Retry or inform the user of the error */
            }
        });
    }


    /**
     * Sets the state of the application to reflect that the user is currently authorized.
     */
    private void setLoggedInState() {
        mLoginButton.setVisibility(Button.GONE);
        mLogoutButton.setVisibility(Button.VISIBLE);
    }

    /**
     * Sets the state of the application to reflect that the user is not currently authorized.
     */
    private void setLoggedOutState() {
        mLoginButton.setVisibility(Button.VISIBLE);
        mLogoutButton.setVisibility(Button.GONE);
//        mProfileText.setText(getString(R.string.default_message));
//        mIamText.setText(getString(R.string.default_iam));
    }

    private void getIdentity() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                // 先只获取身份ID ，验证 Cognito 已正常启用。
                String identityId = credentialsProvider.getIdentityId();
                Log.d(TAG, "my ID is " + identityId);
                try {
                    AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                    s3.setRegion(Region.getRegion(MY_REGION));
                    List<Bucket> bucketList = s3.listBuckets();
                    final StringBuilder bucketNameList = new StringBuilder("My S3 buckets are:\n");
                    for (Bucket bucket : bucketList) {
                        bucketNameList.append(bucket.getName()).append("\n");
                    }
                    Log.d(TAG, "s3 bucket" + bucketNameList);
                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                          Toast.makeText(MainActivity.this, bucketNameList, Toast.LENGTH_LONG).show();
                       }
                     });
                   }
                   catch (Exception e) {
                       e.printStackTrace();
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                            Toast.makeText(MainActivity.this, "This is OK as not authenticated to list S3 bucket.", Toast.LENGTH_LONG).show();
                           }
                       });
                }
            }
        }.start();
    }

    private void setCognitoLogin(String token) {
        /* 用户已登录，联合登录Cognito*/
        Map<String, String> logins = new HashMap<String, String>();
        logins.put("www.amazon.com", token);
        credentialsProvider.setLogins(logins);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        identityPoolId = ""; // 这里补充具体的身份池 ID
        // 初始化 Amazon Cognito 凭证提供程序
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                identityPoolId, // 身份池 ID
                MY_REGION // 区域
        );
        Log.d(TAG, "onCreate: ");
        requestContext = RequestContext.create(this);
        requestContext.registerListener(new AuthorizeListener() {

            /* Authorization was completed successfully. */
            @Override
            public void onSuccess(AuthorizeResult result) {
        /* Your app is now authorized for the requested scopes */
                String token = result.getAccessToken();
                if (null != token) {
                    /* 用户已登录，联合登录Cognito*/
                    setCognitoLogin(token);
                    fetchUserProfile();
                    getIdentity();
                } else {
                    /* The user is not signed in */
                }
                fetchUserProfile();
            }

            /* There was an error during the attempt to authorize the
               application. */
            @Override
            public void onError(AuthError ae) {
        /* Inform the user of the error */
            }

            /* Authorization was cancelled before it could be completed. */
            @Override
            public void onCancel(AuthCancellation cancellation) {
        /* Reset the UI to a ready-to-login state */

            }
        });

        // 给登录按钮注册点击事件
        mLoginButton = findViewById(R.id.login_with_amazon);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthorizationManager.authorize(
                        new AuthorizeRequest.Builder(requestContext)
                                .addScopes(ProfileScope.profile(), ProfileScope.postalCode())
                                .build()
                );
            }
        });

        // 退出按钮，及注册点击事件
        mLogoutButton = (Button) findViewById(R.id.logout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 退出 Amazon 登录
                AuthorizationManager.signOut(getApplicationContext(), new Listener<Void, AuthError>() {
                    @Override
                    public void onSuccess(Void response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setLoggedOutState();
                            }
                        });
                    }

                    @Override
                    public void onError(AuthError authError) {
                        Log.e(TAG, "Error clearing authorization state.", authError);
                    }
                });
                // 退出 AWS Cognito 的登录
                credentialsProvider.clearCredentials();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestContext.onResume();
    }

    @Override
    protected void onStart(){
        super.onStart();
        getIdentity();
        Scope[] scopes = { ProfileScope.profile(), ProfileScope.postalCode() };
        AuthorizationManager.getToken(this, scopes, new Listener<AuthorizeResult, AuthError>() {

            @Override
            public void onSuccess(AuthorizeResult result) {
                String token = result.getAccessToken();
                if (null != token) {
                    Log.d(TAG, "onSuccess: ");
                    setCognitoLogin(token);
                    fetchUserProfile();
                } else {
                    /* The user is not signed in */
                }
            }

            @Override
            public void onError(AuthError ae) {
        /* The user is not signed in */
            }
        });
    }
}