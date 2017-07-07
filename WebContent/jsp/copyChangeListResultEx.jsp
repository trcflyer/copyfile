<%@page import="com.app.service.FileBuildContext"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ include file="/jsp/common.jsp"%>
<%@page import="java.util.List"%>

<html>
<head>
<!-- <style type="text/css">
body,input,label,select,option,textarea,button,fieldset,legend {
	font-family:"微软雅黑";
	}
</style> -->
</head>
<body>

	<%
	    List sucList = (List) request.getAttribute("sucList");
	    List errList = (List) request.getAttribute("errList");
	    List innList = (List) request.getAttribute("innList");

	    String msg = (String) request.getAttribute("msg");

	    if (msg != null && msg.trim().length() > 0) {
	%>
	<div><%=msg%></div>
	<%
	    } else {
	%>
	<form name="changeListForm" id="changeListForm"
		action="<%=root%>/jsp/app/copyChangeList.jsp" method="post">
		<input type="hidden" name="toRootPath"
			value="<%=request.getParameter("toRootPath")%>" />
		<table width="80%" border="1" align="center">
			<tr>
				<td>输入文件路径</td>
				<td>复制原文件路径</td>
				<td>复制目的文件路径</td>
				<td>结果</td>
			</tr>
			<%
			    for (int i = 0; i < sucList.size(); i++) {
			            FileBuildContext data = (FileBuildContext) sucList.get(i);
			%>
			<tr>
				<td><%=data.getInputPath()%>&nbsp;</td>
				<td><%=data.getSrcPath()%>&nbsp;</td>
				<td><%=data.getDstPath()%>&nbsp;
				<%
					for(String str:data.getInnerClassPathList()){
					    out.print("<br/>");
					    out.print(str);
					}
				%>
				</td>
				<td nowrap="nowrap">成功</td>
			</tr>
			<%
			    }
			%>
			<%
			    for (int i = 0; i < innList.size(); i++) {
			            FileBuildContext data = (FileBuildContext) innList.get(i);
			%>
			<tr>
				<td><%=data.getInputPath()%>&nbsp;</td>
				<td><%=data.getSrcPath()%>&nbsp;</td>
				<td><%=data.getDstPath()%>&nbsp;</td>
				<td nowrap="nowrap">忽略</td>
			</tr>
			<%
			    }
			%>
			<%
			    for (int i = 0; i < errList.size(); i++) {
			            FileBuildContext data = (FileBuildContext) errList.get(i);
			%>
			<tr>
				<td><%=data.getInputPath()%>&nbsp;</td>
				<td><%=data.getSrcPath()%>&nbsp;</td>
				<td><%=data.getDstPath()%>&nbsp;</td>
				<td nowrap="nowrap">失败</td>
			</tr>
			<%
			    }
			%>
			<tr>
				<td colspan="4" align="center">总数:<%=sucList.size() + errList.size() + innList.size()%> | 成功:<%=sucList.size()%> | 忽略:<%=innList.size()%> | 失败:<%=errList.size()%></td>
			</tr>
		</table>
	</form>
	<%
	    }
	%>
	<table width="80%" align="center">
		<tr>
			<td align="center"><input type="button" value="返 回"
				onclick="history.back(-1)"></td>
		</tr>
	</table>

</body>
</html>