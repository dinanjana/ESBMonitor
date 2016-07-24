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
import org.wso2.esbMonitor.esbEvents.ESBStatus;

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
    private String jmxurl ="service:jmx:rmi://localhost:11111/jndi/rmi://localhost:9999/jmxrmi";
    private String username ="admin";
    private String password ="admin";
    private Map<ESBStatus,EventConfiguration> eventConfigurations = new HashMap<>();
    private Properties properties;
    private static Configuration instance;


    private Configuration(){

    }

    public static synchronized Configuration getInstance(){
        if(instance==null){
            instance=new Configuration();
            instance.configurationBean = new ConfigurationBean();
            instance.readPropFile(instance.FILE_NAME);
            instance.addProperties(instance.properties);
        }
        return instance;

    }

    private void readPropFile(String fileName){
        try {
            instance.properties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            if (inputStream != null) {
                properties.load(inputStream);
            }
        }catch (Exception e){
            logger.error("Property file error",e);
        }
    }

    private void addProperties(Properties prop){
        if(prop.getProperty("DB_TASK_INTERVAL") != null){
            instance.configurationBean.setDbTask(Long.parseLong(prop.getProperty("DB_TASK_INTERVAL")));
            logger.info("Added db task interval "+ configurationBean.getDbTask());
        }

        if(prop.getProperty("JVM_TASK_INTERVAL")!= null){
            instance.configurationBean.setJvmTask(Long.parseLong(prop.getProperty("JVM_TASK_INTERVAL")));
            logger.info("Added jvm task interval "+ configurationBean.getJvmTask());
        }

        if(prop.getProperty("NETWORK_TASK_INTERVAL") != null){
            instance.configurationBean.setNetworkTask(Long.parseLong(prop.getProperty("NETWORK_TASK_INTERVAL")));
            logger.info("Added network task interval "+ configurationBean.getNetworkTask());
        }

        if(prop.getProperty("HEAP_DUMP_PATH") != null){
            instance.configurationBean.setHeapDumpPath(prop.getProperty("HEAP_DUMP_PATH"));
            logger.info("Added heap dump path "+ configurationBean.getHeapDumpPath());
        }

        if(prop.getProperty("EMAIL_ADDRESS")!= null){
            instance.configurationBean.setEmailAddress(prop.getProperty("EMAIL_ADDRESS"));
            logger.info("Added Email address "+ configurationBean.getEmailAddress());
        }

        if(prop.getProperty("MAX_MEMORY_USAGE")!= null){
            instance.configurationBean.setMemoryUsage(Double.parseDouble(prop.getProperty("MAX_MEMORY_USAGE")));
            logger.info("Added max memory usage "+ configurationBean.getEmailAddress());
        }

        if(prop.getProperty("MAX_CPU_USAGE")!=null){
            instance.configurationBean.setCpuUsage(Double.parseDouble(prop.getProperty("MAX_CPU_USAGE")));
            logger.info("Added max CPU usage " + configurationBean.getCpuUsage());
        }

        if(prop.getProperty("MAX_REQUEST_QUEUE_SIZE") != null){
            instance.configurationBean.setMaxReqestqueueSize(Integer.parseInt(prop.getProperty("MAX_REQUEST_QUEUE_SIZE")));
            logger.info("Added max request queue size "+configurationBean.getMaxReqestqueueSize());
        }

        if(prop.getProperty("MAX_HTTP_REQUESTS") != null){
            instance.configurationBean.setHttpRequests(Integer.parseInt(prop.getProperty("MAX_HTTP_REQUESTS")));
            logger.info("Added max http requests "+ configurationBean.getHttpRequests());
        }

        if(prop.getProperty("JMX_SERVICE_URL") != null){
            instance.jmxurl =prop.getProperty("JMX_SERVICE_URL");
            logger.info("Added JMX URL" + jmxurl);
        }

        if(prop.getProperty("JMX_USER") != null){
            instance.username =prop.getProperty("JMX_USER");
            logger.info("Added JMX user "+ username);
        }

        if(prop.getProperty("JMX_USER_PASSWORD") != null){
            instance.password =prop.getProperty("JMX_USER_PASSWORD");
            logger.info("Added JMX user password"+ password);
        }

        if(prop.getProperty("DB_CLEANER_TASK") != null){
            instance.configurationBean.setDbCleanerTask(Long.parseLong(prop.getProperty("DB_CLEANER_TASK")));
            logger.info("Added DB cleaner task wait time "+ configurationBean.getDbCleanerTask());
        }

        if(prop.getProperty("THREAD_DUMP_PATH") != null){
            instance.configurationBean.setThreadDumpPath(prop.getProperty("THREAD_DUMP_PATH"));
            logger.info("Added thread dump path" + configurationBean.getThreadDumpPath());
        }

        if(prop.getProperty("PING_RECEIVING_PORT") != null){
            instance.configurationBean.setPingReceivingPort(Integer.parseInt(prop.getProperty("PING_RECEIVING_PORT")));
            logger.info("Added ping receiving port " + configurationBean.getPingReceivingPort());
        }

        if(prop.getProperty("PING_DELAY") != null){
            instance.configurationBean.setPingDelay(Long.parseLong(prop.getProperty("PING_DELAY")));
            logger.info("Added ping delay " + configurationBean.getPingDelay());
        }
        EventConfiguration eventConfiguration = new EventConfiguration(ESBStatus.OOM_EVENT,"wso2esbfrOOMevent.properties");
        instance.eventConfigurations.put(ESBStatus.OOM_EVENT,eventConfiguration);
        eventConfiguration = new EventConfiguration(ESBStatus.HIGH_CPU_LOAD,"wso2esbfrHIGHCPULOADevent.properties");
        instance.eventConfigurations.put(ESBStatus.HIGH_CPU_LOAD,eventConfiguration);
        eventConfiguration = new EventConfiguration(ESBStatus.HIGH_CPU_LOAD,"wso2esbfrHighRequestCountevent.properties");
        instance.eventConfigurations.put(ESBStatus.HIGH_REQUEST_COUNT,eventConfiguration);
    }

    public String getJmxurl() {
        return jmxurl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ConfigurationBean getConfigurationBean() {
        return configurationBean;
    }

    public Map<ESBStatus, EventConfiguration> getEventConfigurations() {
        return eventConfigurations;
    }
}
