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

package org.wso2.esbMonitor.configuration;

/**
 * Created by Dinanjana on 01/07/2016.
 */
public class ConfigurationBean {


    private long dbTask = 3000;
    private long jvmTask = 3000;
    private long networkTask = 3000;
    private String heapDumpPath;
    private String emailAddress;
    private Double memoryUsage = 0.7;
    private Double cpuUsage = 0.7;
    private int httpRequests = 0;
    private int maxReqestqueueSize = 0;
    private long dbCleanerTask = 24L;
    private String threadDumpPath ="ThreadDumps//";
    private int pingReceivingPort =9090;
    private long pingDelay =3000;

    public long getDbTask() {
        return dbTask;
    }

    public void setDbTask(long dbTask) {
        this.dbTask = dbTask;
    }

    public long getJvmTask() {
        return jvmTask;
    }

    public void setJvmTask(long jvmTask) {
        this.jvmTask = jvmTask;
    }

    public long getNetworkTask() {
        return networkTask;
    }

    public void setNetworkTask(long networkTask) {
        this.networkTask = networkTask;
    }

    public String getHeapDumpPath() {
        return heapDumpPath;
    }

    public void setHeapDumpPath(String heapDumpPath) {
        this.heapDumpPath = heapDumpPath;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public int getHttpRequests() {
        return httpRequests;
    }

    public void setHttpRequests(int httpRequests) {
        this.httpRequests = httpRequests;
    }

    public int getMaxReqestqueueSize() {
        return maxReqestqueueSize;
    }

    public void setMaxReqestqueueSize(int maxReqestqueueSize) {
        this.maxReqestqueueSize = maxReqestqueueSize;
    }

    public long getDbCleanerTask() {
        return dbCleanerTask;
    }

    public void setDbCleanerTask(long dbCleanerTask) {
        this.dbCleanerTask = dbCleanerTask;
    }

    public String getThreadDumpPath() {
        return threadDumpPath;
    }

    public void setThreadDumpPath(String threadDumpPath) {
        this.threadDumpPath = threadDumpPath;
    }

    public int getPingReceivingPort() {
        return pingReceivingPort;
    }

    public void setPingReceivingPort(int pingReceivingPort) {
        this.pingReceivingPort = pingReceivingPort;
    }

    public long getPingDelay() {
        return pingDelay;
    }

    public void setPingDelay(long pingDelay) {
        this.pingDelay = pingDelay;
    }
}
