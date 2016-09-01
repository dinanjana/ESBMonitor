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

import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.connector.ConnectorFactory;

/**
 * Created by Dinanjana on 17/07/2016.
 */
public class DumpCreatorFactory {
    private static ThreadDumpCreator threadDumpCreator=null;
    private  static JFRDumper jfrDumper=null;

    public static ThreadDumpCreator getThreadDumpCreator(){
        if(threadDumpCreator == null){
            ConnectorFactory conn = new ConnectorFactory();
            threadDumpCreator=new ThreadDumpCreator(Configuration.getInstance(),conn.getRemoteConnectorInstance());
        }
        return threadDumpCreator;
    }

    public static JFRDumper getJFRDumperInstance(){
        if(jfrDumper==null){
            jfrDumper=new JFRDumper();
        }
        return jfrDumper;
    }
}
