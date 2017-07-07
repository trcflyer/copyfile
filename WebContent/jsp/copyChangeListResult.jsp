<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="/jsp/common.jsp" %>
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
%>
<form name="changeListForm" id="changeListForm" action="<%=root %>/jsp/app/copyChangeList.jsp" method="post">
	<input type="hidden" name="toRootPath" value="<%=request.getParameter("toRootPath") %>" />
	<table width="80%" border="1" align="center">
		<tr>
			<td>源文件</td>
			<td>目标文件</td>
			<td>结果</td>
		</tr>
		<%
		for (int i = 0; i < sucList.size(); i++) {
			String[] data = (String[]) sucList.get(i);
		%>
		<tr>
			<td><%=data[0] %></td>
			<td><%=data[1] %></td>
			<td nowrap="nowrap">成功</td>
		</tr>
		<%
		}
		%>
		<%
		for (int i = 0; i < innList.size(); i++) {
			String[] data = (String[]) innList.get(i);
		%>
		<tr>
			<td><%=data[0] %></td>
			<td><%=data[1] %></td>
			<td nowrap="nowrap">成功</td>
		</tr>
		<%
		}
		%>
		<%
		for (int i = 0; i < errList.size(); i++) {
			String[] data = (String[]) errList.get(i);
		%>
		<tr>
			<td><%=data[0] %></td>
			<td><%=data[1] %></td>
			<td nowrap="nowrap">失败</td>
		</tr>
		<%
		}
		%>
		<tr>
			<td colspan="3" align="center">总数：<%=sucList.size() + errList.size() + innList.size() %>,成功：<%=sucList.size()+innList.size() %>(主类<%=sucList.size() %>,内部类：<%=innList.size() %>),失败：<%=errList.size() %></td>
		</tr>
	</table>
	<table width="80%" align="center">
		<tr>
			<td align="center"><input type="button" value="返 回" onclick="history.back(-1)"></td>
		</tr>
	</table>
</form>
</body>
</html>