package com.example.cognitodeveloper;

import android.content.Context;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.regions.Regions;

/**
 * Created by xuef on 2018-3-22.
 */

public class DeveloperAuthenticationProvider extends AWSAbstractCognitoDeveloperIdentityProvider {

    private static final String developerProvider = "login.company.developer";


    public DeveloperAuthenticationProvider(String accountId, String identityPoolId, Context context, Regions region) {
        super(accountId, identityPoolId, region);
        // Initialize any other objects needed here.
    }

    /**
     * @see com.amazonaws.auth.AWSAbstractCognitoIdentityProvider#getProviderName()
     * Return the developer provider name which you choose while setting up the
     * identity pool in the Amazon Cognito Console
     */
    @Override
    public String getProviderName() {
        return developerProvider;
    }


    @Override
    public String refresh() {

        // Override the existing token
        setToken(null);

        // Get the identityId and token by making a call to your backend
        // (Call to your backend)

        // Call the update method with updated identityId and token to make sure
        // these are ready to be used from Credentials Provider.

        update(identityId, token);
        return token;

    }

    // If the app has a valid identityId return it, otherwise get a valid
    // identityId from your backend.

    @Override
    public String getIdentityId() {

        return identityId;

    }
}
