<%--
  Created by IntelliJ IDEA.
  User: WangGenshen
  Date: 5/18/16
  Time: 19:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String path = request.getContextPath();
%>
<html>
<head>
    <title>客户列表-***医院系统</title>
    <meta charset="UTF-8"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/icon.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>

    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <script src="<%=path %>/js/site_easyui.js"></script>

    <script src="<%=path %>/js/article/article.js"></script>

    <script type="text/javascript" charset="utf-8" src="<%=path %>/ueditor/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8" src="<%=path %>/ueditor/ueditor.all.min.js"> </script>
    <script type="text/javascript" charset="utf-8" src="<%=path %>/ueditor/lang/zh-cn/zh-cn.js"></script>


    <script type="text/javascript">
        function type(value) {
            return value.name;
        }
    </script>

</head>
<body>

<div  style="width:700px; height: 400px">
    <form:form id="addForm" >
        <table style=" font-family: verdana,arial,sans-serif;
            font-size:11px;
            color:#333333;
            border-width: 1px;
            border-color: #666666;
            border-collapse: collapse;">
            <tr style=" border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #dedede;">
                <th>今天预约患者姓名</th>
            </tr>
            <c:forEach items="${revisitors}" var="node">
                <tr>
                    <td style=" border-width: 1px;
            padding: 8px;
            border-style: solid;
            border-color: #666666;
            background-color: #ffffff;"><c:out value="${node}"></c:out></td>
                </tr>
            </c:forEach>

        </table>
    </form:form>
</div>
</body>
</html>
