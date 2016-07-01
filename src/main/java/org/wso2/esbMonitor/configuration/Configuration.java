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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Dinanjana on 31/05/2016.
 * Reads property file
 * to initiate tasks
 */
public class Configuration {
    private Logger logger = Logger.getLogger(Configuration.class);
    private final String FILE_NAME = "wso2esbfr.properties";
    private long DB_TASK = 3000;
    private long JVM_TASK = 3000;
    private long NETWORK_TASK = 3000;
    private String HEAP_DUMP_PATH;
    private String EMAIL_ADDRESS;
    private String JMXURL="service:jmx:rmi://localhost:11111/jndi/rmi://localhost:9999/jmxrmi";
    private String USERNAME="admin";
    private String PASSWORD="admin";
    private Double MEMORY_USAGE = 0.7;
    private Double CPU_USAGE = 0.7;
    private int HTTP_REQUESTS = 100;
    private int MAX_REQESTQUEUE_SIZE = 100;
    private long DB_CLEANER_TASK = 24L;
    private String THREAD_DUMP_PATH="ThreadDumps//";
    private int PING_RECEIVING_PORT=9090;
    private long PING_DELAY=3000;
    private List<EventConfiguration> eventConfigurations = new ArrayList<>();


    public void initProperties(){
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
//        PingHandler.setPORT(PING_RECEIVING_PORT);
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
                DB_TASK= Long.parseLong(prop.getProperty("DB_TASK_INTERVAL"));
                logger.info("Added db task interval "+ DB_TASK);
            }

            if(prop.getProperty("JVM_TASK_INTERVAL")!= null){
                JVM_TASK= Long.parseLong(prop.getProperty("JVM_TASK_INTERVAL"));
                logger.info("Added jvm task interval "+ JVM_TASK);
            }

            if(prop.getProperty("NETWORK_TASK_INTERVAL") != null){
                NETWORK_TASK= Long.parseLong(prop.getProperty("NETWORK_TASK_INTERVAL"));
                logger.info("Added network task interval "+ NETWORK_TASK);
            }

            if(prop.getProperty("HEAP_DUMP_PATH") != null){
                HEAP_DUMP_PATH=prop.getProperty("HEAP_DUMP_PATH");
                logger.info("Added heap dump path "+ HEAP_DUMP_PATH);
            }

            if(prop.getProperty("EMAIL_ADDRESS")!= null){
                EMAIL_ADDRESS=prop.getProperty("EMAIL_ADDRESS");
                logger.info("Added Email address "+ EMAIL_ADDRESS);
            }

            if(prop.getProperty("MAX_MEMORY_USAGE")!= null){
                MEMORY_USAGE=Double.parseDouble(prop.getProperty("MAX_MEMORY_USAGE"));
                logger.info("Added max memory usage "+ MEMORY_USAGE);
            }

            if(prop.getProperty("MAX_CPU_USAGE")!=null){
                CPU_USAGE=Double.parseDouble(prop.getProperty("MAX_CPU_USAGE"));
                logger.info("Added max CPU usage " + CPU_USAGE);
            }

            if(prop.getProperty("MAX_REQUEST_QUEUE_SIZE") != null){
                MAX_REQESTQUEUE_SIZE=Integer.parseInt(prop.getProperty("MAX_REQUEST_QUEUE_SIZE"));
                logger.info("Added max request queue size "+MAX_REQESTQUEUE_SIZE);
            }

            if(prop.getProperty("MAX_HTTP_REQUESTS") != null){
                HTTP_REQUESTS=Integer.parseInt(prop.getProperty("MAX_HTTP_REQUESTS"));
                logger.info("Added max http requests "+ HTTP_REQUESTS);
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
                DB_CLEANER_TASK=Long.parseLong(prop.getProperty("DB_CLEANER_TASK"));
                logger.info("Added DB cleaner task wait time "+ DB_CLEANER_TASK);
            }

            if(prop.getProperty("THREAD_DUMP_PATH") != null){
                THREAD_DUMP_PATH=prop.getProperty("THREAD_DUMP_PATH");
                logger.info("Added thread dump path" + THREAD_DUMP_PATH);
            }

            if(prop.getProperty("PING_RECEIVING_PORT") != null){
                PING_RECEIVING_PORT=Integer.parseInt(prop.getProperty("PING_RECEIVING_PORT"));
                logger.info("Added ping receiving port " +PING_RECEIVING_PORT);
            }

            if(prop.getProperty("PING_DELAY") != null){
                PING_DELAY=Long.parseLong(prop.getProperty("PING_DELAY"));
                logger.info("Added ping delay " + PING_DELAY);
            }



        }catch (Exception e){
            logger.error("Property file error",e);
        }

    }

    class EventConfiguration{


    }

    public String getFILE_NAME() {
        return FILE_NAME;
    }

    public long getDB_TASK() {
        return DB_TASK;
    }

    public long getJVM_TASK() {
        return JVM_TASK;
    }

    public long getNETWORK_TASK() {
        return NETWORK_TASK;
    }

    public String getHEAP_DUMP_PATH() {
        return HEAP_DUMP_PATH;
    }

    public String getEMAIL_ADDRESS() {
        return EMAIL_ADDRESS;
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

    public Double getMEMORY_USAGE() {
        return MEMORY_USAGE;
    }

    public Double getCPU_USAGE() {
        return CPU_USAGE;
    }

    public int getHTTP_REQUESTS() {
        return HTTP_REQUESTS;
    }

    public int getMAX_REQESTQUEUE_SIZE() {
        return MAX_REQESTQUEUE_SIZE;
    }

    public long getDB_CLEANER_TASK() {
        return DB_CLEANER_TASK;
    }

    public String getTHREAD_DUMP_PATH() {
        return THREAD_DUMP_PATH;
    }

    public int getPING_RECEIVING_PORT() {
        return PING_RECEIVING_PORT;
    }

    public long getPING_DELAY() {
        return PING_DELAY;
    }
}
