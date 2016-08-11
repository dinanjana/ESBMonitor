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

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Dinanjana on 11/08/2016.
 */
public class ZipArchiveCreator {
    private Logger logger = Logger.getLogger(ZipArchiveCreator.class);
    List<String> fileList;
    private String outputZipFile = "E:\\Project\\esbMonitor\\EsbMonitor\\ThreadDumps.zip";
    private String sourceFolder = "E:\\Project\\esbMonitor\\EsbMonitor\\ThreadDumps";

    public ZipArchiveCreator(String outputZipFile,String sourceFolder){
        fileList = new ArrayList<String>();
        this.outputZipFile=outputZipFile;
        this.sourceFolder=sourceFolder;
    }

    /**
     * Zip it
     */
    public void zipIt(){
        byte[] buffer = new byte[1024];
        try{
            FileOutputStream fos = new FileOutputStream(outputZipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            System.out.println("Output to Zip : " + outputZipFile);
            for(String file : this.fileList){
                try {
                    logger.info("File Added : " + file);
                    ZipEntry ze= new ZipEntry(file);
                    zos.putNextEntry(ze);
                    FileInputStream in =
                            new FileInputStream(/*sourceFolder + File.separator +*/ file);
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    in.close();
                }catch (FileNotFoundException e){
                    logger.error("Error:" , e);
                }
            }
            zos.closeEntry();
            zos.close();
            logger.info("Done");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList
     * @param node file or directory
     */
    public void generateFileList(File node){
        //add file only
        if(node.isFile()){
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }
        if(node.isDirectory()){
            String[] subNote = node.list();
            for(String filename : subNote){
                generateFileList(new File(node, filename));
            }
        }

    }

    /**
     * Format the file path for zip
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file){
        logger.info(file.substring(sourceFolder.length()+1, file.length()));
        String path = file.substring(sourceFolder.length()+1, file.length());
        if(path.contains("nitor\\EsbMonitor\\")){
            path=path.replace("nitor\\EsbMonitor\\","");
            logger.debug(path);
        }
        return path;
    }
    /**Sets source and target
     * directories.
     * @param src Source directory path
     * @param target target file path
     * */
    public void setFileNames(String src, String target){
        this.sourceFolder=src;
        this.outputZipFile=target;
    }
}
