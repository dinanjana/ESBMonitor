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

import org.apache.log4j.Logger;
import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.connector.RemoteConnector;
import org.wso2.esbMonitor.utils.FileWriter;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by Dinanjana
 * on 02/05/2016.
 */
public class ThreadDumpCreator {
    final static Logger logger = Logger.getLogger(ThreadDumpCreator.class);
    private MBeanInfo memoryInfo;
    private ObjectName bean = null;
    private boolean threadDumpInProgress =false;
    private String filePath;
    private final String THREAD_DUMP_BEAN_NAME = "java.lang:type=Threading";
    private RemoteConnector remoteConnector;
    private Configuration config;

    /**
     * For testing purposes only
     */

    public ThreadDumpCreator(Configuration config, RemoteConnector remote){
        this.remoteConnector = remote;
        this.config = config;
    }
    public static String generateThreadDump() {
        final StringBuilder dump = new StringBuilder();
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
        for (ThreadInfo threadInfo : threadInfos) {
            dump.append('"');
            dump.append(threadInfo.getThreadName());
            dump.append("\" ");
            final Thread.State state = threadInfo.getThreadState();
            dump.append("\n   java.lang.Thread.State: ");
            dump.append(state);
            final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
            for (final StackTraceElement stackTraceElement : stackTraceElements) {
                dump.append("\n        at ");
                dump.append(stackTraceElement);
            }
            dump.append("\n\n");
        }
        return dump.toString();
    }

    /**
     * Getting tread dump from
     * remote JVM
     * */

    public synchronized void getMbeanInfo() {
        threadDumpInProgress = true;
        StringBuilder dump = new StringBuilder();
        filePath=config.getConfigurationBean().getThreadDumpPath();
        try {
            bean = new ObjectName(THREAD_DUMP_BEAN_NAME);
            memoryInfo = remoteConnector.getRemote().getMBeanInfo(bean);
            remoteConnector.getRemote().getObjectInstance(bean);
            MBeanOperationInfo[] mBeanAttributeInfos = memoryInfo.getOperations();
//            for (MBeanOperationInfo mBeanAttributeInfo : mBeanAttributeInfos) {
//                System.out.println(mBeanAttributeInfo.getName());
//            }
            long[] allThreadIds = (long[]) remoteConnector.getRemote().getAttribute(bean, "AllThreadIds");
            Object[] params = new Object[2];
            int maxDepth = 100;
            params[0] = allThreadIds;
            params[1] = maxDepth;
            String[] opSigs = {allThreadIds.getClass().getName(), int.class.getName()};
            CompositeData[] threadInfos = (CompositeData[]) remoteConnector.getRemote().invoke(bean, "getThreadInfo", params, opSigs);
            for (CompositeData threadInfo : threadInfos) {
                dump.append('"');
                dump.append(threadInfo.get("threadName").toString());
                dump.append("\" ");
                final String state = (String) threadInfo.get("threadState");
                dump.append("\n   java.lang.Thread.State: ");
                dump.append(state);
                final CompositeData[] stackTraceElements = (CompositeData[]) threadInfo.get("stackTrace");
                for (CompositeData stackTraceElement : stackTraceElements) {
                    dump.append("\n        at ");
                    // String declaringClass= (String) stackTraceElement.get("declaringClass");
                    String methodName = (String) stackTraceElement.get("methodName");
                    String fileName = (String) stackTraceElement.get("fileName");
                    int lineNumber = (Integer) stackTraceElement.get("lineNumber");
                    dump.append(/*declaringClass + */"." + methodName +
                            ((lineNumber == -2) ? "(Native Method)" :
                                    (fileName != null && lineNumber >= 0 ?
                                            "(" + fileName + ":" + lineNumber + ")" :
                                            (fileName != null ? "(" + fileName + ")" : "(Unknown Source)"))));
                }
                dump.append("\n\n");
                byte [] data = dump.toString().getBytes(Charset.forName("UTF-8"));
                FileWriter.writeFile(filePath+"ThreadDump"+new Date().toString().replaceAll(":","")+".txt",data);
            }

        } catch (MalformedObjectNameException e) {
            logger.error(e.getMessage());
        } catch (InstanceNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IntrospectionException e) {
            logger.error(e.getMessage());
        } catch (ReflectionException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage()+" "+e.getStackTrace());
        } catch (MBeanException e) {
            logger.error(e.getMessage());
        } catch (AttributeNotFoundException e) {
            logger.error(e.getMessage());
        }
        finally {
            threadDumpInProgress = false;
        }
    }

    public boolean isThreadDumpInProgress() {
        return threadDumpInProgress;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
