package com.example.cognitouserpool;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidParameterException;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidPasswordException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CUP";

    // 请在这里写你的 Cognito User Pool Id
    public static final String USER_POOL_ID = "us-west-2_w56xyV04e";
    public static final String USER_POOL_CLIENT_ID = "cg9bgltnr0vti1q9qk1aj7tod";
    public static final String USER_POOL_CLIENT_SECRET = "1lt7ppfe9vq0dgt223hsr0ms1a057jppp17fch3gi9lbmuomnskj";

    public static final String IDENTITY_POOL_ID = "us-west-2:eb8e2546-658e-4d16-bde8-e6d07cb3f7cb";
    public static final Regions COGNITO_REGION = Regions.US_WEST_2;
    public static final String BUCKET = "email-receive-2017";

//    private String userId = "Test";
//    private String password = "Password@123";
//    private String userId = "vendor001";
//    private String password = "vendor001";
//    private String password = "Password@12";
    private CognitoUser cognitoUser;
    private User user;
    private Context context;
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
     * 把 User Pool 的 token 集成到 Identity pool，再换成 AWS 的凭据访问 S3
     * @param idToken
     */
    private void getFile(final String idToken) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Map<String, String> logins = new HashMap<String, String>();
        //            logins.put(cognito-idp.<region>.amazonaws.com/<YOUR_USER_POOL_ID>, session.getIdToken().getJWTToken());
                String idKey = "cognito-idp."+ COGNITO_REGION.getName()+".amazonaws.com/" + USER_POOL_ID;
                Log.d(TAG, "idKey: "+idKey+", idToken: "+ idToken);
                logins.put(idKey, idToken);

                credentialsProvider = new CognitoCachingCredentialsProvider(
                        context, // Context
                        IDENTITY_POOL_ID,
                        COGNITO_REGION
                );
                // 换用户登录时要先清理缓存，否则会由于之前缓存的结果报错 Amazon.CognitoIdentity.Model.NotAuthorizedException: Logins don't match. Please include at least one valid login for this identity or identity pool
                credentialsProvider.clear();
                credentialsProvider.setLogins(logins);
                Log.d(TAG, "getIdentityId()=" + credentialsProvider.getIdentityId());
                AmazonS3 s3Client = new AmazonS3Client(credentialsProvider, Region.getRegion(COGNITO_REGION));
//                List<Bucket> bucketList = s3Client.listBuckets();
//                StringBuilder bucketNameList = new StringBuilder("My S3 buckets are:\n");
//                for (Bucket bucket : bucketList) {
//                    bucketNameList.append(bucket.getName()).append("\n");
//                }
        //        setHint(txtHint, "Login succeeded.\n"+bucketNameList);
//                Log.d(TAG, "buckets: "+bucketNameList);
                String s3Key = "vendor/vendor001/20181031145731.txt";
                // vendor001 的文件，它的 identityId=us-west-2:abc3820d-bd52-4cdb-96fd-769f5d18a07e
                s3Key = "vendor/us-west-2:abc3820d-bd52-4cdb-96fd-769f5d18a07e/output-auto-deploy.yaml";

                // Test 用户的文件，它的 identityId=us-west-2:511d334f-017d-4145-ac05-d5a52be9a046
//                s3Key = "vendor/us-west-2:511d334f-017d-4145-ac05-d5a52be9a046/22a44334a37100903b20977585d042bf";

                try {
                    GetObjectMetadataRequest requestCheck = new GetObjectMetadataRequest(BUCKET, s3Key);
                    ObjectMetadata response = s3Client.getObjectMetadata(requestCheck);
                    Log.d(TAG, "Etag: " + response.getETag() + ", size:" + response.getContentLength());
                }
                catch (Exception e) {
                    Log.d(TAG, "s3 exception: ");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 登录用户，实现验证处理器。这里有实现的几个方法，其实是登录验证交互过程中的几步，至少 getAuthenticationDetails方法是必须实现的，用户ID和密码就是在这个方法里传递给后台接口的
     * onSuccess 和 onFailure 方法也必须实现，这是验证成功和失败返回的提示。
     */
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            // Authentication was successful, the "userSession" will have the current valid tokens
            // Time to do awesome stuff


            // 获取用户详细信息。这2个请求都是后台运行的，所以不能保证先验证，后获取用户信息，有时可能出现验证未完成，不能获取用户信息的情况。要想保证顺序，可以放在 AuthenticationHandler.onSuccess()中。
            cognitoUser.getDetailsInBackground(userDetailHandler);


            // Session is an object of the type CognitoUserSession
            String accessToken = userSession.getAccessToken().getJWTToken();
            String idToken = userSession.getIdToken().getJWTToken();

            Log.d(TAG, "onSuccess: "+userSession + "\naccessToken="+accessToken+",\n idToken="+idToken);
            // 用 User Pool 的 token 去访问AWS资源
            getFile(idToken);

        }

        @Override
        public void getAuthenticationDetails(final AuthenticationContinuation continuation, final String userID) {
            // User authentication details, userId and password are required to continue.
            // Use the "continuation" object to pass the user authentication details

            // After the user authentication details are available, wrap them in an AuthenticationDetails class
            // Along with userId and password, parameters for user pools for Lambda can be passed here
            // The validation parameters "validationParameters" are passed in as a Map<String, String>
            AuthenticationDetails authDetails = new AuthenticationDetails(user.getUserId(), user.getPassword(), null);

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
//                Log.d(TAG, "userDetailHandler detailsAttributes: "+entry.getKey()+"="+entry.getValue());
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
        context = getApplicationContext();
        CognitoUserPool userPool = new CognitoUserPool(context, USER_POOL_ID, USER_POOL_CLIENT_ID, USER_POOL_CLIENT_SECRET, COGNITO_REGION);

        // 为应用程序注册用户
        // Create a CognitoUserAttributes object and add user attributes
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        user = new User("vendor001", "vendor001", "vendor001", "+8613522071098", "xuef@amazon.com");

        // 另一个用户
//        user = new User("vendor002", "vendor002", "vendor002", "+8613522071098", "xuef@amazon.com");
//        user = new User("Test", "Password@123", "vendor001", "+8613522071098", "xuef@amazon.com");


        // Add the user attributes. Attributes are added as key-value pairs
        // Adding user's given name.
        // Note that the key is "given_name" which is the OIDC claim for given name
        userAttributes.addAttribute("given_name", user.getUserGivenName());

        // Adding user's phone number
        userAttributes.addAttribute("phone_number", user.getPhoneNumber());

        // Adding user's email address
        userAttributes.addAttribute("email", user.getEmail());

        // 注册新用户。后台创建的用户初始状态是 Account Status	Enabled / FORCE_CHANGE_PASSWORD
        //只能通过登录时改密码，或者验证邮箱来使状态正常。
        //比较简单的办法是用 SDK 创建用户，然后在管理后台 CONFIRM
//        userPool.signUpInBackground(user.getUserId(), user.getPassword(), userAttributes, null, signupCallback);


        // User 对象实例一次，后面可以反复使用。
        cognitoUser = userPool.getUser(user.getUserId());
        // 只要验证过一次，后面客户端 SDK 会缓存，要想再测试验证不过，必须清理缓存，或者把App删掉。
        cognitoUser.getSessionInBackground(authenticationHandler);
    }
}