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

package org.wso2.esbMonitor.tasks;

/**
 * Created by Dinanjana on 04/07/2016.
 */
public class TaskFactory {
    private static DBCleanerTask dbCleanerTask;
    private static DBTaskRunner dbTaskRunner;
    private static ESBStatusCheckerTask esbStatusCheckerTask;
    private static JVMTaskRunner jvmTaskRunner;
    private static NetworkMonitor networkMonitor;

    public DBCleanerTask getDbCleanerTaskInstance(){
        if(dbCleanerTask ==null){
            dbCleanerTask=new DBCleanerTask();
        }
        return dbCleanerTask;
    }

    public ESBStatusCheckerTask getEsbStatusCheckerTaskInstance(){
        if(esbStatusCheckerTask== null){
            esbStatusCheckerTask=new ESBStatusCheckerTask();
        }
        return esbStatusCheckerTask;
    }

    public JVMTaskRunner getJvmTaskRunnerInstance(){
        if(jvmTaskRunner == null){
            jvmTaskRunner=new JVMTaskRunner();
        }
        return jvmTaskRunner;
    }

    public NetworkMonitor getNetworkMonitorInstance(){
        if(networkMonitor==null){
            networkMonitor=new NetworkMonitor();
        }
        return networkMonitor;
    }

    public DBTaskRunner getDbTaskRunnerInstance(){
        if(dbTaskRunner == null){
            dbTaskRunner=new DBTaskRunner();
        }
        return dbTaskRunner;
    }
}
