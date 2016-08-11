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

    private Logger logger= Logger.getLogger(PersistenceService.class);
    private Connection conn;
    private ArrayList<PassThruHTTPBean> scheduledList
            = new ArrayList<>();

    public synchronized void addNetworkEvent(PassThruHTTPBean passThruHTTPBean){
        logger.info("Adding event :{}"+ passThruHTTPBean.getActiveThreadCount() +"Size :{}" +scheduledList.size());
        scheduledList.add(passThruHTTPBean);
    }
    public void createTables(){
        Statement stmt=null;
        try{
            stmt= conn.createStatement();
            stmt.execute("CREATE TABLE HTTP_LOG(\n" +
                         "  activeThreadCount INTEGER,\n" +
                         "  avgSizeRecieved DECIMAL(3,2),\n" +
                         "  avgSizeSent DECIMAL(3,2),\n" +
                         "  faultsRecieving INTEGER,\n" +
                         "  faultsSending INTEGER,\n" +
                         "  messagesRecieved INTEGER,\n" +
                         "  messageSent INTEGER,\n" +
                         "  queueSize INTEGER,\n" +
                         "  time TIMESTAMP,\n" +
                         "  requestType INTEGER\n" +
                         ")");
        }catch (SQLException e){
            if(e.getSQLState().equals("X0Y32")){
                logger.info("Table already created");
            }else {
                logger.error("Error :",e);
            }
        }
        catch (Exception e){
            logger.error("Table already exist",e);
        }
        finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.error("Error :" , e);
            }
        }
    }

    public synchronized void addEventToDB() throws SQLException {
        Statement stmt = conn.createStatement();
        try {
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
        }catch (Exception e){
            logger.error("Error",e);
        }finally {
            scheduledList.clear();
            stmt.close();
        }
    }

    public synchronized void cleanDBTables() throws SQLException {
        String tableName = "HTTP_LOG";
        Statement stmt = conn.createStatement();
        try{
            stmt.execute("TRUNCATE TABLE "+ tableName);
        }catch (Exception e){
            logger.error("Error",e);
        }finally {
            stmt.close();
        }
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
}
