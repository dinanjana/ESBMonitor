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

import org.wso2.esbMonitor.esbEvents.ESBEvent;

/**
 * Created by Dinanjana on 01/07/2016.
 */
public class EventConfiguration {

    private String fileName;
    private ESBEvent eventName;
    private int maxThreadDumps=4;
    private int maxHeapDumps=4;
    private long eventPeriod=3000L;

    public EventConfiguration(ESBEvent eventName,String eventConfigFileName){
        this.eventName=eventName;
        this.fileName=eventConfigFileName;
    }
    public void initProperties(){}

    public void setEventName(ESBEvent eventName) {
        this.eventName = eventName;
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
}
