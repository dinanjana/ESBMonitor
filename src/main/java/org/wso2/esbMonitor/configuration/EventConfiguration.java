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
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Dinanjana on 01/07/2016.
 */
public class EventConfiguration {

    private Logger logger= Logger.getLogger(EventConfiguration.class);
    private String fileName;
    private ESBStatus eventName;
    private int maxThreadDumps=4;
    private int maxHeapDumps=4;
    private long eventPeriod=3000L;
    private Properties properties;

    public EventConfiguration(ESBStatus eventName,String eventConfigFileName){
        this.eventName=eventName;
        this.fileName=eventConfigFileName;
        initProperties();

    }
    private void initProperties(){
        readPropFile(fileName);
        addProperties(properties);
    }

    private void readPropFile(String fileName){
        try {
            properties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                return;
            }

        }catch (Exception e){
            logger.error("Property file error",e);
        }
    }

    private void addProperties(Properties prop){
        if(prop.getProperty("MAX_HEAP_DUMPS") != null){
            maxHeapDumps = Integer.parseInt(prop.getProperty("MAX_HEAP_DUMPS"));
            logger.info("Added max heap dumps ="+ getMaxHeapDumps()+" for "+eventName);
        }
        if(prop.getProperty("MAX_THREAD_DUMPS") != null){
            maxThreadDumps = Integer.parseInt(prop.getProperty("MAX_THREAD_DUMPS"));
            logger.info("Added max Thread dumps ="+ getMaxThreadDumps()+" for "+eventName);
        }
        if(prop.getProperty("EVENT_PERIOD") != null){
            maxThreadDumps = Integer.parseInt(prop.getProperty("EVENT_PERIOD"));
            logger.info("Added max event period  ="+ getMaxThreadDumps()+" for "+eventName);
        }

    }

    public int getMaxThreadDumps() {
        return maxThreadDumps;
    }

    public int getMaxHeapDumps() {
        return maxHeapDumps;
    }

    public long getEventPeriod() {
        return eventPeriod;
    }

    public Properties getOtherProperties(){return this.properties; }
}
