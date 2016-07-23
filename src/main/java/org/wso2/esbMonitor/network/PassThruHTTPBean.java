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

package org.wso2.esbMonitor.network;


import java.util.Date;

/**
 * Created by Dinanjana on 29/05/2016.
 * This serves as the DAO object for network
 * traffic details
 */

public class PassThruHTTPBean {

    private int id;
    private int activeThreadCount;
    private double avgSizeRecieved;
    private double avgSizeSent;
    private long faultsRecieving;
    private long faultSending;
    private long messagesRecieved;
    private long messageSent;
    private int queueSize;
    private Date date;
    private RequestType type;

    public double getAvgSizeRecieved() {
        return avgSizeRecieved;
    }

    public void setAvgSizeRecieved(double avgSizeRecieved) {
        this.avgSizeRecieved = avgSizeRecieved;
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setDate(Date date) {
        this.date = new Date(date.getTime());
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public int getActiveThreadCount() {
        return activeThreadCount;
    }

    public void setActiveThreadCount(int activeThreadCount) {
        this.activeThreadCount = activeThreadCount;
    }

    public double getAvgSizeSent() {
        return avgSizeSent;
    }

    public void setAvgSizeSent(double avgSizeSent) {
        this.avgSizeSent = avgSizeSent;
    }

    public long getFaultsRecieving() {
        return faultsRecieving;
    }

    public void setFaultsRecieving(long faultsRecieving) {
        this.faultsRecieving = faultsRecieving;
    }

    public long getMessagesRecieved() {
        return messagesRecieved;
    }

    public void setMessagesRecieved(long messagesREcieved) {
        this.messagesRecieved = messagesREcieved;
    }

    public long getFaultSending() {
        return faultSending;
    }

    public void setFaultSending(long faultSending) {
        this.faultSending = faultSending;
    }

    public long getMessageSent() {
        return messageSent;
    }

    public void setMessageSent(long messageSent) {
        this.messageSent = messageSent;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }
}
