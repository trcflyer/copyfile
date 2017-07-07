/**
 * 项目名称(中文)
 * 项目名称(英文)
 * Copyright (c) 2013 ChinaPay Ltd. All Rights Reserved.
 */
package com.app.service;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 类描述.
 * 
 * @author xu.wei
 * @version 1.0 2013-11-26 改订
 * @since 1.0
 */
public class InnerClassFilenameFilter implements FilenameFilter {
    private String fileName;

    public InnerClassFilenameFilter(String fileFullName) {
        int index = fileFullName.lastIndexOf(".");
        this.fileName = fileFullName.substring(0, index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String) .
     * 
     * @param dir
     * 
     * @param name
     * 
     * @return
     */
    public boolean accept(File dir, String name) {
        if (name.endsWith(".class")) {
            if (name.indexOf(fileName + "$") != -1) {
                return true;
            }
        }
        return false;
    }

}
