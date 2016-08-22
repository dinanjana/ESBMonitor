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

package org.wso2.esbMonitor.dumpHandlers;

import com.sun.management.DiagnosticCommandMBean;
import org.apache.log4j.Logger;
import org.wso2.esbMonitor.connector.ConnectorFactory;

import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.io.IOException;

/**
 * Created by Dinanjana on 22/08/2016.
 */
public class JFRDumper {
    private Logger logger= Logger.getLogger(JFRDumper.class);
    /** Object Name of DiagnosticCommandMBean. */
    public final static String DIAGNOSTIC_COMMAND_MBEAN_NAME =
            "com.sun.management:type=DiagnosticCommand";
    /** Platform MBean Server. */
    private ObjectName objectName;
    private DiagnosticCommandMBean diagnosticCommandMBean;

    public JFRDumper(){
        try {
            this.objectName=new ObjectName(DIAGNOSTIC_COMMAND_MBEAN_NAME);
        } catch (MalformedObjectNameException e) {
            logger.error("JFR is not available :" ,e);
        }
    }

    public void createJFR(long time,String path){
        String commandLineArgument = String.format("delay=0s compress=true duration=%ds filename=\"%s\"", time,path);
        try {
            logger.info("Creating JFR");
            String res = (String) new ConnectorFactory().getRemoteConnectorInstance().getRemote().invoke(this.objectName,"jfrStart", new Object[]{new String[]{commandLineArgument}}, new String[]{String[].class.getName()});
            logger.info("JFR Message: " + res );
        } catch (MBeanException e) {
            logger.error("Error:" ,e);
        } catch (ReflectionException e) {
            logger.error("Error" ,e);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
