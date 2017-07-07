/**
 * 项目名称(中文)
 * 项目名称(英文)
 * Copyright (c) 2013 ChinaPay Ltd. All Rights Reserved.
 */
package com.app.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.app.util.ZipUtil;

/**
 * 文件构建服务.
 * 
 * @author xu.wei
 * @version 1.0 2013-11-22 改订
 * @since 1.0
 */
public class FileBuildService {
    /**
     * 测试.
     * 
     * @param args
     *            参数
     */
    public static void main(String[] args) throws Exception {
        FileBuildRequest request = new FileBuildRequest();
        request.setOutputFolder("/Users/hrtc/Documents/builder/");
        request.setProjectPath("/Users/hrtc/Documents/ws/eclipse/trunk/copyfile");
        request.addClassPath("build/classes");
        request.addWebPath("WebContent");
        request.addSrcPath("src");
        request.setProjectName("myApp");
        request.addInputFile("/WebContent/jsp/*");
        request.addInputFile("src/com/app/servlet/CopyChangeListServlet1.java");
        FileBuildResponse response = FileBuildService.buildFile(request);

        File file = new File("/Users/hrtc/Documents/builder/copyfile.war");
        File zipFile = new File("/Users/hrtc/Documents/builder/copyfile.war.zip");
        ZipUtil.zip(file, zipFile);

        System.out.println(response.getResults());
    }

    /**
     * 构建文件,目录路径以/结尾,文件路径前不带/.
     * 
     * @param request
     *            请求
     * @return 响应
     */
    public static FileBuildResponse buildFile(FileBuildRequest request) {
        prepareBuild(request);
        FileBuildResponse response = new FileBuildResponse();

        String outputPath = request.getOutputFolder() + request.getWarName()
                + ".war/";

        // 获得输出目录
        //File outputFolder = new File(outputPath);
        //outputFolder.mkdirs();

        // 获得项目根目录
        File projectFolder = new File(request.getProjectPath());
        if (!projectFolder.exists() || !projectFolder.isDirectory()) {
            throw new RuntimeException(String.format(
                    "Project folder not exists,path = %s", projectFolder.getPath()));
        }

        for (String inputFile : request.getInputFileList()) {
            FileBuildContext fileBuildContext = new FileBuildContext();
            fileBuildContext.setInputPath(inputFile);

            buildRelativePath(fileBuildContext, request.getSrcPathList());

            if (fileBuildContext.getRelativePath() != null
                    && fileBuildContext.getRelativePath().trim().length() > 0) {
                buildCopyPathFromSrc(fileBuildContext, request, outputPath);
            } else {
                buildRelativePath(fileBuildContext, request.getWebPathList());

                if (fileBuildContext.getRelativePath() != null
                        && fileBuildContext.getRelativePath().trim().length() > 0) {
                    buildCopyPathFromWeb(fileBuildContext, request, outputPath);

                }
            }

            if (fileBuildContext.getDstPath() == null) {
                response.addIgnoreFile(fileBuildContext);
            } else {
                try {
                    if (fileBuildContext.getSrcPath().endsWith(".class")) {
                        File file = new File(fileBuildContext.getSrcPath());
                        File[] files = file.getParentFile().listFiles(
                                new InnerClassFilenameFilter(file.getName()));
                        if (files != null && files.length > 0) {
                            for (File f : files) {
                                File dstF = new File(
                                        fileBuildContext.getDstPath());
                                copyFile(f.getPath(), dstF.getParentFile()
                                        .getPath() + "/" + f.getName());
                                fileBuildContext.getInnerClassPathList().add(dstF.getParentFile()
                                        .getPath() + "/" + f.getName());
                            }
                        }
                    }
                    copyFile(fileBuildContext.getSrcPath(),
                            fileBuildContext.getDstPath());
                    response.addSuccessFile(fileBuildContext);
                } catch (IOException e) {
                    response.addFailureFile(fileBuildContext);
                }
            }

        }

        return response;
    }

    /**
     * 预构建，过滤同名文件路径和转换*号通配符 .
     * 
     * @param request
     *            请求参数
     */
    private static void prepareBuild(FileBuildRequest request) {
        Set<String> lines = new HashSet<String>();
        for (String inputFile : request.getInputFileList()) {
            FileBuildContext fileBuildContext = new FileBuildContext();
            fileBuildContext.setInputPath(inputFile);

            buildRelativePath(fileBuildContext, request.getSrcPathList());

            if (fileBuildContext.getRelativePath() == null
                    || fileBuildContext.getRelativePath().trim().length() == 0) {
                buildRelativePath(fileBuildContext, request.getWebPathList());
            }

            if (fileBuildContext.getRelativePath() != null
                    && fileBuildContext.getRelativePath().trim().length() > 0) {
                if (fileBuildContext.getRelativePath().endsWith("*")) {
                    String path = String.format(
                            "%s%s%s",
                            request.getProjectPath(),
                            fileBuildContext.getCtxPath(),
                            fileBuildContext.getRelativePath()
                                    .substring(
                                            0,
                                            fileBuildContext.getRelativePath()
                                                    .length() - 1));
                    File folder = new File(path);
                    if (folder.isDirectory() && folder.exists()) {
                        Set<String> filePathSet = new HashSet<String>();
                        buildFileList(filePathSet, folder);
                        for (String filePath : filePathSet) {
                            filePath = filePath.replaceAll("\\\\", "/");
                            filePath = filePath.substring(request
                                    .getProjectPath().length() - 1);
                            lines.add(FileBuildService.formatFilePath(filePath));
                        }
                    }
                } else {
                    lines.add(inputFile);
                }
            }
        }

        request.getInputFileList().clear();
        request.getInputFileList().addAll(lines);
    }

