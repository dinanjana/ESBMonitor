package org.wso2.esbMonitor.reporting.beans;

import org.wso2.esbMonitor.configuration.Configuration;
import org.wso2.esbMonitor.esbEvents.ESBStatus;

import java.util.Date;

/**
 * Created by Dinanjana on 14/05/2016.
 */
public class ESBEventReport {
    private ESBStatus esbEvent;
    private String reason;
    private String dumplocation;
    private Date date;

    public ESBEventReport(String reason, String dumplocation){
        this.reason = reason;
        this.dumplocation = dumplocation;
        this.date = new Date();
    }

    public String getReason() {
        return reason;
    }

    public String getDumplocation() {
        return dumplocation;
    }

    public Date getDate() {
        return date;
    }

    public String getReport(){
        return "\n\n\n\n\n"
                +esbEvent.toString() +
                "\n\n\n\n\n"+
                esbEvent.toString() +"detected on" + date.toString() +"\n\n\n" +
                "relevant dumps can be found on"
                +"\n\n\n\n" +
                "Thread dumps :" + Configuration.getInstance().getConfigurationBean().getThreadDumpPath()+
                "Heap dumps : " +Configuration.getInstance().getConfigurationBean().getHeapDumpPath();
    }
}
