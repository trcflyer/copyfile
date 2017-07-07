<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ include file="/jsp/common.jsp"%>

<html>
<head>
<!-- <style type="text/css">
body,input,label,select,option,textarea,button,fieldset,legend {
    font-family:"微软雅黑";
    }
</style> -->
<script type="text/javascript">
    function bindMode(mode) {
        if (mode == 0) {
            document.getElementById("classPaths").value = 'build/classes';
            document.getElementById("srcFolders").value = 'src,api,config,resource,basic,resources,spring,test';
            document.getElementById("webPaths").value = 'WebContent,WebRoot,webapp,resources';
        } else if (mode == 1) {
            document.getElementById("classPaths").value = 'build/classes';
            document.getElementById("srcFolders").value = 'src/main/java,src/main/resources';
            document.getElementById("webPaths").value = 'WebContent';
        } else if (mode == 2) {
            document.getElementById("classPaths").value = 'target/classes';
            document.getElementById("srcFolders").value = 'src/main/java,src/main/resources';
            document.getElementById("webPaths").value = 'src/main/webapp';
        } else {
            alert("不支持的模式");
        }
    }
</script>
<!-- <script type="text/javascript">
     function openpage() {
        window.location.replace("<%=root %>/jsp/copyChangeList.jsp");
    }
</script> -->
</head>
<body>
    <form name="changeListForm" id="changeListForm" action="<%=root%>/app/copyChangeListExServlet" enctype="multipart/form-data" method="post">
        <table>
            <tr>
                <td width="15%">工作空间路径</td>
                <td width="85%"><input type="text" name="projectPath" id="projectPath" size="60" value="D:/MyWorkSpace/MyEclipseWorkspace/MyChinapay/" /></td>
            </tr>
            <tr>
                <td>目标文件根路径</td>
                <td><input type="text" name="toRootPath" id="toRootPath" size="60" value="D:/workspace/ftp/builder/"/></td>
            </tr>
            <tr>
                <td>项目路径</td>
                <td><input type="text" name="projectName" id="projectName" size="60" value="" />(文件列表或内容以项目路径开始无需填写，否则需要填写)</td>
            </tr>
            <tr>
                <td>打包名称</td>
                <td><input type="text" name="warName" id="warName" size="60" value="" />(默认和项目路径相同)</td>
            </tr>
            <tr>
                <td>版本号</td>
                <td><input type="text" name="version" id="version" size="10" value="" /></td>
            </tr>
            <tr>
                <td>扩展选项</td>
                <td>
                    <input type="checkbox" name="deleteFlag" id="deleteFlag" value="1" checked />.war文件清除 | 
                    <input type="checkbox" name="zipFlag" id="zipFlag" value="1" checked />.war.zip文件生成
                    <input type="checkbox" name="jarFlag" id="jarFlag" value="1"  />.jar文件生成
                </td>
            </tr>
            <tr>
                <td>打包模式</td>
                <td><select id="mode" name="mode" onchange="bindMode(this.value)">
                        <option value="0">eclipse</option>
                        <option value="1">兼容maven</option>
                        <option value="2">标准maven(jar需要手工复制)</option>
                </select></td>
            </tr>
            <tr>
                <td>类路径(以,分割)</td>
                <td><input type="text" name="classPaths" id="classPaths" size="60" value="build/classes" /></td>
            </tr>
            <tr>
                <td>src路径(以,分割)</td>
                <td><input type="text" name="srcFolders" id="srcFolders" size="60" value="src,api,config,resource,basic,resources,spring,test" /></td>
            </tr>
            <tr>
                <td>web路径(以,分割)</td>
                <td><input type="text" name="webPaths" id="webPaths" size="60" value="WebContent,WebRoot,webapp,resources" /></td>
            </tr>
            <!-- <tr>
                <td>文件列表(文件内容优先)</td>
                <td><input type="file" name="changeListFile" id="changeListFile" size="60" /></td>
            </tr> -->
            <tr>
                <td>文件内容</td>
                <td><textarea rows="15" cols="70" name="content" id="content" style="width:100%; border:solid 1px #000000"></textarea>
                </td>
            </tr>
        </table>
        <input type="submit" value="确  定">
    </form>
</body>
</html>