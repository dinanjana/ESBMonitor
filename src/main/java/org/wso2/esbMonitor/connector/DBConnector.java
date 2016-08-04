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

import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Created by Dinanjana
 * on 22/05/2016.
 * This connector is
 * used to connect to
 * derby embedded db
 */
public class DBConnector {

    private final String protocol = "jdbc:derby:";
    private String dbName = "./wso2FlightRecorder";
    private Connection conn ;
    private Logger logger = Logger.getLogger(DBConnector.class);

    protected DBConnector(){

    }

    public void initDBConnection() {
        try {
            conn = DriverManager.getConnection(protocol + dbName
                    + ";create=true");
            logger.info("Connected to and created database " + dbName);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("ERROR : ", e);
            logger.info("Retrying in 3 seconds......");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e1) {
                logger.error("ERROR:" , e1);
            }
            initDBConnection();
        }
    }

    public void closeDBConnection()  {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            if(e.getErrorCode() == 50000){
                logger.info("Closed sucessfully");
            }
            //e.printStackTrace();
        }finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public Connection getConn() {
        return conn;
    }

}
