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

package org.wso2.esbMonitor.esbEvents.events;

import org.apache.log4j.Logger;
import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.configuration.EventConfiguration;
import org.wso2.esbMonitor.connector.ConnectorFactory;
import org.wso2.esbMonitor.dumpHandlers.HeapDumper;
import org.wso2.esbMonitor.dumpHandlers.ThreadDumpCreator;
import org.wso2.esbMonitor.esbEvents.ESBStatus;
import org.wso2.esbMonitor.esbEvents.Event;
import org.wso2.esbMonitor.jvmDetails.CPULoadMonitor;
import org.wso2.esbMonitor.jvmDetails.MemoryMonitor;
import org.wso2.esbMonitor.network.NetworkFactory;
import org.wso2.esbMonitor.utils.ZipArchiveCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dinanjana on 23/07/2016.
 */
public class HighRequestCountEvent extends Event {
    private final Logger logger = Logger.getLogger(HighRequestCountEvent.class);

    protected HighRequestCountEvent(){
        eventConfiguration= Configuration.getInstance().getEventConfigurations().
                get(ESBStatus.HIGH_REQUEST_COUNT);
        this.threadDumpCreator= new ThreadDumpCreator(
                Configuration.getInstance(),
                new ConnectorFactory().getRemoteConnectorInstance());
        this.maxNumOfHeapDumps=eventConfiguration.getMaxHeapDumps();
        this.maxNumOfThreadDumps=eventConfiguration.getMaxThreadDumps();
        this.eventPeriod=eventConfiguration.getEventPeriod();
        createHeapDumps=eventConfiguration.isCreateHeapDumps();
        createThreadDumps=eventConfiguration.isCreateThreadDumps();
    }
    @Override
    public void initEvent() {
        eventStartTime=System.currentTimeMillis();
        setEventEndTime(eventPeriod + System.currentTimeMillis());
        eventDir=ESBStatus.HIGH_REQUEST_COUNT+" "+String.valueOf(eventStartTime);
        createDir(eventDir+"/"+DIR_NAME);
        threadDumpCreator.setFilePath(eventDir+"/"+DIR_NAME+"/");
        logger.info("New High Request Count event started.Ends on "+getEventEndTime()+
                    " Maximum of "+maxNumOfThreadDumps + " and maximum of "+maxNumOfHeapDumps
                    +" will be created. Directory is" + eventDir );
    }

    @Override
    public void triggerEvent() {
        //Checks if event has elapsed and if the number of dumps
        //allowed elapsed
        if(!isEventPeriodElapsed()){
            if(threadDumpsCreated <= maxNumOfThreadDumps && createThreadDumps){
                threadDumpCreator.getMbeanInfo();
                threadDumpsNames.add(threadDumpCreator.getThreadDumpName());
                threadDumpsCreated++;
                logger.info("Thread dump created for high cpu load");
            }
            if(heapDumpsCreated <= maxNumOfHeapDumps && createHeapDumps){
                heapDumper = new HeapDumper(Configuration.getInstance(),new ConnectorFactory().getRemoteConnectorInstance());
                heapDumpsNames.add(heapDumper.getHeapDumpName());
                heapDumper.start();
                heapDumpsCreated++;
                logger.info("Heap dump created for high cpu load");
            }
        }
    }


    @Override
    public synchronized String  getValue(){
        StringBuffer heapNames=new StringBuffer();
        StringBuffer threadNames=new StringBuffer();
        String healthDet="";
        for(String name:heapDumpsNames){
            heapNames.append(name + " ,");
        }
        for (String name:threadDumpsNames){
            threadNames.append(name+ " ,");
        }
        if(eventConfiguration.isUsedMemory()){
            healthDet= "\nUsed heap memory :"+ MemoryMonitor.getCurrentUsedMemory()/(1024*1024) +" mb";
        }
        if(eventConfiguration.isCPULoad()){
            healthDet=healthDet+"\nCPU load :"+ CPULoadMonitor.getCurrentCPULoad();
        }
        if(eventConfiguration.isNetworkLoad()){
            healthDet=healthDet+"\n HTTP receiver active thread count :" + NetworkFactory.
                    getPassThruHTTPRecieverInstance().getCurrThreadCount();
            healthDet=healthDet+"\n HTTPS receiver active thread count :" + NetworkFactory.
                    getPassThruHTTPSRecieverInstance().getCurrThreadCount();
        }
        Configuration config = Configuration.getInstance();
        Date date = new Date(eventStartTime);
        String ret = "\n\nHigh request count Event detected at "+ date+" \n"+heapDumpsCreated + " Heap dumps created.Names of them are "+
                     heapNames.toString()+" Available at :"+ config.getConfigurationBean().getHeapDumpPath()
                     +"\n"+ threadDumpsCreated + " Thread dumps created. Names of them are "+threadNames.toString() +". Available at :"
                     +config.getConfigurationBean().getThreadDumpPath()+ "Other parameters collected at the moment of " +
                     "incident are : "+healthDet;

        return ret;
    }
}
