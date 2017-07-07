/**
 * 项目名称(中文)
 * 项目名称(英文)
 * Copyright (c) 2013 ChinaPay Ltd. All Rights Reserved.
 */
package com.app.service;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件构建响应对象.
 * 
 * @author xu.wei
 * @version 1.0 2013-11-22 改订
 * @since 1.0
 */
/**
 * @author xu.wei .
 */
public class FileBuildResponse {
    private List<FileBuildContext> successFileList = new ArrayList<FileBuildContext>();
    private List<FileBuildContext> failureFileList = new ArrayList<FileBuildContext>();
    private List<FileBuildContext> ignoreFileList = new ArrayList<FileBuildContext>();

    public FileBuildResponse() {
    }

    /**
     * .
     * 
     * @return successFileList
     */
    public List<FileBuildContext> getSuccessFileList() {
        return successFileList;
    }

    /**
     * .
     * 
     * @param successFile
     *            successFile
     */
    public void addSuccessFile(FileBuildContext successFile) {
        this.successFileList.add(successFile);
    }

    /**
     * .
     * 
     * @return failureFileList
     */
    public List<FileBuildContext> getFailureFileList() {
        return failureFileList;
    }

    /**
     * .
     * 
     * @param failureFile
     *            failureFile
     */
    public void addFailureFile(FileBuildContext failureFile) {
        this.failureFileList.add(failureFile);
    }

    /**
     * .
     * 
     * @return ignoreFileList
     */
    public List<FileBuildContext> getIgnoreFileList() {
        return ignoreFileList;
    }

    /**
     * .
     * 
     * @param ignoreFile
     *            ignoreFile
     */
    public void addIgnoreFile(FileBuildContext ignoreFile) {
        this.ignoreFileList.add(ignoreFile);
    }

    public String getResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("success file list\r\n");

        for (FileBuildContext fcr : successFileList) {
            sb.append(String.format("%s\r\n", fcr.getInputPath()));
        }

        sb.append("failure file list\r\n");
        for (FileBuildContext fcr : failureFileList) {
            sb.append(String.format("%s\r\n", fcr.getInputPath()));
        }

        sb.append("ignore file list\r\n");
        for (FileBuildContext fcr : ignoreFileList) {
            sb.append(String.format("%s\r\n", fcr.getInputPath()));
        }

        return sb.toString();
    }

}
