/*
 *
 *  * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package org.wso2.esbMonitor.network;

import org.wso2.esbMonitor.connector.RemoteConnector;

/**
 * Created by Dinanjana on 29/07/2016.
 */
public class NetworkFactory {
    private static PassThruHTTPSenderAndReciever passThruHTTPSender;
    private static PassThruHTTPSenderAndReciever passThruHTTPReciever;
    private static PassThruHTTPSenderAndReciever passThruHTTPSSender;
    private static PassThruHTTPSenderAndReciever passThruHTTPSReciever;
    private static RemoteConnector remoteConnector;

    public NetworkFactory(RemoteConnector remote){
        remoteConnector=remote;
    }

    public static PassThruHTTPSenderAndReciever getPassThruHTTPSenderInstance(){
        if(passThruHTTPSender==null){
            passThruHTTPSender=new PassThruHTTPSenderAndReciever("org.apache.synapse:Type=Transport," +
                                                                 "Name=passthru-http-sender",
                                                                 remoteConnector);
        }
        return passThruHTTPSender;
    }

    public static PassThruHTTPSenderAndReciever getPassThruHTTPSSenderInstance(){
        if(passThruHTTPSSender==null){
            passThruHTTPSSender=new PassThruHTTPSenderAndReciever("org.apache.synapse:Type=Transport," +
                                                                  "Name=passthru-https-sender",
                                                                  remoteConnector);
        }
        return passThruHTTPSSender;
    }

    public static PassThruHTTPSenderAndReciever getPassThruHTTPRecieverInstance(){
        if(passThruHTTPReciever==null){
            passThruHTTPReciever=new PassThruHTTPSenderAndReciever("org.apache.synapse:Type=Transport," +
                                                                   "Name=passthru-http-receiver",
                                                                   remoteConnector);
        }
        return passThruHTTPReciever;
    }

    public static PassThruHTTPSenderAndReciever getPassThruHTTPSRecieverInstance(){
        if(passThruHTTPSReciever==null){
            passThruHTTPSReciever=new PassThruHTTPSenderAndReciever("org.apache.synapse:Type=Transport," +
                                                                    "Name=passthru-https-receiver",
                                                                    remoteConnector);
        }
        return passThruHTTPSReciever;
    }
}
