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

package org.wso2.esbMonitor.pingReceiver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Created by Dinanjana on 19/06/2016.
 */
public class PingHandler extends Thread{
    private static Logger logger = Logger.getLogger(PingHandler.class);
    private static int PORT = 9090;
    private static long lastUpdatedTime=0;

    private static void initPingReceiver() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/ping", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        logger.info("Ping server started");
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            lastUpdatedTime = System.currentTimeMillis();
            String response = "Ping received";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }

    public void run(){
        try {
            initPingReceiver();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setPORT(int PORT) {
        PingHandler.PORT = PORT;
    }

    public static long getLastUpdatedTime(){
        return lastUpdatedTime;
    }

}
