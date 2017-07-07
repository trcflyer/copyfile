package com.app.util;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
 
public class FileUtil {
    private final static String FILE_SUFFIX = ".java.drl";
 
    private final static String FILE_TEMP = "C:/temp/";
 
    /**
     * 将已存在的drl文件删除
     *
     * @param ObjectPath
     */
    public static void deleteExistedDRLFile(String ObjectPath) {
        File filePath = new File(ObjectPath);
        if (!filePath.exists()) {
            System.out.println("目录不存在!");
        } else {
            if (filePath.isDirectory()) {
                File[] list = filePath.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(FILE_SUFFIX);
                    }
                });
                for (int i = 0; i < list.length; i++) {
                    list[i].delete();
                }
            }
        }
    }
 
    /**
     * 创建文件夹,如果有则不创建
     *
     * @param ObjectPath
     */
    public static boolean creareDirectory(String ObjectPath) {
        boolean flag = true;
 
        File filePath = new File(ObjectPath);
        if (!filePath.exists()) {
            filePath.mkdir();
            flag = false;
        }
 
        return flag;
    }
 
    /**
     * 查看某文件夹下面是否有文件,有文件则创建一个temp文件夹,将文件拷贝到temp目录下(备份文件) 没有文件怎什么都不做
     * 备份后，把原文件夹里文件删除
     *
     * @param ObjectPath
     */
    public static void backupFile(String ObjectPath, String dirName) {
        String backupPath;
 
        if (!FILE_TEMP.endsWith(File.separator)) {
            backupPath = FILE_TEMP + File.separator + dirName;
        } else {
            backupPath = FILE_TEMP + dirName;
        }
 
        File backupFilePath = new File(backupPath);
        if (!backupFilePath.exists()) {
            backupFilePath.mkdirs();
        }
        File filePath = new File(ObjectPath);
        if (!filePath.exists()) {
            System.out.println("目录不存在!");
        } else {
            if (filePath.isDirectory()) {
                File[] list = filePath.listFiles();
                if (list != null && list.length != 0) {
                    copyFolder(ObjectPath, backupPath);// 文件备份
                    for (int i = 0; i < list.length; i++) {
                        list[i].delete();
                    }
                }
            }
        }
    }
 
    /**
     * 复原文件，把文件从备份文件夹拷贝到原来文件夹
     *
     * @param ObjectPath
     * @param dirName
     */
    public static void recoverFile(String ObjectPath, String dirName) {
        String backupPath;
        if (ObjectPath.endsWith(File.separator)) {
            ObjectPath = new StringBuffer(ObjectPath).append(dirName)
                    .toString();
        } else {
            ObjectPath = new StringBuffer(ObjectPath)
                    .append(File.separatorChar).append(dirName).toString();
        }
 
        if (!FILE_TEMP.endsWith(File.separator)) {
            backupPath = FILE_TEMP + File.separator + dirName;
        } else {
            backupPath = FILE_TEMP + dirName;
        }
        File backupFilePath = new File(backupPath);
        if (!backupFilePath.exists()) {
            backupFilePath.mkdirs();
        }
        File filePath = new File(ObjectPath);
        if (!filePath.exists()) {
            System.out.println("目录不存在!");
        } else {
            if (filePath.isDirectory()) {
                File[] list = filePath.listFiles();
                if (list != null && list.length != 0) {
                    copyFolder(backupPath, ObjectPath);// 文件复原
                }
            }
        }
    }
 
    /**
     * 复制整个文件夹内容
     *
     * @param oldPath
     *            String 原文件路径 如：c:/fqf
     * @param newPath
     *            String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdir(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
 
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath
                            + "/" + (temp.getName()).toString());
 
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }
    }
 
    /**
     * 删除备份文件和存放备份文件的文件夹
     *
     * @param ObjectPath
     */
    public static void deleteFileAndDirectory(String dirName) {
        String ObjectPath;
        if (!FILE_TEMP.endsWith(File.separator)) {
            ObjectPath = FILE_TEMP + File.separator + dirName;
        } else {
            ObjectPath = FILE_TEMP + dirName;
        }
 
        File filePath = new File(ObjectPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
            System.out.println("目录不存在!");
        } else {
            if (filePath.isDirectory()) {
                File[] list = filePath.listFiles();
 
                for (int i = 0; i < list.length; i++) {
                    list[i].delete();
                }
            }
            filePath.delete();
        }
    }
 
    /**
     * 判断某文件夹下是否存在文件，存在返回true
     *
     * @param ObjectPath
     * @return
     */
    public static boolean existFileInDirectory(String ObjectPath) {
        boolean flag = false;
        File filePath = new File(ObjectPath);
        if (filePath.exists()) {
 
            if (filePath.isDirectory()) {
                File[] list = filePath.listFiles();
                if (list != null && list.length != 0) {
                    flag = true;
                }
            }
        }
 
        return flag;
    }
 
    /**
     * 删除某个文件夹
     * @param ObjectPath
     */
    public static void deleteDirectory(String ObjectPath) {
 
        File filePath = new File(ObjectPath);
        if (filePath.exists()) {
            filePath.delete();
        }
    }
    /**
     * 将已存在的文件删除
     *
     * @param ObjectPath
     */
    public static boolean deleteExistedFile(String ObjectPath,final String fileName) {
        boolean flag =false;
        File filePath = new File(ObjectPath);
        if (filePath.exists()) {           
            if (filePath.isDirectory()) {
                File[] list = filePath.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.equals(fileName);
                    }
                });
                for (int i = 0; i < list.length; i++) {
                    list[i].delete();
                }
                flag=true;
            }
        }
         
        return flag;
    }
}