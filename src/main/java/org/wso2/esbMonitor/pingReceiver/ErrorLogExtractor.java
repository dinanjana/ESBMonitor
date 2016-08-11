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

import org.apache.log4j.Logger;
import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.configuration.EventConfiguration;
import org.wso2.esbMonitor.esbEvents.ESBStatus;
import org.wso2.esbMonitor.utils.FileHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Dinanjana on 01/08/2016.
 */
public class ErrorLogExtractor extends PingHandler {
    private Logger logger = Logger.getLogger(ErrorLogExtractor.class);
    private ErrorLogExtractor instance;
    private String errorFileName;

    public ErrorLogExtractor(){
        this.instance=new ErrorLogExtractor();
        EventConfiguration event= Configuration.getInstance().getEventConfigurations().get(ESBStatus.UNRESPONSIVE_ESB);
        instance.ip=event.getOtherProperties().getProperty("IP");
        instance.port=event.getOtherProperties().getProperty("PORT");
        instance.errorFileName=Configuration.getInstance().getUsername();
    }

    public String sendRequest(){
        String url = "http://"+ip+":"+port+"/esbFR/pingReq";
        URL obj;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            logger.info("\nSending ping request request to URL : " + url);
            logger.info("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (MalformedURLException e) {
            logger.error("Error :" , e);
        } catch (ProtocolException e) {
           logger.error("Error :" ,e);
        } catch (IOException e) {
           logger.error("Error :" ,e);
        }
        return null;
    }

    public void writeErrorLogToFile(String fileName,String data){
        FileHandler.writeFile(fileName, data.getBytes(Charset.forName("UTF-8")));
    }

}
