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

package org.wso2.esbMonitor.connector;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Dinanjana
 * on 30/04/2016.
 */
public class RemoteConnector {

    private MBeanServerConnection remote = null;
    private JMXConnector connector = null;
    private final Logger logger= LogManager.getLogger(RemoteConnector.class);
    private String jmxurl;
    private String username;
    private String password;

    protected RemoteConnector(String jmxurl,String password,String username){
        this.jmxurl=jmxurl;
        this.password=password;
        this.username=username;
    }

    public void createConnection() {
        try {
            connect();
        } catch (MalformedURLException e) {
            logger.error(e.getStackTrace());
        } catch (IOException e) {
            logger.error("IO error in connecting",e);
            try {
                logger.info("Trying to reconnect in 3 seconds");
                Thread.sleep(3000);
                createConnection();
            } catch (InterruptedException e1) {
                logger.error("Thread interrupted",e);
            }

        }
    }

    private void connect() throws IOException {
        JMXServiceURL target = new JMXServiceURL(jmxurl);
        //for passing credentials for password
        Map<String, String[]> env = new HashMap<String, String[]>();
        String[] credentials = {username, password};
        env.put(JMXConnector.CREDENTIALS, credentials);
        connector = JMXConnectorFactory.connect(target, env);
        remote = connector.getMBeanServerConnection();
        logger.info("MbeanServer connection obtained");
    }

    public synchronized MBeanServerConnection getRemote() {
        return remote;
    }

    public synchronized Object getMbeanAttribute(String objectName,String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, IOException {
        try {
            ObjectName bean = new ObjectName(objectName);
            return remote.getAttribute(bean,attribute);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void closeConnection() throws IOException {
        if (connector != null) {
            connector.close();
        }
    }

}
