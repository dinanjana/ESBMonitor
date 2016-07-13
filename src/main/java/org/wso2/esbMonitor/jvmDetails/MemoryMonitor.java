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
import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;


/**
 * Created by Dinanjana
 * on 30/04/2016.
 */
public class MemoryMonitor extends Observable{

    final Logger logger = Logger.getLogger(MemoryMonitor.class);
    private double memory;
    private final String OBJECT_NAME ="java.lang:type=Memory";
    private RemoteConnector remote;
    private ThreadDumpCreator threadDumpCreator = null;
    private HeapDumper heapDumper= null;
    private Configuration config;
    private int maxNumOfThreadDumps;
    private int maxNumOfHeapDumps;
    private long eventStartTime;
    private long eventEndTime;
    private long eventPeriod;
    private long threadDumpsCreated;
    private long heapDumpsCreated;
    private boolean eventDetected=false;
    private List <String> threadDumpsNames = new ArrayList<>();
    private List<String> heapDumpsNames = new ArrayList<>();

    public void initMonitor(){
        EventConfiguration eventConfiguration = config.getEventConfigurations().get(ESBStatus.OOM_EVENT);
        maxNumOfHeapDumps=eventConfiguration.getMaxHeapDumps();
        maxNumOfThreadDumps=eventConfiguration.getMaxThreadDumps();
        eventPeriod =eventConfiguration.getEventPeriod();
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
                if ((double) usedMemory / maxMemory > memory) {
                    logger.info(":Possible Out of Memory event detected");
                    //Records the new event
                    if(!eventDetected){
                        eventStartTime=System.currentTimeMillis();
                        eventEndTime= eventPeriod + System.currentTimeMillis();
                        eventDetected=true;
                        //ToDo Send to report module
                        logger.info("New OOM event started.Ends on "+eventEndTime+
                                " Maximum of "+maxNumOfThreadDumps + " and maximum of "+maxNumOfHeapDumps
                        +" will be created.");
                    }
                    if(threadDumpCreator == null){
                        threadDumpCreator = new ThreadDumpCreator(config,remote);
                    }
                    //Checks if event has elapsed and if the number of dumps
                    //allowed elapsed
                    if(System.currentTimeMillis() < eventEndTime){
                        if(threadDumpsCreated <= maxNumOfThreadDumps){
                            threadDumpCreator.getMbeanInfo();
                            threadDumpsNames.add(threadDumpCreator.getThreadDumpName());
                            threadDumpsCreated++;
                            logger.info("Thread dump created");
                        }
                        if(heapDumpsCreated <= maxNumOfHeapDumps){
                            heapDumper = new HeapDumper(config,remote);
                            heapDumpsNames.add(heapDumper.getHeapDumpName());
                            heapDumper.start();
                            heapDumpsCreated++;
                            logger.info("Heap dump created");
                        }
                    }
                } else {
                    logger.info("Memory usage is normal " + (double) usedMemory / maxMemory);
                }
                //Event ends
                if(System.currentTimeMillis() >= eventEndTime && eventDetected){
                    setChanged();
                    notifyObservers();
                    eventDetected=false;
                    threadDumpsCreated=0;
                    heapDumpsCreated=0;
                    threadDumpsNames.clear();
                    heapDumpsNames.clear();
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

    public synchronized String  getValue(){
        String heapNames="";
        String threadNames=" ";
        for(String name:heapDumpsNames){
            heapNames+=heapNames+" "+name + " ,";
        }
        for (String name:threadDumpsNames){
            threadNames+=threadNames+" "+name+ " ,";
        }
        Date date = new Date(eventStartTime);
        String ret = "OOM Event detected at "+ date+" \n"+heapDumpsCreated + " Heap dumps created.Names of them are "+heapNames+" Available at :"+
                config.getConfigurationBean().getHeapDumpPath()
                +"\n"+ threadDumpsCreated + " Thread dumps created. Names of them are "+threadNames +". Available at :"
                +config.getConfigurationBean().getThreadDumpPath();
        return ret;
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
