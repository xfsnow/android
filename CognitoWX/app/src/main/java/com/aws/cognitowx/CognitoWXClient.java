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

package com.aws.cognitowx;

import java.util.*;

import com.aws.cognitowx.model.AuthenticationResponseModel;
import com.aws.cognitowx.model.AuthenticationRequestModel;


// 请把你的 API Gateway 的请求端点写在这里
@com.amazonaws.mobileconnectors.apigateway.annotation.Service(endpoint = "https://")
public interface CognitoWXClient {


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
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/loginwx", method = "POST")
    AuthenticationResponseModel loginwxPost(
            AuthenticationRequestModel body);
    
}

