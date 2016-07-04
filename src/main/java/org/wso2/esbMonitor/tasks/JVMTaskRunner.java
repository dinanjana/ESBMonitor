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

package org.wso2.esbMonitor.tasks;

import org.apache.log4j.Logger;
import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.connector.RemoteConnector;
import org.wso2.esbMonitor.connector.ConnectorFactory;
import org.wso2.esbMonitor.jvmDetails.CPULoadMonitor;
import org.wso2.esbMonitor.jvmDetails.MemoryMonitor;

/**
 * Created by Dinanjana on 30/05/2016.
 */
public class JVMTaskRunner extends Thread{

    private Logger logger = Logger.getLogger(JVMTaskRunner.class);
    private static RemoteConnector remoteConnector;
    private Configuration configuration;
    private MemoryMonitor memoryMonitor = new MemoryMonitor();
    private CPULoadMonitor cpuLoadMonitor = new CPULoadMonitor();

    protected JVMTaskRunner(){
        configuration = Configuration.getInstance();
        ConnectorFactory connectorFactory = new ConnectorFactory();
        remoteConnector= connectorFactory.getRemoteConnectorInstance();
    }

    private void initTask(){
        memoryMonitor.setRemote(remoteConnector);
        memoryMonitor.setMemory(configuration.getConfigurationBean().getMemoryUsage());
        memoryMonitor.setConfig(configuration);
        cpuLoadMonitor.setRemote(remoteConnector);
        cpuLoadMonitor.setCpuLoad(configuration.getConfigurationBean().getCpuUsage());
        cpuLoadMonitor.setConfig(configuration);
    }

    public void run(){
        initTask();
        while (true){
            try {
                memoryMonitor.getMbeanInfo();
                cpuLoadMonitor.getMbeanInfo();
                Thread.sleep(configuration.getConfigurationBean().getJvmTask());
            } catch (InterruptedException e) {
                logger.error("Thread wait Exception",e);
            }
        }
    }

}
