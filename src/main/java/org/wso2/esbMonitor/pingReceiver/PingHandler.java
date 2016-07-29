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

package org.wso2.esbMonitor.pingReceiver;


import org.apache.log4j.Logger;
import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.configuration.EventConfiguration;
import org.wso2.esbMonitor.esbEvents.ESBStatus;
import org.wso2.esbMonitor.esbEvents.events.EventFactory;
import org.wso2.esbMonitor.esbEvents.events.UnresponsiveESBEvent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Dinanjana on 19/06/2016.
 */
public class PingHandler extends Thread{
    private Logger logger = Logger.getLogger(PingHandler.class);
    private long lastUpdatedTime=0;
    private int maximumFailedRequestCount=5;
    private int continousFailedRequestCount=0;
    private final String USER_AGENT = "Mozilla/5.0";
    private String ip="127.0.0.1";
    private String port="8080";
    private boolean failedRequest=false;
    private boolean eventDetected=false;
    private UnresponsiveESBEvent event;

    public PingHandler(){
        event= EventFactory.getUnresponsiveEsbEventInstance();
        EventConfiguration event=Configuration.getInstance().getEventConfigurations().get(ESBStatus.UNRESPONSIVE_ESB);
        ip=event.getOtherProperties().getProperty("IP");
        port=event.getOtherProperties().getProperty("PORT");
        maximumFailedRequestCount=Integer.parseInt(event.getOtherProperties().
                getProperty("MAXIMUM_FAILED_REQUEST_COUNT"));
    }

    public void sendPing(){
        String url = "http://"+ip+":"+port+"/esbFR/pingReq";
        URL obj;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            logger.info("\nSending ping request request to URL : " + url);
            logger.info("Response Code : " + responseCode);
            if(responseCode >= 500 && responseCode < 600){
                if(!failedRequest){
                    failedRequest=true;
                    continousFailedRequestCount=0;
                }
                continousFailedRequestCount++;
                if(continousFailedRequestCount>=maximumFailedRequestCount){
                    if(!eventDetected){
                        eventDetected=true;
                        event.initEvent();
                    }
                    event.triggerEvent();
                }
            }else {
                failedRequest=false;
                continousFailedRequestCount=0;
                eventDetected=false;
                event.resetEvent();
            }
        } catch (MalformedURLException e) {
            logger.error("Error :",e);
        } catch (ProtocolException e) {
            logger.error("Error :", e);
        } catch (IOException e) {
            //logger.error("Error :", e);
            if(!failedRequest){
                failedRequest=true;
                continousFailedRequestCount=0;
            }
            continousFailedRequestCount++;
            if(continousFailedRequestCount>=maximumFailedRequestCount){
                if(!eventDetected){
                    eventDetected=true;
                    event.initEvent();
                }
                event.triggerEvent();
                failedRequest=false;
                continousFailedRequestCount=0;
                eventDetected=false;
                event.resetEvent();
            }
        }

    }
}
