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

package org.wso2.esbMonitor.reporting;

import org.wso2.esbMonitor.utils.FileHandler;

/**
 * Created by Dinanjana on 12/08/2016.
 * This class will read the template.html
 * file and keep it on reportTemplate
 * string for later use
 */
public class ReportTemplate {
    private String reportTemplate;
    private static ReportTemplate instance=null;

    public static ReportTemplate getReportTemplateInstance(){
        if(instance==null){
            instance=new ReportTemplate();
        }
        return instance;
    }

    private ReportTemplate(){
        //this.reportTemplate = getClass().getClassLoader().getResource("reportTemplate/template.html");
        this.reportTemplate= FileHandler.readFile("reportTemplate/template.html");
    }

    public String getReportTemplate(){
        return this.reportTemplate;
    }

}
