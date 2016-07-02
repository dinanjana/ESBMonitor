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
import org.wso2.esbMonitor.esbEvents.ESBEvent;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.io.IOException;


/**
 * Created by Dinanjana
 * on 30/04/2016.
 */
public class MemoryMonitor{

    final Logger logger = Logger.getLogger(MemoryMonitor.class);
    private ObjectName bean = null;
    private double memory;
    private final String OBJECT_NAME ="java.lang:type=Memory";
    private RemoteConnector remote;
    private ThreadDumpCreator threadDumpCreator = null;
    private HeapDumper heapDumper= null;
    private Configuration config;
    private int maxNumOfThreadDumps;
    private int maxNumOfHeapDumps;
    private long eventEndTime;
    private long threadDumpsCreated;
    private long heapDumpsCreated;
    private boolean eventDetected=false;

    public void getMbeanInfo() {
        try {
            bean = new ObjectName(OBJECT_NAME);
            checkWarningUsage();
        } catch (MalformedObjectNameException e) {
            logger.error("MemoryMonitor.java:25 " + e.getMessage());
        }
    }

    private boolean checkWarningUsage() {
            try {
                logger.info(":Acessing memory details");
                CompositeData memoryUsage = (CompositeData) remote.getMbeanAttribute(OBJECT_NAME,"HeapMemoryUsage");
                long maxMemory = (Long) memoryUsage.get("max");
                long usedMemory = (Long) memoryUsage.get("used");
                if ((double) usedMemory / maxMemory > memory) {
                    logger.info(":Possible Out of Memory event detected");
                    //Grab necessary config from config object
                    if(!eventDetected){
                        EventConfiguration eventConfiguration = config.getEventConfigurations().get(ESBEvent.OOM_EVENT);
                        maxNumOfHeapDumps=eventConfiguration.getMaxHeapDumps();
                        maxNumOfThreadDumps=eventConfiguration.getMaxThreadDumps();
                        eventEndTime=eventConfiguration.getEventPeriod() + System.currentTimeMillis();
                        eventDetected=true;
                        threadDumpsCreated=0;
                        heapDumpsCreated=0;
                        //ToDo Send to report module
                    }
                    if(threadDumpCreator == null){
                        threadDumpCreator = new ThreadDumpCreator(config,remote);
                    }
                    //Checks if event as elapsed and if the number of dumps
                    //allowed elapsed
                    if(System.currentTimeMillis() < eventEndTime &&
                            threadDumpsCreated <= maxNumOfThreadDumps){
                        threadDumpCreator.getMbeanInfo();
                        threadDumpsCreated++;
                    }
                    if(System.currentTimeMillis() < eventEndTime &&
                            heapDumpsCreated <= maxNumOfHeapDumps){
                        heapDumper = new HeapDumper(config,remote);
                        heapDumper.start();
                        heapDumpsCreated++;
                    }
                    return true;
                } else {
                    logger.info("Memory usage is normal " + (double) usedMemory / maxMemory);
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
        return false;
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
