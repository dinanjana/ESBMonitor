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

import org.apache.log4j.Logger;
import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.reporting.ReportTemplate;
import org.wso2.esbMonitor.connector.DBConnector;
import org.wso2.esbMonitor.connector.RemoteConnector;
import org.wso2.esbMonitor.connector.ConnectorFactory;
import org.wso2.esbMonitor.esbEvents.events.EventFactory;
import org.wso2.esbMonitor.esbEvents.events.HighCPULoadEvent;
import org.wso2.esbMonitor.esbEvents.events.HighRequestCountEvent;
import org.wso2.esbMonitor.esbEvents.events.OOMEvent;
import org.wso2.esbMonitor.esbEvents.events.UnresponsiveESBEvent;
import org.wso2.esbMonitor.persistance.PersistenceServiceFactory;
import org.wso2.esbMonitor.reporting.ReportCreator;
import org.wso2.esbMonitor.tasks.*;
import org.wso2.esbMonitor.utils.FileHandler;

import java.io.IOException;

/**
 *
 */
public class ESBMonitor {
      private static Logger logger = Logger.getLogger(ESBMonitor.class);
      public static void main(String[] args) throws IOException {
          logger.info("Starting WSO2 ESB Flight Recorder");
          FileHandler.createDir("reports",true);
          Configuration config = Configuration.getInstance();
          ReportTemplate.getReportTemplateInstance();
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
          persistenceServiceFactory.getPersistenceServiceInstance().createTables();

          /**
       * Registering events for notifications
       * */
          //List of observers
          ReportCreator reportCreator = ReportCreator.getInstance();
          //List of observable event
          OOMEvent oomEvent = EventFactory.getOomEventInstance();
          HighCPULoadEvent highCPULoadEvent=EventFactory.getHighCPULoadEventInstance();
          HighRequestCountEvent[] highRequestCountEvents=EventFactory.getHighRequestCountEvents();
          UnresponsiveESBEvent unresponsiveESBEvent=EventFactory.getUnresponsiveEsbEventInstance();

          reportCreator.setOomEvent(oomEvent);
          reportCreator.setHighCPULoadEvent(highCPULoadEvent);
          reportCreator.setHighRequestCountEvent(highRequestCountEvents);
          reportCreator.setUnresponsiveESBEvent(unresponsiveESBEvent);

          oomEvent.addObserver(reportCreator);
          highCPULoadEvent.addObserver(reportCreator);
          unresponsiveESBEvent.addObserver(reportCreator);
          for(HighRequestCountEvent highRequestCountEvent:highRequestCountEvents){
              highRequestCountEvent.addObserver(reportCreator);
          }


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
          //new PingHandler().start();

    }
}
