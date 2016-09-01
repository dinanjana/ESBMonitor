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

package org.wso2.esbMonitor.esbEvents;

import org.wso2.esbMonitor.configuration.EventConfiguration;
import org.wso2.esbMonitor.dumpHandlers.HeapDumper;
import org.wso2.esbMonitor.dumpHandlers.ThreadDumpCreator;
import org.wso2.esbMonitor.reporting.ReportContent;
import org.wso2.esbMonitor.utils.FileHandler;
import org.wso2.esbMonitor.utils.ZipArchiveCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Dinanjana on 17/07/2016.
 */
public abstract class Event extends Observable{
    protected ThreadDumpCreator threadDumpCreator;
    protected HeapDumper heapDumper;
    protected int maxNumOfThreadDumps;
    protected int maxNumOfHeapDumps;
    protected long threadDumpsCreated;
    protected long heapDumpsCreated;
    protected List<String> threadDumpsNames = new ArrayList<>();
    protected List<String> heapDumpsNames = new ArrayList<>();
    protected long eventPeriod;
    protected long eventStartTime;
    protected EventConfiguration eventConfiguration;
    protected boolean createThreadDumps;
    protected boolean createHeapDumps;
    protected final String DIR_NAME="Thread dumps";
    protected String eventDir=null;

    private long eventEndtime;
    /**This method is called when a
     *new event detected
     * */
    public abstract void initEvent();
    /**This method is called
     * during the period of event
     * */
    public abstract void triggerEvent();
    /**This method is called when a event is
     * finished
     * */
    public void resetEvent(){
        setChanged();
        notifyObservers();
        threadDumpsCreated=0;
        heapDumpsCreated=0;
        threadDumpsNames.clear();
        heapDumpsNames.clear();
        ZipArchiveCreator zip = new ZipArchiveCreator("./reports/"+eventDir+".zip","./"+eventDir);
        zip.generateFileList(new File("./"+eventDir));
        zip.zipIt();
    }

    public abstract ReportContent getValue();

    public boolean isEventPeriodElapsed(){
        return eventEndtime <= System.currentTimeMillis();
    }

    public void setEventEndTime(long eventEndtime){
        this.eventEndtime=eventEndtime;
    }

    public long getEventEndTime(){
        return this.eventEndtime;
    }

    /**
     * Creates a new directory to save info of
     * this event
     * @param  dirName new directory's name*/
    public void createDir(String dirName){
        FileHandler.createDir(dirName, false);
    }

    public String getEventDir() {
        return eventDir;
    }
}
