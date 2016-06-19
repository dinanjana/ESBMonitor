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
import org.wso2.esbMonitor.persistance.PersistenceService;

import java.sql.SQLException;

/**
 * Created by Dinanjana
 * on 30/05/2016.
 */
public class DBTaskRunner extends Thread {
    private static long WAIT_TIME;
    private static Logger logger = Logger.getLogger(DBTaskRunner.class);

    public void run(){
        while (true){
            try {
                PersistenceService.addEventToDB();
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                logger.error("Wait error" , e);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setWaitTime(long waitTime) {
        WAIT_TIME = waitTime;
    }
}
