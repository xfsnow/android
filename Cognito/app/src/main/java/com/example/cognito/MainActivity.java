package com.example.cognito;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CognitoLwA";
    private RequestContext requestContext;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestContext = RequestContext.create(this);
        requestContext.registerListener(new AuthorizeListener() {

            /* Authorization was completed successfully. */
            @Override
            public void onSuccess(AuthorizeResult result) {
        /* Your app is now authorized for the requested scopes */
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
        Scope[] scopes = { ProfileScope.profile(), ProfileScope.postalCode() };
        AuthorizationManager.getToken(this, scopes, new Listener<AuthorizeResult, AuthError>() {

            @Override
            public void onSuccess(AuthorizeResult result) {
                String token = result.getAccessToken();
                if (null != token) {
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
