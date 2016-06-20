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

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.io.IOException;
import java.util.TreeSet;

/**
 *
 */
public class ESBMonitor {
    private static TreeSet<ObjectName> mbeansNames = null;


    public static void main(String[] args) throws IOException {

        new Configuration().initProperties();
        DBConnector.initDBConnection();
        PersistenceService.setConn(DBConnector.getConn());
        RemoteConnector.defaultConnector();
        /**
         * Tasks start here
         *  1)JVM monitor
         *  2)Network traffic monitor
         *  3)Persistence service
         *  4)Clean database tables
         *  5)Ping receiver
         *  6)ESB status monitor
         *  */

        new JVMTaskRunner().start();
        new NetworkMonitor().start();
        new DBTaskRunner().start();
        new DBCleanerTask().start();
        new PingHandler().start();
        new ESBStatusCheckerTask().start();
//        MBeanServerConnection remote = RemoteConnector.getRemote();
//        try {
//            ObjectName bean = new ObjectName("org.apache.synapse:Type=Transport,Name=passthru-http-receiver");
//            MBeanInfo info = remote.getMBeanInfo(bean);
//            MBeanAttributeInfo[] attributes = info.getAttributes();
//            for (MBeanAttributeInfo attr : attributes) {
//                System.out.println(attr.getName() + " " + remote.getAttribute(bean, attr.getName()));
//            }
//            System.out.println("Getting Mbean Names from the MBean server ");
//            mbeansNames = new TreeSet<ObjectName>(remote.queryNames(null, null));
//            for (ObjectName name : mbeansNames) {
//                System.out.println("\tObjectName = " + name.getCanonicalName());
//            }
//        } catch (Exception e) {
//
//        }


    }
}