    /**
     * 根据web路径构建复制路径 .
     * 
     * @param fileBuildContext
     *            文件构建上下文
     * @param request
     *            文件构建请求
     * @param outputPath
     *            输出路径
     * 
     */
    private static void buildCopyPathFromWeb(FileBuildContext fileBuildContext,
            FileBuildRequest request, String outputPath) {
        fileBuildContext.setSrcPath(String.format("%s%s%s",
                request.getProjectPath(), fileBuildContext.getCtxPath(),
                fileBuildContext.getRelativePath()));
        File f = new File(fileBuildContext.getSrcPath());
        if (f.isFile() && f.exists()) {
            fileBuildContext.setDstPath(String.format("%s%s", outputPath,
                    fileBuildContext.getRelativePath()));
        }
    }

    /**
     * 
     * 根据源代码路径构建复制路径 .
     * 
     * @param fileBuildContext
     *            文件构建上下文
     * @param request
     *            构建请求对象
     * @param outputPath
     *            输出路径
     */
    private static void buildCopyPathFromSrc(FileBuildContext fileBuildContext,
            FileBuildRequest request, String outputPath) {
        if (fileBuildContext.getRelativePath().endsWith(".java")) {
            int index = fileBuildContext.getRelativePath().lastIndexOf("/");
            if (index != -1) {
                String fileName = fileBuildContext.getRelativePath().substring(
                        index + 1,
                        fileBuildContext.getRelativePath().length() - 5);
                fileBuildContext.setRelativePath(String.format("%s/%s.class",
                        fileBuildContext.getRelativePath().substring(0, index),
                        fileName));
            } else {
                String fileName = fileBuildContext.getRelativePath().substring(
                        0, fileBuildContext.getRelativePath().length() - 5);
                fileBuildContext.setRelativePath(String.format("%s.class",
                        fileName));
            }
        }

        for (String classPath : request.getClassPathList()) {
            fileBuildContext.setSrcPath(String.format("%s%s%s",
                    request.getProjectPath(), classPath,
                    fileBuildContext.getRelativePath()));
            File f = new File(fileBuildContext.getSrcPath());
            if (f.isFile() && f.exists()) {
                fileBuildContext.setDstPath(String.format(
                        "%sWEB-INF/classes/%s", outputPath,
                        fileBuildContext.getRelativePath()));
                break;
            }
        }
    }

    /**
     * 获得相对路径,为空表示没有找到 .
     * 
     * @param fileBuildContext
     *            输入路径
     * @param pathList
     *            环境路径列表
     */
    private static void buildRelativePath(FileBuildContext fileBuildContext,
            List<String> pathList) {
        String strRelativePath = null;
        for (String strPath : pathList) {
            if (fileBuildContext.getInputPath().startsWith(strPath)) {
                strRelativePath = fileBuildContext.getInputPath().substring(
                        strPath.length());
                fileBuildContext.setRelativePath(strRelativePath);
                fileBuildContext.setCtxPath(strPath);
                break;
            }
        }
    }

    /**
     * 复制文件.
     * 
     * @param srcFilePath
     *            原文件路径
     * @param dstFilePath
     *            目标文件路径
     * @throws IOException
     *             io异常
     */
    public static void copyFile(String srcFilePath, String dstFilePath)
            throws IOException {
        int pos = dstFilePath.lastIndexOf("/");
        if (pos != -1) {
            File f = new File(dstFilePath.substring(0, pos));
            if (!f.exists()) {
                f.mkdirs();
            }
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcFilePath);
            fos = new FileOutputStream(dstFilePath);

            byte[] buf = new byte[4096];
            int count = fis.read(buf);
            while (count != -1) {
                fos.write(buf, 0, count);
                count = fis.read(buf);
            }
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * 格式化文件夹路径 .
     * 
     * @param folderPath
     *            folderPath
     * @return folderPath
     */
    public static String formatFolderPath(String folderPath) {
        folderPath = folderPath.replaceAll("\\\\", "/");
        if (!folderPath.endsWith("/")) {
            return folderPath + "/";
        } else {
            return folderPath;
        }
    }

    /**
     * 格式化文件路径 .
     * 
     * @param filePath
     *            filePath
     * @return filePath
     */
    public static String formatFilePath(String filePath) {
        filePath = filePath.replaceAll("\\\\", "/");
        if (filePath.startsWith("/")) {
            return filePath.substring(1);
        } else {
            return filePath;
        }
    }

    /**
     * 构建文件路径集合 .
     * 
     * @param filePathSet
     *            返回的文件路径集合
     * @param folder
     *            文件夹
     */
    public static void buildFileList(Set<String> filePathSet, File folder) {
        for (File f : folder.listFiles()) {
            if (f.isFile()) {
                filePathSet.add(f.getPath());
            } else {
                buildFileList(filePathSet, f);
            }
        }
    }

}
