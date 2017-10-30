package org.snowpeak.cognitoamazon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CognitoAmazon";

    private RequestContext requestContext;
    private TextView mProfileText;
    private TextView mIamText;
    private View mLoginButton;
    private View mLogoutButton;
    private String identityPoolId;
    private CognitoCachingCredentialsProvider credentialsProvider;

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
                        StringBuilder profileBuilder = new StringBuilder();
                        profileBuilder.append(String.format("Welcome, %s!\n", name));
                        profileBuilder.append(String.format("Your Account Id is %s\n", accountId));
                        profileBuilder.append(String.format("Your email is %s\n", email));
                        profileBuilder.append(String.format("Your zipCode is %s\n", zipCode));
                        final String profile = profileBuilder.toString();
                        mProfileText.setText(profile);
                        Log.d(TAG, "Profile Response: " + profile);
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
        mProfileText.setText(getString(R.string.default_message));
        mIamText.setText(getString(R.string.default_iam));
    }

    private void getIdentity() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                String identityId = credentialsProvider.getIdentityId();
                Log.d(TAG, "my ID is " + identityId);
                // 用 S3 演示授权用户可操作读取桶列表。
                AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                try {
                    List<Bucket> bucketList = s3.listBuckets();
                    final StringBuilder bucketNameList = new StringBuilder("My S3 buckets are:\n");
                    for (Bucket bucket: bucketList) {
                        bucketNameList.append(bucket.getName()).append("\n");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           Log.d(TAG, "s3 bucket" );
                           mIamText.setText(bucketNameList);
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d(TAG, "This is OK as not authenticated to list S3 bucket." );
                }
            }
        }.start();
    }

    /**
     * 为了把 Identity Pool ID 之类的配置值不上传到 github，做个读取 assets 文件的方法
     * @param fileName
     * @return
     */
    private String getConfig(String fileName) {
        String res="";
        try{
            InputStreamReader inputReader = new InputStreamReader( getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String fileName = "identity_pool_id.txt"; //文件名字
        identityPoolId = getConfig(fileName);
        super.onCreate(savedInstanceState);
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // Context
                identityPoolId, // Identity Pool ID
                Regions.US_WEST_2 // Region
        );
        getIdentity();

        requestContext = RequestContext.create(this);
        requestContext.registerListener(new AuthorizeListener() {

            /* Authorization was completed successfully. */
            @Override
            public void onSuccess(AuthorizeResult result) {
        /* Your app is now authorized for the requested scopes */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // At this point we know the authorization completed, so remove the ability to return to the app to sign-in again
//                        setLoggingInState(true);
                    }
                });
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

        setContentView(R.layout.activity_main);

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
        // 显示用户信息的文本块，未登录时显示提示语
        mProfileText = (TextView) findViewById(R.id.profile_info);
        // 显示用户使用 AWS 的信息，用来演示授权前后的行为
        mIamText = (TextView) findViewById(R.id.iam_info);

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
        // 如果 Android　应用的生命周期管理把这个活动关闭了，在 onResume() 方法里把 requestContext 恢复起来。
        requestContext.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Scope[] scopes = {ProfileScope.profile(), ProfileScope.postalCode()};
        // 活动开始时调 getToken 检查是否已经登录状态，是登录的直接显示用户信息
        AuthorizationManager.getToken(this, scopes, new Listener<AuthorizeResult, AuthError>() {
            @Override
            public void onSuccess(AuthorizeResult result) {
                String token = result.getAccessToken();
                if (null != token) {
                    /* The user is signed in */
                    Map<String, String> logins = new HashMap<String, String>();
                    logins.put("www.amazon.com", token);
                    credentialsProvider.setLogins(logins);
                    getIdentity();
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