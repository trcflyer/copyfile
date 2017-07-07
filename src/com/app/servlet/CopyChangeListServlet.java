package com.app.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

import com.app.util.InnerClassFilter;
import com.app.util.ZipUtil;
import com.chinapay.util.StringUtils;
import com.chinapay.util.date.DateUtil;
import com.chinapay.util.log.TraceLog;

public class CopyChangeListServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5227017993491166137L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

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
			String fromRootPath = (String) map.get("fromRootPath");
			String toRootPath = (String) map.get("toRootPath");
			String srcFlag = (String) map.get("srcFlag");
			String classesFlag = (String) map.get("classesFlag");
			String warFlag = (String) map.get("warFlag");
			String zipFlag = (String) map.get("zipFlag");
			String innerClass = (String) map.get("innerClass");
			String workName = (String) map.get("workName");
			String version = (String) map.get("version");
			String srcFolder = (String) map.get("srcFolder");
			String jarFlag = (String) map.get("jarFlag");
			if (StringUtils.isNotEmpty((String) map.get("content"))) {
				content = (String) map.get("content");
			}
			
			if (StringUtils.isNotEmpty(version)) {
				toRootPath = toRootPath + DateUtil.getToday() + "." + version + "/";
				new File(toRootPath).mkdir();
			}
			// 删除已有子文件夹及子文件
