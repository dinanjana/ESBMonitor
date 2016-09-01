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
import org.wso2.esbMonitor.reporting.ReportContent;
import org.wso2.esbMonitor.utils.ZipArchiveCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dinanjana on 23/07/2016.
 */
public class HighCPULoadEvent extends Event{
    private final Logger logger = Logger.getLogger(HighCPULoadEvent.class);
//    private ThreadDumpCreator threadDumpCreator;
//    private HeapDumper heapDumper;
//    private int maxNumOfThreadDumps;
//    private int maxNumOfHeapDumps;
//    private long threadDumpsCreated;
//    private long heapDumpsCreated;
//    private List<String> threadDumpsNames = new ArrayList<>();
//    private List<String> heapDumpsNames = new ArrayList<>();
//    private long eventPeriod;
//    private long eventStartTime;
//    private EventConfiguration eventConfiguration;
//    private  boolean createThreadDumps;
//    private boolean createHeapDumps;
//    private final String DIR_NAME="Thread dumps";
//    private String eventDir=null;

    protected HighCPULoadEvent (){
        eventConfiguration= Configuration.getInstance().getEventConfigurations().
                get(ESBStatus.HIGH_CPU_LOAD);
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
        eventDir=ESBStatus.HIGH_CPU_LOAD+" "+String.valueOf(eventStartTime);
        createDir(eventDir+"/"+DIR_NAME);
        threadDumpCreator.setFilePath(eventDir+"/"+DIR_NAME+"/");
        logger.info("New High CPU Load event started.Ends on "+getEventEndTime()+
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

    public synchronized ReportContent getValue(){
        StringBuffer heapNames=new StringBuffer().append("<ol>");
        StringBuffer threadNames=new StringBuffer().append("<ol>");
        StringBuffer threadTab=new StringBuffer().append("<ul class=\"tab\">");
        StringBuffer threadDumpPanel=new StringBuffer();
        String healthDet="<ol>";
        for(String name:heapDumpsNames){
            heapNames.append("<il>"+name + "</il>,");
        }
        heapNames.append("</ol>");

        for (String name:threadDumpsNames){
            threadNames.append("<il>"+name+ "</il>,");
            threadTab.append("<li><a href=\"#\" class=\"tablinks\" onclick=\"openCity" +
                             "(event, '"+name+"')\">"+name+"</a></li>");
            threadDumpPanel.append("<div id=\""+name+"\" class=\"tabcontent\">\n" +
                                   "  <iframe src=\""+DIR_NAME+"/"+name+"\" width=\"1000\" height=\"200\"></iframe>\n" +
                                   "</div>");
        }
        threadNames.append("</ol>");
        threadTab.append("</ul>");

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
        String ret = "\n\nPossible High CPU Load event detected at "+ date+"<br>\n"+heapDumpsCreated + " Heap dumps created.Names of them are "+
                     heapNames.toString()+" <br>Available at :"+ config.getConfigurationBean().getHeapDumpPath()
                     +"<br>\n"+ threadDumpsCreated + " Thread dumps created. Names of them are "+threadNames.toString() +"Available at :"
                     +eventDir+"/"+DIR_NAME+ "<br>\nOther parameters collected at the moment of " +
                     "incident are : "+healthDet+threadTab+threadDumpPanel;
        return new ReportContent(ret,null);
    }
//    public String getEventDir() {
//        return eventDir;
//    }
}
