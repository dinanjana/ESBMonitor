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
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dinanjana on 30/05/2016.
 * Finds out cpu load
 */
public class CPULoadMonitor extends JVMDetails {
    final static Logger logger = Logger.getLogger(CPULoadMonitor.class);
    private ObjectName bean = null;
    private RemoteConnector remote;
    private final String OBJECT_NAME="java.lang:type=OperatingSystem";
    //needs to be initialized from a property file
    private double cpuLoad;
    private Configuration config;
    private ThreadDumpCreator threadDumpCreator=null;
    private HeapDumper heapDumper=null;
    private int maxNumOfThreadDumps;
    private int maxNumOfHeapDumps;
    private long threadDumpsCreated;
    private long heapDumpsCreated;
    private List<String> threadDumpsNames = new ArrayList<>();
    private List<String> heapDumpsNames = new ArrayList<>();
    private long eventPeriod;
    private long eventStartTime;
    private long eventEndTime;
    private boolean eventDetected=false;

    @Override
    public void getMbeanInfo() {
        try {
            bean = new ObjectName(OBJECT_NAME);
            checkWarningUsage(bean);

        } catch (MalformedObjectNameException e) {
            logger.error("CPULoadMonitor " + e.getMessage());
        }
    }

    @Override
    public void initMonitor(){
        EventConfiguration eventConfiguration = config.getEventConfigurations().get(ESBStatus.OOM_EVENT);
        maxNumOfHeapDumps=eventConfiguration.getMaxHeapDumps();
        maxNumOfThreadDumps=eventConfiguration.getMaxThreadDumps();
        eventPeriod =eventConfiguration.getEventPeriod();
    }


    private void checkWarningUsage(ObjectName mbean) {
        OperatingSystemMXBean operatingSystemMXBean=
                JMX.newMBeanProxy(remote.getRemote(), mbean, OperatingSystemMXBean.class);
        double cpuLoad = operatingSystemMXBean.getSystemLoadAverage();

        if (cpuLoad > this.cpuLoad) {
            logger.info(":High CPU load");
            if(!eventDetected){
                eventStartTime=System.currentTimeMillis();
                eventEndTime= eventPeriod + System.currentTimeMillis();
                eventDetected=true;
                //ToDo Send to report module
                logger.info("New HIGH CPU LOAD event started.Ends on "+eventEndTime+
                        " Maximum of "+maxNumOfThreadDumps + " and maximum of "+maxNumOfHeapDumps
                        +" will be created.");
            }
            if(threadDumpCreator == null){
                threadDumpCreator = new ThreadDumpCreator(config,remote);
            }
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
            logger.info("cpu load is normal: " + cpuLoad);
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

    }

    @Override
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
        String ret = "\nHIGH CPU LOAD Event detected at "+ date+" \n"+heapDumpsCreated +
                " Heap dumps created.Names of them are "+heapNames+" Available at :"+
                config.getConfigurationBean().getHeapDumpPath()
                +"\n"+ threadDumpsCreated + " Thread dumps created. Names of them are "+threadNames +". Available at :"
                +config.getConfigurationBean().getThreadDumpPath();
        return ret;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public void setRemote(RemoteConnector remote) {
        this.remote = remote;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }
}
