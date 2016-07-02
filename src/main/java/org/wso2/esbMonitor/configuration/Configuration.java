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

package org.wso2.esbMonitor.configuration;

import org.apache.log4j.Logger;
import org.wso2.esbMonitor.connector.RemoteConnector;
import org.wso2.esbMonitor.dumpHandlers.HeapDumper;
import org.wso2.esbMonitor.dumpHandlers.ThreadDumpCreator;
import org.wso2.esbMonitor.esbEvents.ESBEvent;
import org.wso2.esbMonitor.jvmDetails.CPULoadMonitor;
import org.wso2.esbMonitor.jvmDetails.MemoryMonitor;
import org.wso2.esbMonitor.network.PassThruHTTPSenderAndReciever;
import org.wso2.esbMonitor.pingReceiver.PingHandler;
import org.wso2.esbMonitor.tasks.*;

import java.io.InputStream;
import java.util.*;

/**
 * Created by Dinanjana on 31/05/2016.
 * Reads property file
 * to initiate tasks
 */
public class Configuration {
    private Logger logger = Logger.getLogger(Configuration.class);
    private final String FILE_NAME = "wso2esbfr.properties";
    private ConfigurationBean configurationBean;
    private EventConfiguration eventConfiguration;

    private String JMXURL="service:jmx:rmi://localhost:11111/jndi/rmi://localhost:9999/jmxrmi";
    private String USERNAME="admin";
    private String PASSWORD="admin";

    private Map<ESBEvent,EventConfiguration> eventConfigurations = new HashMap<>();


    public void initProperties(){
        configurationBean = new ConfigurationBean();
        readPropFile();
//        DBTaskRunner.setWaitTime(DB_TASK);
//        JVMTaskRunner.setWaitTime(JVM_TASK);
//        NetworkMonitor.setWaitTime(NETWORK_TASK);
//        HeapDumper.setFileName(HEAP_DUMP_PATH+"/");
//        MemoryMonitor.setMemory(MEMORY_USAGE);
//        CPULoadMonitor.setCpuLoad(CPU_USAGE);
//        PassThruHTTPSenderAndReciever.setMaxQueueSize(MAX_REQESTQUEUE_SIZE);
//        PassThruHTTPSenderAndReciever.setMaxThreadCount(HTTP_REQUESTS);
//        RemoteConnector.setJmxurl(JMXURL);
//        RemoteConnector.setUsername(USERNAME);
//        RemoteConnector.setPassword(PASSWORD);
//        DBCleanerTask.setWaitTime(DB_CLEANER_TASK);
//        ThreadDumpCreator.setFilePath(THREAD_DUMP_PATH);
//        PingHandler.setPort(PING_RECEIVING_PORT);
//        ESBStatusCheckerTask.setWaitTime(PING_DELAY);
    }

    private void readPropFile(){
        try {
            Properties prop = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                return;
            }

            if(prop.getProperty("DB_TASK_INTERVAL") != null){
                configurationBean.setDbTask(Long.parseLong(prop.getProperty("DB_TASK_INTERVAL")));
                logger.info("Added db task interval "+ configurationBean.getDbTask());
            }

            if(prop.getProperty("JVM_TASK_INTERVAL")!= null){
                configurationBean.setJvmTask(Long.parseLong(prop.getProperty("JVM_TASK_INTERVAL")));
                logger.info("Added jvm task interval "+ configurationBean.getJvmTask());
            }

            if(prop.getProperty("NETWORK_TASK_INTERVAL") != null){
                configurationBean.setNetworkTask(Long.parseLong(prop.getProperty("NETWORK_TASK_INTERVAL")));
                logger.info("Added network task interval "+ configurationBean.getNetworkTask());
            }

            if(prop.getProperty("HEAP_DUMP_PATH") != null){
                configurationBean.setHeapDumpPath(prop.getProperty("HEAP_DUMP_PATH"));
                logger.info("Added heap dump path "+ configurationBean.getHeapDumpPath());
            }

            if(prop.getProperty("EMAIL_ADDRESS")!= null){
                configurationBean.setEmailAddress(prop.getProperty("EMAIL_ADDRESS"));
                logger.info("Added Email address "+ configurationBean.getEmailAddress());
            }

            if(prop.getProperty("MAX_MEMORY_USAGE")!= null){
                configurationBean.setMemoryUsage(Double.parseDouble(prop.getProperty("MAX_MEMORY_USAGE")));
                logger.info("Added max memory usage "+ configurationBean.getEmailAddress());
            }

            if(prop.getProperty("MAX_CPU_USAGE")!=null){
                configurationBean.setCpuUsage(Double.parseDouble(prop.getProperty("MAX_CPU_USAGE")));
                logger.info("Added max CPU usage " + configurationBean.getCpuUsage());
            }

            if(prop.getProperty("MAX_REQUEST_QUEUE_SIZE") != null){
                configurationBean.setMaxReqestqueueSize(Integer.parseInt(prop.getProperty("MAX_REQUEST_QUEUE_SIZE")));
                logger.info("Added max request queue size "+configurationBean.getMaxReqestqueueSize());
            }

            if(prop.getProperty("MAX_HTTP_REQUESTS") != null){
                configurationBean.setHttpRequests(Integer.parseInt(prop.getProperty("MAX_HTTP_REQUESTS")));
                logger.info("Added max http requests "+ configurationBean.getHttpRequests());
            }

            if(prop.getProperty("JMX_SERVICE_URL") != null){
                JMXURL=prop.getProperty("JMX_SERVICE_URL");
                logger.info("Added JMX URL" + JMXURL);
            }

            if(prop.getProperty("JMX_USER") != null){
                USERNAME=prop.getProperty("JMX_USER");
                logger.info("Added JMX user "+USERNAME);
            }

            if(prop.getProperty("JMX_USER_PASSWORD") != null){
                PASSWORD=prop.getProperty("JMX_USER_PASSWORD");
                logger.info("Added JMX user password"+PASSWORD);
            }

            if(prop.getProperty("DB_CLEANER_TASK") != null){
                configurationBean.setDbCleanerTask(Long.parseLong(prop.getProperty("DB_CLEANER_TASK")));
                logger.info("Added DB cleaner task wait time "+ configurationBean.getDbCleanerTask());
            }

            if(prop.getProperty("THREAD_DUMP_PATH") != null){
                configurationBean.setThreadDumpPath(prop.getProperty("THREAD_DUMP_PATH"));
                logger.info("Added thread dump path" + configurationBean.getThreadDumpPath());
            }

            if(prop.getProperty("PING_RECEIVING_PORT") != null){
                configurationBean.setPingReceivingPort(Integer.parseInt(prop.getProperty("PING_RECEIVING_PORT")));
                logger.info("Added ping receiving port " + configurationBean.getPingReceivingPort());
            }

            if(prop.getProperty("PING_DELAY") != null){
                configurationBean.setPingDelay(Long.parseLong(prop.getProperty("PING_DELAY")));
                logger.info("Added ping delay " + configurationBean.getPingDelay());
            }



        }catch (Exception e){
            logger.error("Property file error",e);
        }

    }

    public String getJMXURL() {
        return JMXURL;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public ConfigurationBean getConfigurationBean() {
        return configurationBean;
    }

    public Map<ESBEvent, EventConfiguration> getEventConfigurations() {
        return eventConfigurations;
    }
}
