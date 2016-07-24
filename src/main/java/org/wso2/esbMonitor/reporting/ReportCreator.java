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

package org.wso2.esbMonitor.reporting;

import org.apache.log4j.Logger;
import org.wso2.esbMonitor.esbEvents.Event;
import org.wso2.esbMonitor.esbEvents.events.HighCPULoadEvent;
import org.wso2.esbMonitor.esbEvents.events.HighRequestCountEvent;
import org.wso2.esbMonitor.esbEvents.events.OOMEvent;
import org.wso2.esbMonitor.esbEvents.events.UnresponsiveESBEvent;
import org.wso2.esbMonitor.jvmDetails.CPULoadMonitor;
import org.wso2.esbMonitor.jvmDetails.MemoryMonitor;
import org.wso2.esbMonitor.network.PassThruHTTPSenderAndReciever;
import org.wso2.esbMonitor.utils.FileWriter;

import java.nio.charset.Charset;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dinanjana on 13/07/2016.
 */
public class ReportCreator implements Observer {
    private OOMEvent oomEvent;
    private HighCPULoadEvent highCPULoadEvent;
    private UnresponsiveESBEvent unresponsiveESBEvent;
    private HighRequestCountEvent highRequestCountEvent;
    private static ReportCreator instance;
    private Logger logger = Logger.getLogger(ReportCreator.class);

    private ReportCreator(){

    }

    public synchronized static ReportCreator getInstance(){
        if(instance==null){
            instance=new ReportCreator();
        }
        return instance;
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        logger.info("Notification received");
        if(o==oomEvent && o instanceof OOMEvent){
            logger.info("OOM Event notified");
            byte[] data=((OOMEvent) o).getValue().getBytes(Charset.forName("UTF-8"));
            FileWriter.writeFile("Report.txt",data);
        }
        if(o==highCPULoadEvent && o instanceof HighCPULoadEvent){
            logger.info("Notified observer");
            byte[] data = ((HighCPULoadEvent) o).getValue().getBytes(Charset.forName("UTF-8"));
            FileWriter.writeFile("Report.txt",data);
        }
        if(o==highRequestCountEvent && o instanceof HighRequestCountEvent){
            logger.info("Notified observer");
            byte[] data = ((HighRequestCountEvent) o).getValue().getBytes(Charset.forName("UTF-8"));
            FileWriter.writeFile("Report.txt",data);
        }
    }

    public void setOomEvent(OOMEvent oomEvent){
        this.oomEvent=oomEvent;
    }

    public void setHighCPULoadEvent(HighCPULoadEvent highCPULoadEvent) {
        this.highCPULoadEvent = highCPULoadEvent;
    }

    public void setUnresponsiveESBEvent(UnresponsiveESBEvent unresponsiveESBEvent) {
        this.unresponsiveESBEvent = unresponsiveESBEvent;
    }

    public void setHighRequestCountEvent(HighRequestCountEvent highRequestCountEvent) {
        this.highRequestCountEvent = highRequestCountEvent;
    }
}
