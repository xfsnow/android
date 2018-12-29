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

package com.aws.cognitowechat;

import java.util.*;

import com.aws.cognitowechat.model.AuthenticationResponseModel;
import com.aws.cognitowechat.model.AuthenticationRequestModel;

// endpoint 换成你的 API SDK 中的 URL
@com.amazonaws.mobileconnectors.apigateway.annotation.Service(endpoint = "https://12345678.execute-api.cn-north-1.amazonaws.com.cn/test")
public interface CognitoWechatClient {


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
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/loginwechat", method = "POST")
    AuthenticationResponseModel loginwechatPost(
            AuthenticationRequestModel body);
    
}

