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

package com.example.petstore.model;

import java.math.BigDecimal;

public class Pet {
    @com.google.gson.annotations.SerializedName("id")
    private Integer id = null;
    @com.google.gson.annotations.SerializedName("price")
    private BigDecimal price = null;
    @com.google.gson.annotations.SerializedName("type")
    private String type = null;

    /**
     * Gets id
     *
     * @return id
     **/
    public Integer getId() {
        return id;
    }

    /**
     * Sets the value of id.
     *
     * @param id the new value
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets price
     *
     * @return price
     **/
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the value of price.
     *
     * @param price the new value
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Gets type
     *
     * @return type
     **/
    public String getType() {
        return type;
    }

    /**
     * Sets the value of type.
     *
     * @param type the new value
     */
    public void setType(String type) {
        this.type = type;
    }

}
