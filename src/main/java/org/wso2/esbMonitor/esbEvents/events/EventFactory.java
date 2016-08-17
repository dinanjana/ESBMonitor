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

package org.wso2.esbMonitor.esbEvents.events;

/**
 * Created by Dinanjana on 17/07/2016.
 */
public class EventFactory {
    private static OOMEvent oomEvent=null;
    private static HighCPULoadEvent highCPULoadEvent=null;
    private static HighRequestCountEvent [] highRequestCountEvents=new HighRequestCountEvent[4];
    private static UnresponsiveESBEvent unresponsiveESBEvent=null;

    /**
     * Returns OOM event instance
     * */
    public static OOMEvent getOomEventInstance() {
        if(oomEvent == null){
            oomEvent=new OOMEvent();
        }
        return oomEvent;
    }
    /**
     * Returns High cpu load event event instance
     * */
    public static HighCPULoadEvent getHighCPULoadEventInstance(){
        if(highCPULoadEvent==null){
            highCPULoadEvent=new HighCPULoadEvent();
        }
        return highCPULoadEvent;
    }

    public static HighRequestCountEvent getHighRequestCountEventInstance(){
//        if(highRequestCountEvent==null){
//            highRequestCountEvent=new HighRequestCountEvent();
//        }
        for (int i = 0 ; i <highRequestCountEvents.length;i++){
            if(highRequestCountEvents[i]==null){
                highRequestCountEvents[i]=new HighRequestCountEvent();
                return highRequestCountEvents[i];
            }
        }
        return null;
    }

    /**
     * Returns Unresponsive ESB event instance
     * */
    public static UnresponsiveESBEvent getUnresponsiveEsbEventInstance(){
        if(unresponsiveESBEvent==null){
            unresponsiveESBEvent=new UnresponsiveESBEvent();
        }
        return unresponsiveESBEvent;
    }

    /**
     * Returns High request count events instance
     * */
    public static HighRequestCountEvent[] getHighRequestCountEvents() {
        for (int i = 0 ; i <highRequestCountEvents.length;i++){
            if(highRequestCountEvents[i] == null){
                highRequestCountEvents[i]=new HighRequestCountEvent();
            }
        }
        return highRequestCountEvents;
    }
}
