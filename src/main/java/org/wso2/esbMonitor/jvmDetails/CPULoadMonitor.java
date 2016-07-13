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
import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.connector.RemoteConnector;
import org.wso2.esbMonitor.dumpHandlers.ThreadDumpCreator;
import javax.management.*;
import java.lang.management.OperatingSystemMXBean;

/**
 * Created by Dinanjana on 30/05/2016.
 * Finds out cpu load
 */
public class CPULoadMonitor {
    final static Logger logger = Logger.getLogger(CPULoadMonitor.class);
    private ObjectName bean = null;
    private RemoteConnector remote;
    private final String OBJECT_NAME="java.lang:type=OperatingSystem";
    //needs to be initialized from a property file
    private double cpuLoad;
    private Configuration config;
    private ThreadDumpCreator threadDumpCreator=null;

    public void initMonitor(){

    }

    public void getMbeanInfo() {
        try {
            bean = new ObjectName(OBJECT_NAME);
            checkWarningUsage(bean);

        } catch (MalformedObjectNameException e) {
            logger.error("CPULoadMonitor " + e.getMessage());
        }
    }

    private boolean checkWarningUsage(ObjectName mbean) {
        OperatingSystemMXBean operatingSystemMXBean=
                JMX.newMBeanProxy(remote.getRemote(), mbean, OperatingSystemMXBean.class);
        double cpuLoad = operatingSystemMXBean.getSystemLoadAverage();

        if (cpuLoad > this.cpuLoad) {
            logger.info(":High CPU load");
            if(threadDumpCreator == null){
                threadDumpCreator = new ThreadDumpCreator(config,remote);
            }
            if (!threadDumpCreator.isThreadDumpInProgress())
                ThreadDumpCreator.generateThreadDump();
            return true;
        } else {
            logger.info("cpu load is normal: " + cpuLoad);
        }
        return false;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public void setRemote(RemoteConnector remote) {
        this.remote = remote;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }
}
