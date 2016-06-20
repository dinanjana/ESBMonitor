# ESB Flight Recorder

##Introduction

ESB Flight Recorder is a tool that can be used to monitor an instance of ESB.Users can set variable 
values by changing parameters in wso2esbfr.properties file. 
ESBMonitor mainly compromises of 5 tasks
  
  1)JVM monitoring task
  
  2)Network monitoring task
  
  3)Database persisting task
  
  4)Cleaning task
  
  5)ESB status checking task
  
##Building from source

Use **mvn clean compile assembly:single** to build the esbMonitor from source

##Run the project

Use **java -jar esbMonitor-1.0-SNAPSHOT-jar-with-dependencies.jar** command to run esbMonitor executable
jar

##Setting property values
Many options can be customized through changing values in **wso2esbfr.properties** file.
- DB_TASK_INTERVAL : Value of this property is used to set period value where ESB events are persisted to
database. Value is taken in miliseconds. E.g : **DB_TASK_INTERVAL=3000** ESB events are persisted in every 3 sseconds
- JVM_TASK_INTERVAL : Value of this property is used to set period value where remote JVM is queried for resource usages. Value is taken in miliseconds. E.g **JVM_TASK_INTERVAL=3000** Remote jvm is queried in every 3 seconds
- NETWORK_TASK_INTERVAL : Value of this property is used to set period value where remote JVM is queried for network request load. Value is taken in miliseconds. E.g. **NETWORK_TASK_INTERVAL=3000** Remote jvm is queried in every 3 seconds
- HEAP_DUMP_PATH : This sets the path of where heap dumps must be saved in remote machine which hosts the JVM which holds WSO2 ESB.
E.g : **HEAP_DUMP_PATH=/heap**. Heap dumps will be saved on ESB_HOME/bin/heap folder in the remote machine
- EMAIL_ADDRESS= : Email address of the persons which needs to contact for emergencies
- MAX_MEMORY_USAGE : Memory threshold value. Value = memory usage / max heap size.If this value exceeds heap dumps will be generated. E.g **MAX_MEMORY_USAGE=0.1** If memory usage is 10% of allocated heap memory esbMonitor will take necessary steps to monitor issue
- MAX_CPU_USAGE : Cpu usage threshold value. When this value exceeds thread dumps of the remote JVM will be generated. E.g. **MAX_CPU_USAGE=0.01** If cpu usage is 1% thread dumps will be generated.
- MAX_REQUEST_QUEUE_SIZE : Maximum request queue size at a given moment.
- MAX_HTTP_REQUESTS : Maximum number of http or https requests at a given moment
- DB_CLEANER_TASK : Value of this property is used to set period value where DB tables are truncated. Value is taken in hours.
E.g. **DB_CLEANER_TASK=24**. Tables are truncated in every 24 hours
  
  
