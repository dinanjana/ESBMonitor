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

package org.wso2.esbMonitor.tasks;

import org.apache.log4j.Logger;
import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.connector.RemoteConnector;
import org.wso2.esbMonitor.dumpHandlers.ThreadDumpCreator;
import org.wso2.esbMonitor.network.PassThruHTTPSenderAndReciever;

/**
 * Created by Dinanjana on 29/05/2016.
 */
public class NetworkMonitor extends Thread {

    private Logger logger = Logger.getLogger(NetworkMonitor.class);
    private Configuration config;
    private PassThruHTTPSenderAndReciever passThruHTTPSender;
    private PassThruHTTPSenderAndReciever passThruHTTPReciever;
    private PassThruHTTPSenderAndReciever passThruHTTPSSender;
    private PassThruHTTPSenderAndReciever passThruHTTPSReciever;
    private RemoteConnector remoteConnector;
    private ThreadDumpCreator threadDumpCreator;
    //needs to be initialized from a property file
    private long waitTime;


    public NetworkMonitor(Configuration config,RemoteConnector remote){
        this.config = config;
        this.remoteConnector = remote;
    }
    private void initTask(){
        waitTime=config.getConfigurationBean().getNetworkTask();
        threadDumpCreator = new ThreadDumpCreator(config,remoteConnector);
        passThruHTTPSender = new PassThruHTTPSenderAndReciever("org.apache.synapse:Type=Transport,Name=passthru-http-sender",
                remoteConnector);
        passThruHTTPReciever = new PassThruHTTPSenderAndReciever("org.apache.synapse:Type=Transport,Name=passthru-http-receiver",
                remoteConnector);
        passThruHTTPSSender = new PassThruHTTPSenderAndReciever("org.apache.synapse:Type=Transport,Name=passthru-https-sender",
                remoteConnector);
        passThruHTTPSReciever = new PassThruHTTPSenderAndReciever("org.apache.synapse:Type=Transport,Name=passthru-https-receiver",
                remoteConnector);
        passThruHTTPSender.setMaxQueueSize(config.getConfigurationBean().getMaxReqestqueueSize());
        passThruHTTPSender.setMaxThreadCount(config.getConfigurationBean().getHttpRequests());
        passThruHTTPSender.setThreadDumpCreator(threadDumpCreator);
        passThruHTTPReciever.setMaxQueueSize(config.getConfigurationBean().getMaxReqestqueueSize());
        passThruHTTPReciever.setMaxThreadCount(config.getConfigurationBean().getHttpRequests());
        passThruHTTPReciever.setThreadDumpCreator(threadDumpCreator);
        passThruHTTPSReciever.setMaxThreadCount(config.getConfigurationBean().getHttpRequests());
        passThruHTTPSReciever.setMaxQueueSize(config.getConfigurationBean().getMaxReqestqueueSize());
        passThruHTTPSReciever.setThreadDumpCreator(threadDumpCreator);
        passThruHTTPSSender.setMaxQueueSize(config.getConfigurationBean().getMaxReqestqueueSize());
        passThruHTTPSSender.setMaxThreadCount(config.getConfigurationBean().getHttpRequests());
        passThruHTTPSSender.setThreadDumpCreator(threadDumpCreator);
    }
    public void run(){
        initTask();
        while (true){
            passThruHTTPSender.getMbeanInfo();
            passThruHTTPReciever.getMbeanInfo();
            passThruHTTPSSender.getMbeanInfo();
            passThruHTTPSReciever.getMbeanInfo();
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                logger.error("Network Thread error" , e);
            }
        }
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }
}
