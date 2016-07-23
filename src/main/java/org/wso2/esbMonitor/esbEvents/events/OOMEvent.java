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
    private ThreadDumpCreator threadDumpCreator;
    private HeapDumper heapDumper;
    private int maxNumOfThreadDumps;
    private int maxNumOfHeapDumps;
    private long threadDumpsCreated;
    private long heapDumpsCreated;
    private List<String> threadDumpsNames = new ArrayList<>();
    private List<String> heapDumpsNames = new ArrayList<>();
    private long eventPeriod;
    private long eventStartTime;

    protected OOMEvent (){
        EventConfiguration eventConfiguration= Configuration.getInstance().getEventConfigurations().
                get(ESBStatus.OOM_EVENT);
        this.threadDumpCreator= new ThreadDumpCreator(
                Configuration.getInstance(),
                new ConnectorFactory().getRemoteConnectorInstance());
        this.maxNumOfHeapDumps=eventConfiguration.getMaxHeapDumps();
        this.maxNumOfThreadDumps=eventConfiguration.getMaxThreadDumps();
        this.eventPeriod=eventConfiguration.getEventPeriod();
    }

    /**This method is called when a event is
     * finished
     * */

    public synchronized void resetEvent(){
        setChanged();
        notifyObservers();
        threadDumpsCreated=0;
        heapDumpsCreated=0;
        threadDumpsNames.clear();
        heapDumpsNames.clear();
    }

    /**This method is called when a
     *new event detected
     * */
    public synchronized void initEvent(){
        eventStartTime=System.currentTimeMillis();
        setEventEndTime(eventPeriod + System.currentTimeMillis());
        logger.info("New OOM event started.Ends on "+getEventEndTime()+
                    " Maximum of "+maxNumOfThreadDumps + " and maximum of "+maxNumOfHeapDumps
                    +" will be created.");
    }

    /**This method is called
     * during the period of event
     * */

    public synchronized void triggerEvent(){
        //Checks if event has elapsed and if the number of dumps
        //allowed elapsed
        if(!isEventPeriodElapsed()){
            if(threadDumpsCreated <= maxNumOfThreadDumps){
                threadDumpCreator.getMbeanInfo();
                threadDumpsNames.add(threadDumpCreator.getThreadDumpName());
                threadDumpsCreated++;
                logger.info("Thread dump created");
            }
            if(heapDumpsCreated <= maxNumOfHeapDumps){
                heapDumper = new HeapDumper(Configuration.getInstance(),new ConnectorFactory().getRemoteConnectorInstance());
                heapDumpsNames.add(heapDumper.getHeapDumpName());
                heapDumper.start();
                heapDumpsCreated++;
                logger.info("Heap dump created");
            }
        }
    }

    public synchronized String  getValue(){
        StringBuffer heapNames=new StringBuffer();
        StringBuffer threadNames=new StringBuffer();
        for(String name:heapDumpsNames){
            heapNames.append(name + " ,");
        }
        for (String name:threadDumpsNames){
            threadNames.append(name+ " ,");
        }
        Configuration config = Configuration.getInstance();
        Date date = new Date(eventStartTime);
        String ret = "\nOOM Event detected at "+ date+" \n"+heapDumpsCreated + " Heap dumps created.Names of them are "+
                     heapNames.toString()+" Available at :"+ config.getConfigurationBean().getHeapDumpPath()
                     +"\n"+ threadDumpsCreated + " Thread dumps created. Names of them are "+threadNames.toString() +". Available at :"
                     +config.getConfigurationBean().getThreadDumpPath();
        return ret;
    }
}
