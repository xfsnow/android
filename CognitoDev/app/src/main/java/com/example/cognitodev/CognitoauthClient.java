/*
 * Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.cognitodev;

import java.util.*;

import com.example.cognitodev.model.AuthenticationResponseModel;
import com.example.cognitodev.model.AuthenticationRequestModel;

// Change this endpoint URL to your own URL.
@com.amazonaws.mobileconnectors.apigateway.annotation.Service(endpoint = "https://.execute-api.us-west-2.amazonaws.com/test")
public interface CognitoauthClient {


    /**
     * A generic invoker to invoke any API Gateway endpoint.
     * @param request
     * @return ApiResponse
     */
    com.amazonaws.mobileconnectors.apigateway.ApiResponse execute(com.amazonaws.mobileconnectors.apigateway.ApiRequest request);

    /**
     *
     *
     * @param body
     * @return AuthenticationResponseModel
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/login", method = "POST")
    AuthenticationResponseModel loginPost(
            AuthenticationRequestModel body);

}

