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

import org.apache.log4j.Logger;
import org.wso2.esbMonitor.connector.RemoteConnector;
import org.wso2.esbMonitor.esbEvents.events.EventFactory;
import org.wso2.esbMonitor.esbEvents.events.HighRequestCountEvent;
import org.wso2.esbMonitor.persistance.PersistenceServiceFactory;

import javax.management.*;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Dinanjana on 29/05/2016.
 * This class checks network traffic
 * for HTTP/S
 */
public class PassThruHTTPSenderAndReciever {

    final static Logger logger = Logger.getLogger(PassThruHTTPSenderAndReciever.class);
    private String bean;
    private RemoteConnector remote;
    // needs to be initialized by a property file
    private int maxThreadCount;
    private int maxQueueSize;
    private boolean eventDetected=false;
    private HighRequestCountEvent event;
    private int currThreadCount;
    private int currQueueSize;

    public PassThruHTTPSenderAndReciever(String bean,RemoteConnector remote){
        this.bean = bean;
        this.remote=remote;
        //event= EventFactory.getHighRequestCountEventInstance();
    }

    public void getMbeanInfo() {
        checkWarningUsage(bean);
    }

    /**
     * */
    private void checkWarningUsage(String mbeanName) {
        PassThruHTTPBean passThruHTTPBean = new PassThruHTTPBean();

        try {
            logger.info(":Accessing HTTP transport details ");
            passThruHTTPBean.setActiveThreadCount((int) remote.getMbeanAttribute(mbeanName, "ActiveThreadCount"));
            passThruHTTPBean.setAvgSizeRecieved((Double) remote.getMbeanAttribute(mbeanName,"AvgSizeReceived"));
            passThruHTTPBean.setAvgSizeSent((Double) remote.getMbeanAttribute(mbeanName,"AvgSizeSent"));
            passThruHTTPBean.setFaultSending((Long) remote.getMbeanAttribute(mbeanName,"FaultsSending"));
            passThruHTTPBean.setFaultsRecieving((Long) remote.getMbeanAttribute(mbeanName,"FaultsReceiving"));
            passThruHTTPBean.setQueueSize((Integer) remote.getMbeanAttribute(mbeanName,"QueueSize"));
            passThruHTTPBean.setMessageSent((Long) remote.getMbeanAttribute(mbeanName,"MessagesSent"));
            passThruHTTPBean.setMessagesRecieved((Long) remote.getMbeanAttribute(mbeanName, "MessagesReceived"));
            passThruHTTPBean.setDate(new Date());
            switch (bean){
                case "org.apache.synapse:Type=Transport,Name=passthru-http-sender":
                    passThruHTTPBean.setType(RequestType.HTTP_SENDER);
                    break;
                case "org.apache.synapse:Type=Transport,Name=passthru-http-receiver":
                    passThruHTTPBean.setType(RequestType.HTTP_RECEIVER);
                    break;
                case "org.apache.synapse:Type=Transport,Name=passthru-https-receiver":
                    passThruHTTPBean.setType(RequestType.HTTPS_RECEIVER);
                    break;
                case "org.apache.synapse:Type=Transport,Name=passthru-https-sender":
                    passThruHTTPBean.setType(RequestType.HTTP_SENDER);
                    break;
                default:
                    passThruHTTPBean.setType(RequestType.HTTP_SENDER);
            }

            logger.info(passThruHTTPBean.getMessageSent() +" "+ passThruHTTPBean.getActiveThreadCount());
            currThreadCount=passThruHTTPBean.getActiveThreadCount();
            currQueueSize=passThruHTTPBean.getQueueSize();
            if((passThruHTTPBean.getActiveThreadCount() >= maxThreadCount) || (passThruHTTPBean.getQueueSize() >= maxQueueSize)) {
                logger.info(":High HTTP loads");
                if(!eventDetected){
                    eventDetected=true;
                    event.initEvent();
                }
                event.triggerEvent();
            } else {
                logger.info("HTTP network load is normal");
            }
            if(event.isEventPeriodElapsed()&&eventDetected){
                eventDetected=false;
                event.resetEvent();
                logger.info("High Request Count Event ended on " +System.currentTimeMillis());
            }
            logger.info("Adding network event to scheduledList");
            PersistenceServiceFactory factory = new PersistenceServiceFactory();
            factory.getPersistenceServiceInstance().addNetworkEvent(passThruHTTPBean);

        } catch (MBeanException e) {
            logger.error(e.getMessage());

        } catch (AttributeNotFoundException e) {
            logger.error(e.getMessage());

        } catch (InstanceNotFoundException e) {
            logger.error(e.getMessage());

        } catch (ReflectionException e) {
            logger.error(e.getMessage());

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void setMaxThreadCount(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public void setEvent(HighRequestCountEvent event){
        this.event=event;
    }

    public int getCurrQueueSize() {
        return this.currQueueSize;
    }

    public int getCurrThreadCount() {
        return this.currThreadCount;
    }
}
