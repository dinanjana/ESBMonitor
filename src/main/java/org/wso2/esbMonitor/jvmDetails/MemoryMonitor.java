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

package org.wso2.esbMonitor.jvmDetails;

import org.apache.log4j.Logger;
import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.configuration.EventConfiguration;
import org.wso2.esbMonitor.connector.RemoteConnector;
import org.wso2.esbMonitor.dumpHandlers.HeapDumper;
import org.wso2.esbMonitor.dumpHandlers.ThreadDumpCreator;
import org.wso2.esbMonitor.esbEvents.ESBStatus;
import org.wso2.esbMonitor.esbEvents.events.EventFactory;
import org.wso2.esbMonitor.esbEvents.events.OOMEvent;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dinanjana
 * on 30/04/2016.
 */
public class MemoryMonitor extends JVMDetails{

    final Logger logger = Logger.getLogger(MemoryMonitor.class);
    private double memory;
    private final String OBJECT_NAME ="java.lang:type=Memory";
    private RemoteConnector remote;
    private Configuration config;
    private boolean eventDetected=false;
    private OOMEvent oomEvent;
    private static long currentUsedMemory=0;

    protected MemoryMonitor(){}

    public void initMonitor(){
        oomEvent= EventFactory.getOomEventInstance();
    }

    public void getMbeanInfo() {
        checkWarningUsage();
    }

    private void checkWarningUsage() {
            try {
                logger.info(":Accessing memory details");
                CompositeData memoryUsage = (CompositeData) remote.getMbeanAttribute(OBJECT_NAME,"HeapMemoryUsage");
                long maxMemory = (Long) memoryUsage.get("max");
                long usedMemory = (Long) memoryUsage.get("used");
                currentUsedMemory=usedMemory;
                logger.info("Committed heap memory :"+maxMemory/(1024*1024) + "mb Used heap Memory :" +
                            usedMemory/(1024*1024) +"mb");
                if ((double) usedMemory / maxMemory > memory) {
                    logger.info(":Possible Out of Memory event detected");
                    //Records the new event
                    if(!eventDetected){
                        eventDetected=true;
                        oomEvent.initEvent();
                    }
                    oomEvent.triggerEvent();
                } else {
                    logger.info("Memory usage is normal " + (double) usedMemory / maxMemory);
                }
                //Event ends
                if(oomEvent.isEventPeriodElapsed() && eventDetected){
                    eventDetected=false;
                    oomEvent.resetEvent();
                    logger.info("Event ended on " +System.currentTimeMillis());
                }

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

    public synchronized static long getCurrentUsedMemory(){
        return currentUsedMemory;
    }

    public synchronized String  getValue(){
        return null;
    }

    public void setMemory(double memory) {
        this.memory = memory;
    }

    public void setRemote(RemoteConnector remote) {
        this.remote = remote;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }
}
