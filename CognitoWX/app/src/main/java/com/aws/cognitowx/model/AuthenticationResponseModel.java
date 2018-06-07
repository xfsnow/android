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

package com.aws.cognitowx.model;


public class AuthenticationResponseModel {
    @com.google.gson.annotations.SerializedName("userId")
    private Integer userId = null;
    @com.google.gson.annotations.SerializedName("openIdToken")
    private String openIdToken = null;
    @com.google.gson.annotations.SerializedName("identityId")
    private String identityId = null;
    @com.google.gson.annotations.SerializedName("status")
    private String status = null;

    /**
     * Gets userId
     *
     * @return userId
     **/
    public Integer getUserId() {
        return userId;
    }

    /**
     * Sets the value of userId.
     *
     * @param userId the new value
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * Gets openIdToken
     *
     * @return openIdToken
     **/
    public String getOpenIdToken() {
        return openIdToken;
    }

    /**
     * Sets the value of openIdToken.
     *
     * @param openIdToken the new value
     */
    public void setOpenIdToken(String openIdToken) {
        this.openIdToken = openIdToken;
    }

    /**
     * Gets identityId
     *
     * @return identityId
     **/
    public String getIdentityId() {
        return identityId;
    }

    /**
     * Sets the value of identityId.
     *
     * @param identityId the new value
     */
    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    /**
     * Gets status
     *
     * @return status
     **/
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of status.
     *
     * @param status the new value
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
