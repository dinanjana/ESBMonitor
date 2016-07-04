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
import org.wso2.esbMonitor.connector.ConnectorFactory;
import org.wso2.esbMonitor.persistance.PersistenceService;
import org.wso2.esbMonitor.persistance.PersistenceServiceFactory;
import org.wso2.esbMonitor.pingReceiver.PingHandler;
import org.wso2.esbMonitor.tasks.*;

import java.io.IOException;

/**
 *
 */
public class ESBMonitor {

      public static void main(String[] args) throws IOException {

          Configuration config = Configuration.getInstance();
          //initializing factory classes
          ConnectorFactory connectorFactory = new ConnectorFactory();
          TaskFactory taskFactory = new TaskFactory();
          PersistenceServiceFactory persistenceServiceFactory = new PersistenceServiceFactory();

          connectorFactory.createRemoteConnector(config.getJmxurl(),
                  config.getPassword(), config.getUsername());
          RemoteConnector remoteConnector = connectorFactory.getRemoteConnectorInstance();
          remoteConnector.createConnection();
          DBConnector dbConnector =connectorFactory.getDbConnectorFactory();
          dbConnector.initDBConnection();
          persistenceServiceFactory.getPersistenceServiceInstance().
                setConn(dbConnector.getConn());


        /**
         * Tasks start here
         *  1)JVM monitor
         *  2)Network traffic monitor
         *  3)Persistence service
         *  4)Clean database tables
         *  5)Ping receiver
         *  6)ESB status monitor
         *  */

        JVMTaskRunner jvmTaskRunner = taskFactory.getJvmTaskRunnerInstance();
        NetworkMonitor networkMonitor = taskFactory.getNetworkMonitorInstance();
        DBTaskRunner dbTaskRunner = taskFactory.getDbTaskRunnerInstance();
        DBCleanerTask dbCleanerTask= taskFactory.getDbCleanerTaskInstance();
        ESBStatusCheckerTask esbStatusCheckerTask = taskFactory.getEsbStatusCheckerTaskInstance();
        jvmTaskRunner.start();
        networkMonitor.start();
        dbTaskRunner.start();
        dbCleanerTask.start();
        esbStatusCheckerTask.start();
        new PingHandler().start();

    }
}
