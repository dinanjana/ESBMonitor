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

package org.wso2.esbMonitor.persistance;

import org.apache.log4j.Logger;
import org.wso2.esbMonitor.network.PassThruHTTPBean;
import org.wso2.esbMonitor.network.RequestType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Dinanjana on 14/06/2016.
 */
public class PersistenceService {

    private static Logger logger= Logger.getLogger(PersistenceService.class);
    private static Connection conn;
    private static ArrayList<PassThruHTTPBean> scheduledList
            = new ArrayList<>();

    public synchronized static void addNetworkEvent(PassThruHTTPBean passThruHTTPBean){
        logger.info("Adding event :{}"+ passThruHTTPBean.getActiveThreadCount() +"Size :{}" +scheduledList.size());
        scheduledList.add(passThruHTTPBean);
    }

    public static synchronized void addEventToDB() throws SQLException {
        Statement stmt = conn.createStatement();
        for(PassThruHTTPBean passThruHTTPBean:scheduledList){
            stmt.executeUpdate("INSERT INTO HTTP_LOG VALUES (" + passThruHTTPBean.getActiveThreadCount() + ","
                    + passThruHTTPBean.getAvgSizeRecieved() + "," +
                    passThruHTTPBean.getAvgSizeSent() + "," +
                    passThruHTTPBean.getFaultsRecieving() + "," +
                    passThruHTTPBean.getFaultSending() + "," +
                    passThruHTTPBean.getMessagesRecieved() + "," +
                    passThruHTTPBean.getMessageSent() + "," +
                    passThruHTTPBean.getQueueSize() + ",'" +
                    new Timestamp(passThruHTTPBean.getDate().getTime()).toString() + "'," +
                    RequestType.getId(passThruHTTPBean.getType()) +
                    " ) ");
        }
        scheduledList.clear();
        stmt.close();
    }

    public static synchronized void cleanDBTables() throws SQLException {
        String tableName = "HTTP_LOG";
        Statement stmt = conn.createStatement();
        stmt.execute("TRUNCATE TABLE "+ tableName);
        stmt.close();
    }

    public static void setConn(Connection conn) {
        PersistenceService.conn = conn;
    }
}
