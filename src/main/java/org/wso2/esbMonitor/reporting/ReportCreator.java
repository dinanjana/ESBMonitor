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
import org.wso2.esbMonitor.jvmDetails.CPULoadMonitor;
import org.wso2.esbMonitor.jvmDetails.MemoryMonitor;
import org.wso2.esbMonitor.network.PassThruHTTPSenderAndReciever;
import org.wso2.esbMonitor.utils.FileWriter;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dinanjana on 13/07/2016.
 */
public class ReportCreator implements Observer {
    private MemoryMonitor memoryMonitor;
    private CPULoadMonitor cpuLoadMonitor;
    private PassThruHTTPSenderAndReciever passThruHTTPSender;
    private PassThruHTTPSenderAndReciever passThruHTTPReciever;
    private PassThruHTTPSenderAndReciever passThruHTTPSSender;
    private PassThruHTTPSenderAndReciever passThruHTTPSReceiver;
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
        if(o==memoryMonitor){
            logger.info("Notified observer");
            byte[] data = ((MemoryMonitor) o).getValue().getBytes();
            FileWriter.writeFile("Report.txt",data);
        }
        if(o==cpuLoadMonitor){
            logger.info("Notified observer");
            byte[] data = ((CPULoadMonitor) o).getValue().getBytes();
            FileWriter.writeFile("Report.txt",data);
        }
    }

    public void setMemoryMonitor(MemoryMonitor memoryMonitor) {
        this.memoryMonitor = memoryMonitor;
    }

    public void setCpuLoadMonitor(CPULoadMonitor cpuLoadMonitor) {
        this.cpuLoadMonitor = cpuLoadMonitor;
    }

    public void setPassThruHTTPSender(PassThruHTTPSenderAndReciever passThruHTTPSender) {
        this.passThruHTTPSender = passThruHTTPSender;
    }

    public void setPassThruHTTPReciever(PassThruHTTPSenderAndReciever passThruHTTPReciever) {
        this.passThruHTTPReciever = passThruHTTPReciever;
    }

    public void setPassThruHTTPSSender(PassThruHTTPSenderAndReciever passThruHTTPSSender) {
        this.passThruHTTPSSender = passThruHTTPSSender;
    }

    public void setPassThruHTTPSReceiver(PassThruHTTPSenderAndReciever passThruHTTPSReceiver) {
        this.passThruHTTPSReceiver = passThruHTTPSReceiver;
    }
}
