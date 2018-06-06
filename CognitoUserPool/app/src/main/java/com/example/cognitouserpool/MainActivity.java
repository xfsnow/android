package com.example.cognitouserpool;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChooseMfaContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidParameterException;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidPasswordException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CUP";

    private String userId = "Test";
    private String password = "Password@123";
//    private String password = "Password@12";

    private CognitoCachingCredentialsProvider credentialsProvider;

    /**
     * 注册用户
     */
    SignUpHandler signupCallback = new SignUpHandler() {

        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Sign-up was successful

            // Check if this user (cognitoUser) needs to be confirmed
            if(!userConfirmed) {
                // This user must be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user
                Log.d(TAG, "onSuccess: not confirmed");

            }
            else {
                // The user has already been confirmed
                Log.d(TAG, "onSuccess: confirmed");
            }
        }

        @Override
        public void onFailure(Exception exception) {
            // 区分不同的校验异常，我现在想到的只有这种方法
            if (exception instanceof InvalidPasswordException)
            {
                Log.d(TAG, "字段校验失败，请使用符合密码规则要求的密码！");
                java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.FINEST);            }
            else if (exception instanceof InvalidParameterException)
            {
                Log.d(TAG, "字段校验失败，请使用符合规则的电话号码！");
            }
            else {
                Log.d(TAG, "注册失败"+exception);
            }
        }
    };

    /**
     * 登录用户，实现验证处理器。这里有实现的几个方法，其实是登录验证交互过程中的几步，至少 getAuthenticationDetails方法是必须实现的，用户ID和密码就是在这个方法里传递给后台接口的
     * onSuccess 和 onFailure 方法也必须实现，这是验证成功和失败返回的提示。
     */

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            // Authentication was successful, the "userSession" will have the current valid tokens
            // Time to do awesome stuff

            // Session is an object of the type CognitoUserSession
            String accessToken = userSession.getAccessToken().getJWTToken();
            String idToken = userSession.getIdToken().getJWTToken();

            Log.d(TAG, "onSuccess: "+userSession + "\naccessToken="+accessToken+",\n idToken="+idToken);
            // TODO 怎么从 AWSConfiguration 中读出具体配置项的值？不用读出具体配置项的值，有另一个构造方法，直接用 awsConfiguration 作传入参数
            Map<String, String> logins = new HashMap<String, String>();
//            logins.put(cognito-idp.<region>.amazonaws.com/<YOUR_USER_POOL_ID>, session.getIdToken().getJWTToken());
            logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_sT7dO0BSj", idToken);
            credentialsProvider.setLogins(logins);
        }

        @Override
        public void getAuthenticationDetails(final AuthenticationContinuation continuation, final String userID) {
            // User authentication details, userId and password are required to continue.
            // Use the "continuation" object to pass the user authentication details

            // After the user authentication details are available, wrap them in an AuthenticationDetails class
            // Along with userId and password, parameters for user pools for Lambda can be passed here
            // The validation parameters "validationParameters" are passed in as a Map<String, String>
            AuthenticationDetails authDetails = new AuthenticationDetails(userId, password, null);

            // Now allow the authentication to continue
            continuation.setAuthenticationDetails(authDetails);
            continuation.continueTask();
        }

        @Override
        public void getMFACode(final MultiFactorAuthenticationContinuation continuation) {
            // Multi-factor authentication is required to authenticate
            // A code was sent to the user, use the code to continue with the authentication


            // Find where the code was sent to
//            String codeSentHere = continuation.getParameter()[0];
//
//            // When the verification code is available, continue to authenticate
//            continuation.setMfaCode(code);
//                continuation.continueTask();
        }

        @Override
        public void authenticationChallenge(final ChallengeContinuation continuation) {
            // A custom challenge has to be solved to authenticate

            // Set the challenge responses

            // Call continueTask() method to respond to the challenge and continue with authentication.
        }



        @Override
        public void onFailure(final Exception exception) {
            // Authentication failed, probe exception for the cause
            Log.d(TAG, "onFailure: "+exception);
            // 故意登录失败，会得到错误提示的异常，比如  com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException: Incorrect username or password. (Service: AmazonCognitoIdentityProvider; Status Code: 400; Error Code: NotAuthorizedException; Request ID: cdf9403e-f055-11e7-8faa-19f694efb8f4)

        }
    };

    // 获取用户详细信息
    GetDetailsHandler userDetailHandler = new GetDetailsHandler() {
        @Override
        public void onSuccess(final CognitoUserDetails details) {
            // Successfully retrieved user details
            Log.d(TAG, "userDetailHandler  onSuccess: ");
            Map<String, String> detailsAttributes= details.getAttributes().getAttributes();
            for (Map.Entry<String, String> entry : detailsAttributes.entrySet()) {
                Log.d(TAG, "userDetailHandler detailsAttributes: "+entry.getKey()+"="+entry.getValue());
            }

        }

        @Override
        public void onFailure(final Exception exception) {
            // Failed to retrieve the user details, probe exception for the cause
            Log.d(TAG, "userDetailHandler onFailure: "+exception);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        java.util.logging.Logger.getLogger("com.amazonaws").setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.FINEST);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 实例化一个 CognitoUserPool 对象，表示我们的用户池
        Context context = getApplicationContext();
        // 把配置存在配置文件里 /src/main/res/raw/awsconfiguration.json。可以方便地在 github 排除掉，保证安全。
        AWSConfiguration awsConfiguration = new AWSConfiguration(context);

        CognitoUserPool userPool = new CognitoUserPool(context, awsConfiguration);
        // TODO 怎么从 AWSConfiguration 中读出具体配置项的值？不用读出具体配置项的值，有另一个构造方法，直接用 awsConfiguration 作传入参数
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context, // Context
                awsConfiguration
        );

        // 为应用程序注册用户
        // Create a CognitoUserAttributes object and add user attributes
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        String userGivenName = "test";
        // 电话号码必须遵循以下格式规则：电话号码必须以加号 (+) 开头，后面紧跟国家/地区代码。电话号码只能包含 + 号和数字。您必须先删除电话号码中的任何其他字符，如圆括号、空格或短划线 (-)，然后才能将该值提交给服务。例如，美国境内的电话号码必须遵循以下格式：+14325551212。
        String phoneNumber = "+8613000000000";
        String email = "xuef@amazon.com";


        // Add the user attributes. Attributes are added as key-value pairs
        // Adding user's given name.
        // Note that the key is "given_name" which is the OIDC claim for given name
//        userAttributes.addAttribute("given_name", userGivenName);
//
//        // Adding user's phone number
//        userAttributes.addAttribute("phone_number", phoneNumber);
//
//        // Adding user's email address
//        userAttributes.addAttribute("email", email);
//        // 注册新用户
//        userPool.signUpInBackground(userId, password, userAttributes, null, signupCallback);



        // User 对象实例一次，后面可以反复使用。
        final CognitoUser user = userPool.getUser(userId);
        // 只要验证过一次，后面客户端 SDK 会缓存，要想再测试验证不过，必须清理缓存，或者把App删掉。
        user.getSessionInBackground(authenticationHandler);

        // getDetails() 方法没有在后台运行的相应方法，只有自己开个线程避开主线程发HTTP请求了。
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                user.getDetails(userDetailHandler);
//            }
//        }.start();

    }
}
