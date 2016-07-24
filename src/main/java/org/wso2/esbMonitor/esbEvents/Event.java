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

package org.wso2.esbMonitor.esbEvents;

import java.util.Observable;

/**
 * Created by Dinanjana on 17/07/2016.
 */
public abstract class Event extends Observable{

    private long eventEndtime;
    /**This method is called when a
     *new event detected
     * */
    public abstract void initEvent();
    /**This method is called
     * during the period of event
     * */
    public abstract void triggerEvent();
    /**This method is called when a event is
     * finished
     * */
    public abstract void resetEvent();

    public abstract String getValue();

    public boolean isEventPeriodElapsed(){
        return eventEndtime <= System.currentTimeMillis();
    }

    public void setEventEndTime(long eventEndtime){
        this.eventEndtime=eventEndtime;
    }

    public long getEventEndTime(){
        return this.eventEndtime;
    }
}
