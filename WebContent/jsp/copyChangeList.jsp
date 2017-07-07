<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="/jsp/common.jsp" %>

<html>
<head>
<!-- <style type="text/css">
body,input,label,select,option,textarea,button,fieldset,legend {
	font-family:"微软雅黑";
	}
</style> -->
</head>
<body>
<form name="changeListForm" id="changeListForm" action="<%=root %>/app/copyChangeListServlet" enctype="multipart/form-data" method="post">
	<table width="100%">
		<tr>
			<td width="15%">工作空间路径</td>
			<td width="85%">
				<input type="text" name="fromRootPath" id="fromRootPath" size="60" value="E:\xLinker\softwareconfig\eclipse\workspace\"/>
			</td>
		</tr>
		<tr>
			<td>目标文件根路径</td>
			<td>
				<input type="text" name="toRootPath" id="toRootPath" size="60" value="C:\Users\lin.hai\Desktop\"/>
			</td>
		</tr>
		<tr>
			<td>内部类(文件或目录)</td>
			<td>
				<input type="text" name="innerClass" id="innerClass" size="60" value="PropertyType.class,\dao\,\process\,\tools\,\bo\,\util\"/>
			</td>
		</tr>
		<tr>
			<td>src目录</td>
			<td>
				<input type="text" name="srcFolder" id="srcFolder" size="60" value="src,api,config,resource,basic"/>
			</td>
		</tr>
		<tr>
			<td>扩展选项</td>
			<td colspan="2">
				<input type="checkbox" name="srcFlag" id="srcFlag" value="1" />复制到src目录 | 
				<input type="checkbox" name="classesFlag" id="classesFlag" value="1" checked/>复制到classes目录 | 
				<input type="checkbox" name="warFlag" id="warFlag" value="1" checked/>.war文件清除 | 
				<input type="checkbox" name="zipFlag" id="zipFlag" value="1" checked/>.war.zip文件生成 | 
				<input type="checkbox" name="jarFlag" id="zipFlag" value="1" />.jar文件生成
			</td>
		</tr>
		<tr>
			<td>部署应用名称</td>
			<td>
				<input type="text" name="workName" id="workName" size="60" />
			</td>
		</tr>
		<tr>
			<td>版本号</td>
			<td>
				<input type="text" name="version" id="version" size="10" />
			</td>
		</tr>
		<tr>
			<td>切换至</td>
			<td><input type="button" value="兼容maven版本" onClick="openpage()"> 
		</tr>
		<!-- <tr>
			<td>文件列表</td>
			<td>
				<input type="file" name="changeListFile" id="changeListFile" size="60" />
			</td>
		</tr> -->
		<tr>
			<td>文件内容</td>
			<td>
				<textarea name="content" id="content" rows="25" style="width:100%; border:solid 1px #000000"></textarea>
			</td>
		</tr>
	</table>
	<input type="submit" value="确  定">
</form>
</body>
</html>