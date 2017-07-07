/**
 * 项目名称(中文)
 * 项目名称(英文)
 * Copyright (c) 2013 ChinaPay Ltd. All Rights Reserved.
 */
package com.app.service;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件构建上下文.
 * 
 * @author xu.wei
 * @version 1.0 2013-11-25 改订
 * @since 1.0
 */
public class FileBuildContext {
    /**
     * 输入文件路径.
     */
    private String inputPath;
    /**
     * 原文件路径.
     */
    private String srcPath;
    /**
     * 目的文件路径.
     */
    private String dstPath;
    /**
     * 上下文路径.
     */
    private String ctxPath;
    /**
     * 相对路径.
     */
    private String relativePath;
    
    /**
     * 内部类列表.
     */
    private List<String> innerClassPathList = new ArrayList<String>();

    /**
     * .
     * 
     * @return inputPath
     */
    public String getInputPath() {
        return inputPath;
    }

    /**
     * .
     * 
     * @param inputPath
     *            inputPath
     */
    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    /**
     * .
     * 
     * @return srcPath
     */
    public String getSrcPath() {
        return srcPath;
    }

    /**
     * .
     * 
     * @param srcPath
     *            srcPath
     */
    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    /**
     * .
     * 
     * @return dstPath
     */
    public String getDstPath() {
        return dstPath;
    }

    /**
     * .
     * 
     * @param dstPath
     *            dstPath
     */
    public void setDstPath(String dstPath) {
        this.dstPath = dstPath;
    }

    /**
     * .
     * 
     * @return ctxPath
     */
    public String getCtxPath() {
        return ctxPath;
    }

    /**
     * .
     * 
     * @param ctxPath
     *            ctxPath
     */
    public void setCtxPath(String ctxPath) {
        this.ctxPath = ctxPath;
    }

    /**
     * .
     * 
     * @return relativePath
     */
    public String getRelativePath() {
        return relativePath;
    }

    /**
     * .
     * 
     * @param relativePath
     *            relativePath
     */
    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    /**
     * .
     * @return innerClassPathList
     */
    public List<String> getInnerClassPathList() {
        return innerClassPathList;
    }

    /**
     * .
     * @param innerClassPathList innerClassPathList
     */
    public void setInnerClassPathList(List<String> innerClassPathList) {
        this.innerClassPathList = innerClassPathList;
    }
    
    

}
