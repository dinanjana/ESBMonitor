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
import org.wso2.esbMonitor.esbEvents.events.HighCPULoadEvent;
import org.wso2.esbMonitor.esbEvents.events.HighRequestCountEvent;
import org.wso2.esbMonitor.esbEvents.events.OOMEvent;
import org.wso2.esbMonitor.esbEvents.events.UnresponsiveESBEvent;
import org.wso2.esbMonitor.utils.FileHandler;

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
    private ReportTemplate reportTemplate = ReportTemplate.getReportTemplateInstance();

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
            String data0=((OOMEvent) o).getValue();
            data0=reportTemplate.getReportTemplate().replace("EVENT_DESCRIPTION",data0);
            byte[] data=data0.getBytes(Charset.forName("UTF-8"));
            FileHandler.writeFile(((OOMEvent) o).getEventDir()+"/Report.html", data);
        }
        if(o==highCPULoadEvent && o instanceof HighCPULoadEvent){
            logger.info("Notified observer");
            String data0=((HighCPULoadEvent) o).getValue();
            data0=reportTemplate.getReportTemplate().replace("EVENT_DESCRIPTION",data0);
            byte[] data=data0.getBytes(Charset.forName("UTF-8"));
            FileHandler.writeFile(((HighCPULoadEvent) o).getEventDir()+"/Report.html", data);
        }
        if(o==highRequestCountEvent && o instanceof HighRequestCountEvent){
            logger.info("Notified observer");
            String data0=((HighRequestCountEvent) o).getValue();
            data0=reportTemplate.getReportTemplate().replace("EVENT_DESCRIPTION",data0);
            byte[] data=data0.getBytes(Charset.forName("UTF-8"));
            FileHandler.writeFile(((HighRequestCountEvent) o).getEventDir()+"/Report.html", data);
        }
        if(o==unresponsiveESBEvent && o instanceof UnresponsiveESBEvent){
            logger.info("Notified observer");
            String data0=((UnresponsiveESBEvent) o).getValue();
            data0=reportTemplate.getReportTemplate().replace("EVENT_DESCRIPTION",data0);
            byte[] data=data0.getBytes(Charset.forName("UTF-8"));
            FileHandler.writeFile(((UnresponsiveESBEvent) o).getEventDir()+"/Report.html", data);
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
