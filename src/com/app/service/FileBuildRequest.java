/**
 * 项目名称(中文)
 * 项目名称(英文)
 * Copyright (c) 2013 ChinaPay Ltd. All Rights Reserved.
 */
package com.app.service;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件构建请求对象.
 * 
 * @author xu.wei
 * @version 1.0 2013-11-22 改订
 * @since 1.0
 */
public class FileBuildRequest {
    /**
     * 输出文件夹.
     */
    private String outputFolder;
    /**
     * 工程路径.
     */
    private String projectPath;
    /**
     * 工程名.
     */
    private String projectName;
    /**
     * 打包文件名.
     */
    private String warName;
    /**
     * 输入文件列表,从项目根路径开始.
     */
    private List<String> inputFileList = new ArrayList<String>();
    /**
     * 源代码路径列表.
     */
    private List<String> srcPathList = new ArrayList<String>();
    /**
     * 类路径列表.
     */
    private List<String> classPathList = new ArrayList<String>();
    /**
     * web路径列表.
     */
    private List<String> webPathList = new ArrayList<String>();

    /**
     * .
     * 
     * @return outputFolder
     */
    public String getOutputFolder() {
        return outputFolder;
    }

    /**
     * .
     * 
     * @param outputFolder
     *            outputFolder
     */
    public void setOutputFolder(String outputFolder) {
        this.outputFolder = FileBuildService.formatFolderPath(outputFolder);
    }

    /**
     * .
     * 
     * @return projectPath
     */
    public String getProjectPath() {
        return projectPath;
    }

    /**
     * .
     * 
     * @param projectPath
     *            projectPath
     */
    public void setProjectPath(String projectPath) {
        this.projectPath = FileBuildService.formatFolderPath(projectPath);
    }

    /**
     * .
     * 
     * @return projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * .
     * 
     * @param projectName
     *            projectName
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    /**
     * .
     * @return warName
     */
    public String getWarName() {
        if(warName == null || warName.trim().length() == 0){
            return projectName;
        }
        return warName;
    }

    /**
     * .
     * @param warName warName
     */
    public void setWarName(String warName) {
        this.warName = warName;
    }

    /**
     * .
     * 
     * @return inputFileList
     */
    public List<String> getInputFileList() {
        return inputFileList;
    }

    /**
     * .
     * 
     * @param inputFile
     *            inputFile
     */
    public void addInputFile(String inputFile) {
        this.inputFileList.add(FileBuildService.formatFilePath(inputFile));
    }

    /**
     * .
     * 
     * @return classPathList
     */
    public List<String> getClassPathList() {
        return classPathList;
    }

    /**
     * .
     * 
     * @param classPath
     *            classPath
     */
    public void addClassPath(String classPath) {
        this.classPathList.add(FileBuildService.formatFolderPath(classPath));
    }

    /**
     * .
     * 
     * @return srcPathList
     */
    public List<String> getSrcPathList() {
        return srcPathList;
    }

    /**
     * .
     * 
     * @param srcPath
     *            srcPath
     */
    public void addSrcPath(String srcPath) {
        this.srcPathList.add(FileBuildService.formatFolderPath(srcPath));
    }

    /**
     * .
     * 
     * @return webPathList
     */
    public List<String> getWebPathList() {
        return webPathList;
    }

    /**
     * .
     * 
     * @param webPath
     *            webPath
     */
    public void addWebPath(String webPath) {
        this.webPathList.add(FileBuildService.formatFolderPath(webPath));
    }

}
