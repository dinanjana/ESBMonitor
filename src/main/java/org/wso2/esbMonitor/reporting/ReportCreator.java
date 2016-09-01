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
import org.wso2.esbMonitor.network.NetworkFactory;
import org.wso2.esbMonitor.network.PassThruHTTPBean;
import org.wso2.esbMonitor.utils.FileHandler;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dinanjana on 13/07/2016.
 */
public class ReportCreator implements Observer {
    private OOMEvent oomEvent;
    private HighCPULoadEvent highCPULoadEvent;
    private UnresponsiveESBEvent unresponsiveESBEvent;
    private HighRequestCountEvent [] highRequestCountEvents;
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

    private byte [] generateReportContent(ReportContent rc){
        String data0= rc.getMainContent();
        data0=reportTemplate.getReportTemplate().replace("EVENT_DESCRIPTION",data0);
        String netTrafficXaxis ="[";
        String netTrafficYaxis ="[";
        logger.info("Request queue size :" + NetworkFactory.getPassThruHTTPSRecieverInstance().getLast100networkLoad().size());
        for(PassThruHTTPBean historyData:NetworkFactory.getPassThruHTTPSRecieverInstance().getLast100networkLoad()){
            if(historyData== null){
                netTrafficXaxis=netTrafficXaxis+0+",";
                netTrafficYaxis=netTrafficYaxis+0+",";
            }else{
                netTrafficXaxis=netTrafficXaxis+"\""+historyData.getDate()+"\",";
                netTrafficYaxis=netTrafficYaxis+historyData.getActiveThreadCount()+",";
            }
        }
        netTrafficXaxis=netTrafficXaxis+"\"0\"]";
        netTrafficYaxis=netTrafficYaxis+"0]";
        System.out.println(netTrafficXaxis);
        data0=data0.replace("TIME_ARRAY",netTrafficXaxis);
        data0=data0.replace("REQUEST_COUNT_ARRAY",netTrafficYaxis);
        return data0.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        logger.info("Notification received");
        if(o==oomEvent && o instanceof OOMEvent){
            logger.info("OOM Event notified");
            ReportContent rc = ((OOMEvent) o).getValue();
//            String data0= rc.getMainContent();
//            data0=reportTemplate.getReportTemplate().replace("EVENT_DESCRIPTION",data0);
//            String netTrafficXaxis ="[";
//            String netTrafficYaxis ="[";
//            logger.info("Request queue size :" + NetworkFactory.getPassThruHTTPSRecieverInstance().getLast100networkLoad().size());
//            for(PassThruHTTPBean historyData:NetworkFactory.getPassThruHTTPSRecieverInstance().getLast100networkLoad()){
//                if(historyData== null){
//                    netTrafficXaxis=netTrafficXaxis+0+",";
//                    netTrafficYaxis=netTrafficYaxis+0+",";
//                }else{
//                    netTrafficXaxis=netTrafficXaxis+"\""+historyData.getDate()+"\",";
//                    netTrafficYaxis=netTrafficYaxis+historyData.getActiveThreadCount()+",";
//                }
//            }
//            netTrafficXaxis=netTrafficXaxis+"\"0\"]";
//            netTrafficYaxis=netTrafficYaxis+"0]";
//            System.out.println(netTrafficXaxis);
//            data0=data0.replace("TIME_ARRAY",netTrafficXaxis);
//            data0=data0.replace("REQUEST_COUNT_ARRAY",netTrafficYaxis);
//            byte[] data=data0.getBytes(Charset.forName("UTF-8"));
            byte [] data = generateReportContent(rc);
            FileHandler.writeFile(((OOMEvent) o).getEventDir()+"/Report.html", data);
        }
        else if(o==highCPULoadEvent && o instanceof HighCPULoadEvent){
            logger.info("Notified observer");
            ReportContent rc =((HighCPULoadEvent) o).getValue();
            byte[] data=generateReportContent(rc);
            FileHandler.writeFile(((HighCPULoadEvent) o).getEventDir()+"/Report.html", data);
        }

        else if(o==unresponsiveESBEvent && o instanceof UnresponsiveESBEvent){
            logger.info("Notified observer");
            ReportContent rc =((UnresponsiveESBEvent) o).getValue();
            byte[] data=generateReportContent(rc);
            FileHandler.writeFile(((UnresponsiveESBEvent) o).getEventDir()+"/Report.html", data);
        }else {
            if(o instanceof HighRequestCountEvent){
                logger.info("Notified observer on high request count");
                for (HighRequestCountEvent highRequestCountEvent:highRequestCountEvents){
                    if(o==highRequestCountEvent){
                        ReportContent rc = ((HighRequestCountEvent) o).getValue();
                        byte[] data=generateReportContent(rc);
                        FileHandler.writeFile(((HighRequestCountEvent) o).getEventDir()+"/Report.html", data);
                        break;
                    }
                }
            }
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

    public void setHighRequestCountEvent(HighRequestCountEvent []highRequestCountEvent) {
        this.highRequestCountEvents = highRequestCountEvent;
    }
}
