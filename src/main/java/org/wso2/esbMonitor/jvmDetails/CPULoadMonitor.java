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
import org.wso2.esbMonitor.connector.RemoteConnector;
import org.wso2.esbMonitor.esbEvents.events.EventFactory;
import org.wso2.esbMonitor.esbEvents.events.HighCPULoadEvent;

import javax.management.*;
import java.lang.management.OperatingSystemMXBean;

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
    private boolean eventDetected=false;
    private HighCPULoadEvent highCPULoadEvent;
    private static double currentCPULoad;

    protected CPULoadMonitor(){}

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
        highCPULoadEvent= EventFactory.getHighCPULoadEventInstance();
    }


    private void checkWarningUsage(ObjectName mbean) {
        OperatingSystemMXBean operatingSystemMXBean=
                JMX.newMBeanProxy(remote.getRemote(), mbean, OperatingSystemMXBean.class);
        //If the load average is not available, a negative value is returned.
        double cpuLoad = operatingSystemMXBean.getSystemLoadAverage();
        currentCPULoad=cpuLoad;
        if (cpuLoad < this.cpuLoad) {
            logger.info(":High CPU load");
            if(!eventDetected){
                highCPULoadEvent.initEvent();
                eventDetected=true;
            }
            highCPULoadEvent.triggerEvent();
        } else {
            logger.info("cpu load is normal: " + cpuLoad);
        }
        //Event ends
        if(highCPULoadEvent.isEventPeriodElapsed() && eventDetected){
            eventDetected=false;
            highCPULoadEvent.resetEvent();
            logger.info("High CPU Load Event ended on " +System.currentTimeMillis());
        }

    }

    @Override
    public synchronized String  getValue(){
        return highCPULoadEvent.getValue();
    }

    public synchronized static double getCurrentCPULoad(){
        return currentCPULoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public void setRemote(RemoteConnector remote) {
        this.remote = remote;
    }

}
