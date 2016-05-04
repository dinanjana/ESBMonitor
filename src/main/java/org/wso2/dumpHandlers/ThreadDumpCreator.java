package org.wso2.dumpHandlers;

import org.apache.log4j.Logger;
import org.wso2.connector.RemoteConnector;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * Created by Dinanjana
 * on 02/05/2016.
 */
public class ThreadDumpCreator {
    final static Logger logger = Logger.getLogger(ThreadDumpCreator.class);
    private static MBeanInfo memoryInfo;
    private static ObjectName bean = null;

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

    public static void getMbeanInfo()  {
        StringBuilder dump = new StringBuilder();
        try {
            bean = new ObjectName("java.lang:type=Threading");
            memoryInfo = RemoteConnector.getRemote().getMBeanInfo(bean);
            RemoteConnector.getRemote().getObjectInstance(bean);
            MBeanOperationInfo [] mBeanAttributeInfos = memoryInfo.getOperations();
            for(MBeanOperationInfo mBeanAttributeInfo : mBeanAttributeInfos){
                System.out.println(mBeanAttributeInfo.getName());
            }
            long [] allThreadIds = (long [])RemoteConnector.getRemote().getAttribute(bean,"AllThreadIds");
            Object [] params = new Object[2];
            int maxDepth = 100;
            params[0] = allThreadIds;
            params[1] = maxDepth;
            String [] opSigs = {allThreadIds.getClass().getName(),int.class.getName()};
            CompositeData [] threadInfos = (CompositeData[]) RemoteConnector.getRemote().invoke(bean,"getThreadInfo",params,opSigs);
//            ThreadInfo[] threadInfos ;
            for (CompositeData threadInfo : threadInfos) {
                dump.append('"');
                dump.append(threadInfo.get("threadName").toString());
                dump.append("\" ");
                final String state = (String) threadInfo.get("threadState");
                dump.append("\n   java.lang.Thread.State: ");
                dump.append(state);
                final StackTraceElement[] stackTraceElements = (StackTraceElement[]) threadInfo.get("stackTrace");
                for (final StackTraceElement stackTraceElement : stackTraceElements) {
                    dump.append("\n        at ");
                    dump.append(stackTraceElement);
                }
                dump.append("\n\n");
            }
            //ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);

        } catch (MalformedObjectNameException e) {
            logger.error("MemoryExtractor.java:25 " + e.getMessage());
        } catch (InstanceNotFoundException e) {
            logger.error("MemoryExtractor.java:27 " + e.getMessage());
        } catch (IntrospectionException e) {
            logger.error("MemoryExtractor.java:29 " + e.getMessage());
        } catch (ReflectionException e) {
            logger.error("MemoryExtractor.java:31 " + e.getMessage());
        } catch (IOException e) {
            logger.error("MemoryExtractor.java:33 " + e.getMessage());
        } catch (MBeanException e) {
            e.printStackTrace();
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        }
    }

}