//			File[] fs = new File(toRootPath).listFiles();
//			for (int i = 0; i < fs.length; i++) {
//				com.app.util.FileUtils.DeleteFolder(fs[i].getAbsolutePath());
//				TraceLog.debug(this, "删除[" + fs[i].getAbsolutePath() + "]已完成!");
//			}
			int endPos = content.indexOf("/", 1);
			File[] fs = new File(toRootPath).listFiles();
			for (int i = 0; i < fs.length; i++) {
				if (fs[i].getName().startsWith(content.substring(1, endPos))) {
					com.app.util.FileUtils.DeleteFolder(fs[i].getAbsolutePath());
					TraceLog.debug(this, "删除[" + fs[i].getAbsolutePath() + "]已完成!");
				} else {
					TraceLog.debug(this, "文件[" + fs[i].getAbsolutePath() + "]与新生成文件无歧义或无冲突，跳过删除动作!");
					continue;
				}
			}

			List list = copyFiles(fromRootPath, toRootPath, content, srcFlag, classesFlag, warFlag, zipFlag,
					innerClass, srcFolder, workName, jarFlag);
			List sucList = (List) list.get(0);
			List errList = (List) list.get(1);
			List innList = (List) list.get(2);
			List fonerList = (List) list.get(2);

			if (sucList.size() > 0) {
				String filePath = ((String[]) sucList.get(0))[1];
				int appEndPos = filePath.indexOf("\\", toRootPath.length() + 1);
				String appName = filePath.substring(toRootPath.length(), appEndPos);
				String srcPath = toRootPath + appName + "/WEB-INF/classes";
				// 复制class到jar目录
				if ("1".equals(jarFlag)) {
					FileUtils.copyDirectory(new File(srcPath), new File(toRootPath + "/jar"));
				}
			}

			request.setAttribute("sucList", sucList);
			request.setAttribute("errList", errList);
			request.setAttribute("innList", innList);

			request.getRequestDispatcher("/jsp/copyChangeListResult.jsp").forward(request,
					response);
		} catch (Exception e) {
			TraceLog.logStackTrace(this, (Throwable) e);
		} finally {
			if (in != null) {
				in.close();
			}
		}

	}

	private List copyFiles(String fromRootPath, String toRootPath, String content, String srcFlag,
			String classesFlag, String warFlag, String zipFlag, String innerClass, String srcFolder, String workName, String jarFlag) {
		List list = new ArrayList();
		List sucList = new ArrayList();
		List errList = new ArrayList();
		List innList = new ArrayList();

		String[] lines = content.split("\r\n");
		String[] inners = innerClass.split(",");
		String[] srcFolders = srcFolder.split(",");
		
		List fileKeyList = new ArrayList();
		List dirKeyList = new ArrayList();
		for (int i = 0; i < inners.length; i++) {
			if (inners[i].indexOf(".") > 0) {
				fileKeyList.add(inners[i]);
			} else if (StringUtils.isNotEmpty(inners[i])) {
				dirKeyList.add(inners[i]);
			}
		}

		for (int i = 0; i < lines.length; i++) {
			String fileName = lines[i].trim();
			File sourceFile = null;
			File targetFile = null;
			String folder = null;
			int idx = 0;
			int startPos = fileName.indexOf("/", 1);
			
			for (int j = 0; j < srcFolders.length; j++) {
				folder = "/" + srcFolders[j] + "/";
				idx = fileName.indexOf(folder);
				if (idx >= 0) {
					// 起始目录
					if (idx == startPos) {
						break;
					} else {
						// 中间目录
						idx = -1;
					}
				}
			}

			// src文件
			if ("1".equals(srcFlag) && idx >= 0) {
				// 通配符结尾
				if (fileName.endsWith("*")) {
					File tmpFold = new File(fromRootPath + "/" + fileName.substring(0, fileName.length() - 1));
					
					targetFile = new File(toRootPath
							+ "/"
							+ fileName.substring(0, fileName.length() - 1));
					
					try {
						copyFolder(tmpFold, targetFile, sucList, errList);
//						sucList.add(new String[] { tmpFold.getAbsolutePath(),
//								targetFile.getAbsolutePath() });
					} catch (Exception e) {
//						errList.add(new String[] { tmpFold.getAbsolutePath(),
//								targetFile.getAbsolutePath() });
						TraceLog.logStackTrace(this, (Throwable) e);
					}
					
//					File[] allFile = tmpFold.listFiles();
//					for (int j = 0; j < allFile.length; j++) {
//						if (allFile[j].isDirectory() || allFile[j].isHidden()) {
//							continue;
//						}
//						String oneFile = allFile[j].getAbsolutePath();
//						oneFile = oneFile.substring(fromRootPath.length());
//						oneFile = oneFile.replaceAll("\\\\", "/");
//						copySrc(oneFile, fromRootPath, toRootPath, sucList, errList);
//					}
				} else {
					copySrc(fileName, fromRootPath, toRootPath, sucList, errList);
				}
//				sourceFile = new File(fromRootPath + "/" + fileName);
//				targetFile = new File(toRootPath + "/" + fileName);
//
//				try {
//					FileUtils.copyFile(sourceFile, targetFile);
//					sucList.add(new String[] { sourceFile.getAbsolutePath(),
//							targetFile.getAbsolutePath() });
//				} catch (Exception e) {
//					errList.add(new String[] { sourceFile.getAbsolutePath(),
//							targetFile.getAbsolutePath() });
//					TraceLog.logStackTrace(this, (Throwable) e);
//				}
			}

			// classes文件
			if ("1".equals(classesFlag) && idx >= 0) {
				// 通配符结尾
				if (fileName.endsWith("*")) {
					File tmpFold = new File(fromRootPath + "/" + fileName.substring(0, fileName.length() - 1).replaceFirst("/WebContent", "").replaceFirst(folder,
							"/build/classes/"));
					// 调用通用文件夹copy
//					sourceFile = new File(fromRootPath + "/" + fileName);
					targetFile = new File(toRootPath
							+ "/"
							+ fileName.substring(0, fileName.length() - 1).replaceFirst("/WebContent", "").replaceFirst(folder,
									"/WEB-INF/classes/"));
					
					try {
						copyFolder(tmpFold, targetFile, sucList, errList);
//						sucList.add(new String[] { tmpFold.getAbsolutePath(),
//								targetFile.getAbsolutePath() });
					} catch (Exception e) {
//						errList.add(new String[] { tmpFold.getAbsolutePath(),
//								targetFile.getAbsolutePath() });
						TraceLog.logStackTrace(this, (Throwable) e);
					}
					
//					File[] allFile = tmpFold.listFiles();
//					for (int j = 0; j < allFile.length; j++) {
//						if (allFile[j].isDirectory() || allFile[j].isHidden()) {
//							continue;
//						}
//						String oneFile = allFile[j].getAbsolutePath();
//						oneFile = oneFile.substring(fromRootPath.length());
//						oneFile = oneFile.replaceAll("\\\\", "/");
//						copyClass(oneFile, folder, idx, fromRootPath, toRootPath, sucList, errList, innList, fileKeyList, dirKeyList);
//					}
				} else {
					copyClass(fileName, folder, idx, fromRootPath, toRootPath, sucList, errList, innList, fileKeyList, dirKeyList);
				}
				
//				if (fileName.endsWith(".java")) {
//					fileName = fileName.substring(0, fileName.length() - 5) + ".class";
//					fileName = fileName.replaceFirst(folder, "/classes/");
//
//					String[] path = new String[] { "/build/", "/WebContent/WEB-INF/" };
//
//					for (int j = 0; j < path.length; j++) {
//						if (idx > 0) {
//							sourceFile = new File(fromRootPath + "/" + fileName.substring(0, idx)
//									+ path[j] + fileName.substring(idx));
//						} else {
//							sourceFile = new File(fromRootPath + path[j] + fileName);
//						}
//						if (sourceFile.exists()) {
//							break;
//						}
//					}
//
//					if (idx > 0) {
//						targetFile = new File(toRootPath + "/" + fileName.substring(0, idx)
//								+ "/WEB-INF/" + fileName.substring(idx));
//					} else {
//						targetFile = new File(toRootPath + "/WEB-INF/" + fileName);
//					}
//				} else {
//					sourceFile = new File(fromRootPath + "/" + fileName);
//					targetFile = new File(toRootPath
//							+ "/"
//							+ fileName.replaceFirst("/WebContent", "").replaceFirst(folder,
//									"/WEB-INF/classes/"));
//				}
//
//				try {
//					FileUtils.copyFile(sourceFile, targetFile);
//					sucList.add(new String[] { sourceFile.getAbsolutePath(),
//							targetFile.getAbsolutePath() });
//				} catch (Exception e) {
//					errList.add(new String[] { sourceFile.getAbsolutePath(),
//							targetFile.getAbsolutePath() });
//					TraceLog.logStackTrace(this, (Throwable) e);
//				}
//
//				// 复制内部类
//				if (isInner(sourceFile, fileKeyList, dirKeyList)) {
//					String parent = targetFile.getParent();
//					File[] innerFiles = getInnerClassList(sourceFile);
//					for (int j = 0; innerFiles != null && j < innerFiles.length; j++) {
//						sourceFile = innerFiles[j];
//						targetFile = new File(parent + "/" + sourceFile.getName());
//						
//						try {
//							FileUtils.copyFile(sourceFile, targetFile);
//							innList.add(new String[] { sourceFile.getAbsolutePath(),
//									targetFile.getAbsolutePath() });
//						} catch (Exception e) {
//							errList.add(new String[] { sourceFile.getAbsolutePath(),
//									targetFile.getAbsolutePath() });
//							TraceLog.logStackTrace(this, (Throwable) e);
//						}
//					}
//				}
			}

			// 其他文件
			if (idx < 0) {
				// 通配符结尾
				if (fileName.endsWith("*")) {
					File tmpFold = new File(fromRootPath + "/" + fileName.substring(0, fileName.length() - 1));
					targetFile = new File(toRootPath
							+ "/"
							+ fileName.substring(0, fileName.length() - 1).replaceFirst("/WebContent", "").replaceFirst(folder,
									"/WEB-INF/classes/"));
					
					try {
						copyFolder(tmpFold, targetFile, sucList, errList);
//						sucList.add(new String[] { tmpFold.getAbsolutePath(),
//								targetFile.getAbsolutePath() });
					} catch (Exception e) {
//						errList.add(new String[] { tmpFold.getAbsolutePath(),
//								targetFile.getAbsolutePath() });
						TraceLog.logStackTrace(this, (Throwable) e);
					}
					
					
					
//					File[] allFile = tmpFold.listFiles();
//					for (int j = 0; j < allFile.length; j++) {
//						if (allFile[j].isDirectory() || allFile[j].isHidden()) {
//							continue;
//						}
//						String oneFile = allFile[j].getAbsolutePath();
//						oneFile = oneFile.substring(fromRootPath.length());
//						oneFile = oneFile.replaceAll("\\\\", "/");
//						copyOther(oneFile, fromRootPath, toRootPath, sucList, errList);
//					}
				} else {
					copyOther(fileName, fromRootPath, toRootPath, sucList, errList);
				}
//				sourceFile = new File(fromRootPath + "/" + fileName);
//				targetFile = new File(toRootPath + "/" + fileName.replaceFirst("/WebContent", ""));
//
//				try {
//					FileUtils.copyFile(sourceFile, targetFile);
//					sucList.add(new String[] { sourceFile.getAbsolutePath(),
//							targetFile.getAbsolutePath() });
//				} catch (Exception e) {
//					errList.add(new String[] { sourceFile.getAbsolutePath(),
//							targetFile.getAbsolutePath() });
//					TraceLog.logStackTrace(this, (Throwable) e);
//				}
			}
		}
		
		// 临时压缩使用
//		if (StringUtils.isNotEmpty(zipFlag)) {
		File file = new File(toRootPath  + "/" + lines[0].split("/")[1]);
		File tempFile = null;
//			if ("1".equals(warFlag)) {
		file = ZipUtil.rename(file);
//			}
		if (StringUtils.isNotEmpty(workName)) {
			String tempStr = file.getAbsolutePath();
			tempFile = new File(tempStr.replaceAll(tempStr.substring(tempStr.lastIndexOf(File.separator) + 1, tempStr.lastIndexOf(".war")), workName));
			file.renameTo(tempFile);
		}
		if (tempFile != null) {
			file = tempFile;
		}
		if ("1".equals(zipFlag)) {
			try {
				ZipUtil.zip(file, new File(file.getAbsolutePath()+".zip"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if ("1".equals(warFlag)) {
			com.app.util.FileUtils.DeleteFolder(file.getAbsolutePath());
		}
//		}

		list.add(sucList);
		list.add(errList);
		list.add(innList);

		return list;
	}
	
	public static void main(String[] args) {
		String path = "D:\\myAppTest\\cptp.war";
		System.out.println(new File(path).getName());
		int start = path.lastIndexOf("\\");
		System.out.println("start=" + start);
		int end = path.lastIndexOf(".war");
		System.out.println("end=" + end);
		System.out.println(path.substring(13, 34));
		System.out.println(path.replaceAll(path.substring(13, 34), "cptp"));
	}

	private void copySrc(String fileName, String fromRootPath, String toRootPath, 
			List sucList, List errList) {
		File sourceFile = new File(fromRootPath + "/" + fileName);
		File targetFile = new File(toRootPath + "/" + fileName);

		try {
			FileUtils.copyFile(sourceFile, targetFile);
			sucList.add(new String[] { sourceFile.getAbsolutePath(),
					targetFile.getAbsolutePath() });
		} catch (Exception e) {
			errList.add(new String[] { sourceFile.getAbsolutePath(),
					targetFile.getAbsolutePath() });
			TraceLog.logStackTrace(this, (Throwable) e);
		}
	}

	private void copyOther(String fileName, String fromRootPath, String toRootPath, 
			List sucList, List errList) {
		File sourceFile = new File(fromRootPath + "/" + fileName);
		File targetFile = new File(toRootPath + "/" + fileName.replaceFirst("/WebContent", ""));

		try {
			FileUtils.copyFile(sourceFile, targetFile);
			sucList.add(new String[] { sourceFile.getAbsolutePath(),
					targetFile.getAbsolutePath() });
		} catch (Exception e) {
			errList.add(new String[] { sourceFile.getAbsolutePath(),
					targetFile.getAbsolutePath() });
			TraceLog.logStackTrace(this, (Throwable) e);
		}
	}

	private void copyClass(String fileName, String folder, int idx, String fromRootPath, String toRootPath, 
			List sucList, List errList, List innList, List fileKeyList, List dirKeyList) {
		File sourceFile = null;
		File targetFile = null;
		if (fileName.endsWith(".java")) {
			fileName = fileName.substring(0, fileName.length() - 5) + ".class";
			fileName = fileName.replaceFirst(folder, "/classes/");

			String[] path = new String[] { "/build/", "/WebContent/WEB-INF/" };

			for (int j = 0; j < path.length; j++) {
				if (idx > 0) {
					sourceFile = new File(fromRootPath + "/" + fileName.substring(0, idx)
							+ path[j] + fileName.substring(idx));
				} else {
					sourceFile = new File(fromRootPath + path[j] + fileName);
				}
				if (sourceFile.exists()) {
					break;
				}
			}

			if (idx > 0) {
				targetFile = new File(toRootPath + "/" + fileName.substring(0, idx)
						+ "/WEB-INF/" + fileName.substring(idx));
			} else {
				targetFile = new File(toRootPath + "/WEB-INF/" + fileName);
			}
		} else {
			sourceFile = new File(fromRootPath + "/" + fileName);
			targetFile = new File(toRootPath
					+ "/"
					+ fileName.replaceFirst("/WebContent", "").replaceFirst(folder,
							"/WEB-INF/classes/"));
		}

		try {
			FileUtils.copyFile(sourceFile, targetFile);
			sucList.add(new String[] { sourceFile.getAbsolutePath(),
					targetFile.getAbsolutePath() });
		} catch (Exception e) {
			errList.add(new String[] { sourceFile.getAbsolutePath(),
					targetFile.getAbsolutePath() });
			TraceLog.logStackTrace(this, (Throwable) e);
		}

		// 复制内部类
		if (isInner(sourceFile, fileKeyList, dirKeyList)) {
			String parent = targetFile.getParent();
			File[] innerFiles = getInnerClassList(sourceFile);
			for (int j = 0; innerFiles != null && j < innerFiles.length; j++) {
				sourceFile = innerFiles[j];
				targetFile = new File(parent + "/" + sourceFile.getName());
				
				try {
					FileUtils.copyFile(sourceFile, targetFile);
					innList.add(new String[] { sourceFile.getAbsolutePath(),
							targetFile.getAbsolutePath() });
				} catch (Exception e) {
					errList.add(new String[] { sourceFile.getAbsolutePath(),
							targetFile.getAbsolutePath() });
					TraceLog.logStackTrace(this, (Throwable) e);
				}
			}
		}
	}

	private boolean isInner(File file, List fileKeyList, List dirKeyList) {
//		String name = file.getName();
//		String absolutePath = file.getAbsolutePath();
//		for (int i = 0; i < fileKeyList.size(); i++) {
//			String key = (String) fileKeyList.get(i);
//			if (name.endsWith(key)) {
//				return true;
//			}
//		}
//
//		for (int i = 0; i < dirKeyList.size(); i++) {
//			String key = (String) dirKeyList.get(i);
//			if (absolutePath.indexOf(key) > -1) {
//				return true;
//			}
//		}
//
//		return false;
		return true;
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
	public void copyFolder(File oldFile, File newFile, List sucList, List errList) {
		String oldPath = oldFile.getPath();
		String newPath = newFile.getPath();
//		TraceLog.debug(this, "oldPath=[" + oldPath + "],newPath=[" + newPath + "]");
		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
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
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
					
					sucList.add(new String[] { temp.getAbsolutePath(), newPath + "/" + (temp.getName()).toString() });
					errList.add(new String[] { temp.getAbsolutePath(), newPath + "/" + (temp.getName()).toString() });
					
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					// 删除.svn文件夹
					if (temp.getName().startsWith(".svn")) {
						TraceLog.debug(this, ">>>>>>>>>>>>>>>>>>>>>>.svn目录=[" + temp.getAbsolutePath() + "]跳过");
						continue;
					}
					copyFolder(new File(oldPath + "/" + file[i]), new File(newPath + "/" + file[i]), sucList, errList);
				}
			}
			
			errList.removeAll(errList);
		} catch (Exception e) {
			sucList.removeAll(sucList);
			TraceLog.error(this, "复制整个文件夹内容操作出错");
			e.printStackTrace();
		}
	}

	private File[] getInnerClassList(File file) {
		File parent = new File(file.getParent());
		String fileName = file.getName();
		fileName = fileName.substring(0, fileName.length() -6) + "$";
		return parent.listFiles(new InnerClassFilter(fileName));
	}
}