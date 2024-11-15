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

package com.example.salestrainingapp;

import java.util.*;

import com.example.salestrainingapp.model.Empty;


@com.amazonaws.mobileconnectors.apigateway.annotation.Service(endpoint = "api-gateway-deployed-stage-invoke-url")
public interface NegotiationAPIClient {


    /**
     * A generic invoker to invoke any API Gateway endpoint.
     * @param request
     * @return ApiResponse
     */
    com.amazonaws.mobileconnectors.apigateway.ApiResponse execute(com.amazonaws.mobileconnectors.apigateway.ApiRequest request);
    
    /**
     * 
     * 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/check-progress", method = "POST")
    Empty checkProgressPost();
    
    /**
     * 
     * 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/check-progress", method = "OPTIONS")
    Empty checkProgressOptions();
    
    /**
     * 
     * 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/continue-conversation", method = "POST")
    Empty continueConversationPost();
    
    /**
     * 
     * 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/continue-conversation", method = "OPTIONS")
    Empty continueConversationOptions();
    
    /**
     * 
     * 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/reset-progress", method = "POST")
    Empty resetProgressPost();
    
    /**
     * 
     * 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/reset-progress", method = "OPTIONS")
    Empty resetProgressOptions();
    
    /**
     * 
     * 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/start-conversation", method = "POST")
    Empty startConversationPost();
    
    /**
     * 
     * 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/start-conversation", method = "OPTIONS")
    Empty startConversationOptions();
    
    /**
     * 
     * 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/start-or-continue", method = "POST")
    Empty startOrContinuePost();
    
    /**
     * 
     * 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/start-or-continue", method = "OPTIONS")
    Empty startOrContinueOptions();
    
}

