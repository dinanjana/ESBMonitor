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
import org.wso2.esbMonitor.pingReceiver.PingHandler;

/**
 * Created by Dinanjana on 20/06/2016.
 */
public class ESBStatusCheckerTask extends Thread{

    Logger logger = Logger.getLogger(ESBStatusCheckerTask.class);
    private static long WAIT_TIME;
    private static boolean status=true;

    public void run(){
        while (true){
            long currentTime = System.currentTimeMillis();
            long lastUpdatedTime = PingHandler.getLastUpdatedTime();
            if(currentTime-lastUpdatedTime > WAIT_TIME){
                logger.info("ESB is unresponsive..");
                /**TODO
                 * Notify
                 */
                status=false;
            }else {
                logger.info("ESB is responsive..");
                status=true;
            }
            try {
                Thread.sleep(WAIT_TIME+1000L);
            } catch (InterruptedException e) {
                logger.error("ERROR",e);
            }
        }
    }

    public static void setWaitTime(long waitTime) {
        WAIT_TIME = waitTime;
    }

    public static boolean isStatus() {
        return status;
    }
}
