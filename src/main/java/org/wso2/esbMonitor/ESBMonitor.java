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

package org.wso2.esbMonitor;

import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.connector.DBConnector;
import org.wso2.esbMonitor.connector.RemoteConnector;
import org.wso2.esbMonitor.persistance.PersistenceService;
import org.wso2.esbMonitor.pingReceiver.PingHandler;
import org.wso2.esbMonitor.tasks.*;

import java.io.IOException;

/**
 *
 */
public class ESBMonitor {

      public static void main(String[] args) throws IOException {

        Configuration config = new Configuration();
        config.initProperties();
        DBConnector.initDBConnection();
        PersistenceService.setConn(DBConnector.getConn());
        RemoteConnector remoteConnector = new RemoteConnector(config.getJmxurl(),
                config.getPassword(),config.getUsername());
        remoteConnector.createConnection();
        /**
         * Tasks start here
         *  1)JVM monitor
         *  2)Network traffic monitor
         *  3)Persistence service
         *  4)Clean database tables
         *  5)Ping receiver
         *  6)ESB status monitor
         *  */

        JVMTaskRunner jvmTaskRunner = new JVMTaskRunner(config,remoteConnector);
        jvmTaskRunner.start();
        NetworkMonitor networkMonitor = new NetworkMonitor(config,remoteConnector);
        networkMonitor.start();
        DBTaskRunner dbTaskRunner =new DBTaskRunner(config);
        dbTaskRunner.start();
        DBCleanerTask dbCleanerTask=new DBCleanerTask(config);
        dbCleanerTask.start();
        new PingHandler().start();
        ESBStatusCheckerTask esbStatusCheckerTask = new ESBStatusCheckerTask(config);
        esbStatusCheckerTask.start();

    }
}
