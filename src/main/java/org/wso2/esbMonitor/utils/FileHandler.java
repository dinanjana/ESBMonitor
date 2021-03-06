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

package org.wso2.esbMonitor.utils;

import java.io.InputStreamReader;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by Dinanjana on 01/06/2016.
 */
public class FileHandler {
    private static Logger logger = Logger.getLogger(FileHandler.class);

    /**
     * Creates a new directory in the classpath
     * @param  newDir name of the new folder
     * */
    public static void createDir(String newDir,boolean oneDir){
        if(oneDir){
            new File("./"+newDir).mkdir();
        }else {
            new File("./"+newDir).mkdirs();
        }

    }

    /**
     * Creates new files.If the file exists append to the end
     * @param fileName file name
     * @param data Data to be written to the file as byte a*/

    public static synchronized void writeFile(String fileName,byte [] data){

        Path file = Paths.get(fileName);
        try {
            if(Files.exists(file)){
                Files.write(file, data, StandardOpenOption.WRITE);
            }else {
                Files.write(file, data);
            }
        }catch (NoSuchFileException e){
            try {
                Files.write(file,data);
            } catch (IOException e1) {
                logger.error("Error :" ,e);
            }
        }
        catch (IOException e) {
            logger.error("File writing error",e);
        }
    }

    public static String readFile(String fileName){
        Charset charset = Charset.forName("UTF-8");
        //Path file = Paths.get(fileName);
        InputStreamReader isr = new InputStreamReader(FileHandler.class.getClassLoader().getResourceAsStream(fileName));
        String line = null;
        try (BufferedReader reader = new BufferedReader(isr)) {
            String line1=null;
            while ((line1 = reader.readLine()) != null) {
                line=line+line1;
            }
            logger.info("File read complete");
        } catch (IOException x) {
            logger.error("IOException: %s%n", x);
        }
        return line;
    }

    /**
     * Deletes directory with files
     * @param file directory which needs to be deleted
     * */
    public static void delete(File file)
            throws IOException{
        if(file.isDirectory()){
            //directory is empty, then delete it
            if(file.list().length==0){
                file.delete();
                logger.debug("Directory is deleted : "
                             + file.getAbsolutePath());
            }else{
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    delete(fileDelete);
                }
                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    logger.debug("Directory is deleted : "
                                 + file.getAbsolutePath());
                }
            }
        }else{
            //if file, then delete it
            file.delete();
            logger.debug("File is deleted : " + file.getAbsolutePath());
        }
    }
}
