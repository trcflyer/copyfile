package com.app.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import com.app.service.FileBuildRequest;
import com.app.service.FileBuildResponse;
import com.app.service.FileBuildService;
import com.app.util.ZipUtil;
import com.chinapay.util.StringUtils;
import com.chinapay.util.date.DateUtil;
import com.chinapay.util.log.TraceLog;

/**
 * 复制文件servlet.
 * 
 * @author xu.wei .
 */
public class CopyChangeListExServlet extends HttpServlet {

    /**
     * .
     */
    private static final long serialVersionUID = 5227017993491166137L;

    /**
     * 调用post.
     * 
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * post.
     * 
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map map = new HashMap();
        BufferedInputStream in = null;
        try {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List items = upload.parseRequest(request);
            Iterator iterator = items.iterator();
            String content = null;
            while (iterator.hasNext()) {
                FileItem item = (FileItem) iterator.next();
                String contentType = item.getContentType();
                if (contentType == null) {
                    map.put(item.getFieldName(), item.getString("UTF-8"));
                    continue;
                }
                in = new BufferedInputStream(item.getInputStream());
                byte[] buffer = new byte[1024];
                int n;
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
                content = out.toString();
            }

            if (StringUtils.isNotEmpty((String) map.get("content"))) {
                content = (String) map.get("content");
            }

            if (content == null || content.trim().length() == 0) {
                throw new RuntimeException("文件列表未选择或文件内容为空");
            }

            String projectPath = (String) map.get("projectPath");
            String toRootPath = (String) map.get("toRootPath");
            String classPaths = (String) map.get("classPaths");
            String srcFolders = (String) map.get("srcFolders");
            String webPaths = (String) map.get("webPaths");
            String deleteFlag = (String) map.get("deleteFlag");
            String zipFlag = (String) map.get("zipFlag");
            String jarFlag = (String) map.get("jarFlag");
            String projectName = (String) map.get("projectName");
            String warName = (String) map.get("warName");
            String version = (String) map.get("version");

            if (projectPath.toLowerCase().indexOf(toRootPath.toLowerCase()) != -1) {
                throw new RuntimeException("目标文件根路径不能为工作空间路径父路径");
            }
            
            if (StringUtils.isNotEmpty(version)) {
                if(toRootPath.endsWith("/")){
                    toRootPath = toRootPath + DateUtil.getToday() + "." + version + "/";
                }else {
                    toRootPath = toRootPath + "/" + DateUtil.getToday() + "." + version + "/";
                }
                new File(toRootPath).mkdir();
            }

            FileBuildRequest fileBuildRequest = new FileBuildRequest();
            fileBuildRequest.setOutputFolder(toRootPath);
            String[] strs = classPaths.split(",");
            for (String path : strs) {
                fileBuildRequest.addClassPath(path);
            }
            strs = webPaths.split(",");
            for (String path : strs) {
                fileBuildRequest.addWebPath(path);
            }
            strs = srcFolders.split(",");
            for (String path : strs) {
                fileBuildRequest.addSrcPath(path);
            }
            String[] lines = content.split("\r\n");

            int projectNameIndex = -1;
            if (projectName != null && projectName.trim().length() > 0) {
                fileBuildRequest.setProjectName(projectName);

                if (!projectPath.endsWith("/")) {
                    projectPath += "/";
                }

                fileBuildRequest.setProjectPath(projectPath + projectName);
                // 无需查找项目名称
                projectNameIndex = 0;
            }

            for (String line : lines) {
                if (line.trim().length() == 0) {
                    continue;
                }

                if (projectNameIndex == -1) {
                    if (line.startsWith("/")) {
                        projectNameIndex = line.indexOf("/", 1);
                        if (projectNameIndex == -1) {
                            throw new RuntimeException("项目名称未找到，文件输入错误");
                        }
                        projectName = line.substring(1, projectNameIndex);
                    } else {
                        projectNameIndex = line.indexOf("/");
                        if (projectNameIndex == -1) {
                            throw new RuntimeException("项目名称未找到，文件输入错误");
                        }
                        projectName = line.substring(0, projectNameIndex);
                    }

                    fileBuildRequest.setProjectName(projectName);

                    if (!projectPath.endsWith("/")) {
                        projectPath += "/";
                    }
                    fileBuildRequest.setProjectPath(projectPath + projectName);
                }

                fileBuildRequest.addInputFile(line.substring(projectNameIndex)
                        .trim());
            }

            fileBuildRequest.setWarName(warName);

            FileBuildResponse fileBuildResponse = FileBuildService
                    .buildFile(fileBuildRequest);

            if ("1".equals(jarFlag)) {
                if (fileBuildResponse.getSuccessFileList() != null
                        && fileBuildResponse.getSuccessFileList().size() > 0) {
                    String packagePath = fileBuildRequest.getOutputFolder()
                            + fileBuildRequest.getWarName() + ".war"
                            + "/WEB-INF/classes";
                    // 复制class到jar目录
                    if ("1".equals(jarFlag)) {
                        FileUtils.copyDirectory(
                                new File(packagePath),
                                new File(toRootPath + "/"
                                        + fileBuildRequest.getWarName()
                                        + ".jar"));
                    }
                }
            }

            if ("1".equals(zipFlag)) {
                String packagePath = fileBuildRequest.getOutputFolder()
                        + fileBuildRequest.getWarName() + ".war";
                File file = new File(packagePath);
                File zipFile = new File(packagePath + ".zip");
                if (zipFile.exists() && zipFile.isFile()) {
                    zipFile.delete();
                }
                if (fileBuildResponse.getSuccessFileList().size() > 0) {
                    ZipUtil.zip(file, zipFile);
                }
            }

            if ("1".equals(deleteFlag)) {
                String folderPath = fileBuildRequest.getOutputFolder()
                        + fileBuildRequest.getWarName() + ".war";
                File folder = new File(folderPath);
                if (folder.isDirectory() && folder.exists()) {
                    deleteDirectory(folder);
                }
            }
            
            request.setAttribute("sucList",
                    fileBuildResponse.getSuccessFileList());
            request.setAttribute("errList",
                    fileBuildResponse.getFailureFileList());
            request.setAttribute("innList",
                    fileBuildResponse.getIgnoreFileList());

        } catch (Exception e) {
            TraceLog.logStackTrace(this, (Throwable) e);
            request.setAttribute("msg", e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    // ignore
                }
            }
            request.getRequestDispatcher("/jsp/copyChangeListResultEx.jsp")
                    .forward(request, response);
        }

    }

    /**
     * 删除目录及其子目录 .
     * 
     * @param file
     *            目录
     */
    private static void deleteDirectory(File file) {
        File[] list = file.listFiles();
        for (File f : list) {
            if (f.isFile()) {
                f.delete();
            } else {
                deleteDirectory(f);
            }
        }
        file.delete();
    }
}