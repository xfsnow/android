package com.example.cognitouserpool;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidParameterException;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidPasswordException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CUP";

    /**
     * Add your pool id here
     */
//    private static final String userPoolId = "us-west-2_sT7dO0BSj";

    /**
     * Add you app id
     */
//    private static final String clientId = "66161eg6n7sir5n2hd30769b93";

    /**
     * App secret associated with your app id - if the App id does not have an associated App secret,
     * set the App secret to null.
     * e.g. clientSecret = null;
     */
//    private static final String clientSecret = "630pkpuddrigppi331ah31etrhglpi7g00uigp3moaurhcg7tmq";


    /**
     * Set Your User Pools region.
     * e.g. if your user pools are in US East (N Virginia) then set cognitoRegion = Regions.US_EAST_1.
     */
//    private static final Regions cognitoRegion = Regions.US_WEST_2;


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

        // 为应用程序注册用户
        // Create a CognitoUserAttributes object and add user attributes
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        String userGivenName = "test";
        // 电话号码必须遵循以下格式规则：电话号码必须以加号 (+) 开头，后面紧跟国家/地区代码。电话号码只能包含 + 号和数字。您必须先删除电话号码中的任何其他字符，如圆括号、空格或短划线 (-)，然后才能将该值提交给服务。例如，美国境内的电话号码必须遵循以下格式：+14325551212。
        String phoneNumber = "+8613000000000";
        String email = "xuef@amazon.com";
        String userId = "Test";
        String password = "Password@123";

// Add the user attributes. Attributes are added as key-value pairs
// Adding user's given name.
// Note that the key is "given_name" which is the OIDC claim for given name
        userAttributes.addAttribute("given_name", userGivenName);

// Adding user's phone number
        userAttributes.addAttribute("phone_number", phoneNumber);

// Adding user's email address
        userAttributes.addAttribute("email", email);
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
                }
                else if (exception instanceof InvalidParameterException)
                {
                    Log.d(TAG, "字段校验失败，请使用符合规则的电话号码！");
                }
                else {
                    Log.d(TAG, "注册失败"+exception);
                }
            }
        };
//        AppHelper.getPool().signUpInBackground(usernameInput, userpasswordInput, userAttributes, null, signUpHandler);
        userPool.signUpInBackground(userId, password, userAttributes, null, signupCallback);

    }
}
