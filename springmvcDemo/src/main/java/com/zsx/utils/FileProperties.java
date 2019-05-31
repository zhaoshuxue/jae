/*
 * @(#)com.bj58.daojia.mar.common.utils.FileProperties.java 1.0 2015/8/14 16:55
 * Copyright (c) 2015, 58daojia. All rights reserved.
 * 58daojia PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zsx.utils;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * FileProperties.java
 *
 * @author Yanjie Gao
 * @version f-1.9-web-agent-job
 * @date 2015/8/14 16:55
 */
public class FileProperties extends Properties {
    public FileProperties(String path){
        try{
            Resource resource = new DefaultResourceLoader().getResource(path);
            load(resource.getInputStream());
        }catch (Exception e){
            System.err.println("Exception in FileProperties(String): " + e.toString() + " for filename=" + path);
        }
    }

    public FileProperties(Properties properties){
        super(properties);
    }

    public static void main(String args[]){
        FileProperties fp = new FileProperties("/conf.properties");
        System.out.println(fp.getProperty("clusterName","121"));
    }
}
