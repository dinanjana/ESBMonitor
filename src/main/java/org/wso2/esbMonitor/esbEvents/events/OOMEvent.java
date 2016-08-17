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
 * Created by Dinanjana on 17/07/2016.
 * This class acts when an
 * OOM event detected
 */
public class OOMEvent extends Event {
    private final Logger logger = Logger.getLogger(OOMEvent.class);

    protected OOMEvent (){
        eventConfiguration= Configuration.getInstance().getEventConfigurations().
                get(ESBStatus.OOM_EVENT);
        this.threadDumpCreator= new ThreadDumpCreator(
                Configuration.getInstance(),
                new ConnectorFactory().getRemoteConnectorInstance());
        this.maxNumOfHeapDumps=eventConfiguration.getMaxHeapDumps();
        this.maxNumOfThreadDumps=eventConfiguration.getMaxThreadDumps();
        this.eventPeriod=eventConfiguration.getEventPeriod();
        createHeapDumps=eventConfiguration.isCreateHeapDumps();
        createThreadDumps=eventConfiguration.isCreateThreadDumps();
    }


    /**This method is called when a
     *new event detected
     * */
    public synchronized void initEvent(){
        eventStartTime=System.currentTimeMillis();
        setEventEndTime(eventPeriod + System.currentTimeMillis());
        eventDir=ESBStatus.OOM_EVENT+" "+String.valueOf(eventStartTime);
        createDir(eventDir+"/"+DIR_NAME);
        threadDumpCreator.setFilePath(eventDir+"/"+DIR_NAME+"/");
        logger.info("New OOM event started.Ends on " + getEventEndTime() +
                    " Maximum of " + maxNumOfThreadDumps + " and maximum of " + maxNumOfHeapDumps
                    + " will be created.");
    }

    /**This method is called
     * during the period of event
     * */

    public synchronized void triggerEvent(){
        //Checks if event has elapsed and if the number of dumps
        //allowed elapsed
        if(!isEventPeriodElapsed()){
            if(threadDumpsCreated <= maxNumOfThreadDumps && createThreadDumps){
                threadDumpCreator.getMbeanInfo();
                threadDumpsNames.add(threadDumpCreator.getThreadDumpName());
                threadDumpsCreated++;
                logger.info("Thread dump created");
            }
            if(heapDumpsCreated <= maxNumOfHeapDumps && createHeapDumps){
                heapDumper = new HeapDumper(Configuration.getInstance(),new ConnectorFactory().getRemoteConnectorInstance());
                heapDumpsNames.add(heapDumper.getHeapDumpName());
                heapDumper.start();
                heapDumpsCreated++;
                logger.info("Heap dump created");
            }
        }
    }

    public synchronized String  getValue(){
        StringBuffer heapNames=new StringBuffer().append("<ol>");
        StringBuffer threadNames=new StringBuffer().append("<ol>");
        String healthDet="<ol>";
        for(String name:heapDumpsNames){
            heapNames.append("<il>"+name + "</il>,");
        }
        heapNames.append("</ol>");

        for (String name:threadDumpsNames){
            threadNames.append("<il>"+name+ "</il>,");
        }
        threadNames.append("</ol>");

        if(eventConfiguration.isUsedMemory()){
            healthDet= "<il>\nUsed heap memory :"+ MemoryMonitor.getCurrentUsedMemory()/(1024*1024) + " mb</il>";
        }

        if(eventConfiguration.isCPULoad()){
            healthDet=healthDet+"<il>\nCPU load :"+ CPULoadMonitor.getCurrentCPULoad()+"</il>";
        }

        if(eventConfiguration.isNetworkLoad()){
            healthDet=healthDet+"<il>\n HTTP receiver active thread count :" + NetworkFactory.
                    getPassThruHTTPRecieverInstance().getCurrThreadCount()+"</il>";
            healthDet=healthDet+"<il>\n HTTPS receiver active thread count :" + NetworkFactory.
                    getPassThruHTTPSRecieverInstance().getCurrThreadCount()+"</il>";
        }

        healthDet=healthDet+"</ol>";
        Configuration config = Configuration.getInstance();
        Date date = new Date(eventStartTime);
        String ret = "\n\nOOM Event detected at "+ date+" \n"+heapDumpsCreated + " Heap dumps created.Names of them are "+
                     heapNames.toString()+" Available at :"+ config.getConfigurationBean().getHeapDumpPath()
                     +"\n"+ threadDumpsCreated + " Thread dumps created. Names of them are "+threadNames.toString() +". Available at :"
                     +config.getConfigurationBean().getThreadDumpPath()+ "\nOther parameters collected at the moment of " +
                     "incident are : "+healthDet;
        return ret;
    }

//    public String getEventDir() {
//        return eventDir;
//    }
}
