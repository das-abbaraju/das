<%@ page language="java" errorPage="exception_handler.jsp" %>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session"/>
<%@page import="com.picsauditing.access.LoginController"%>
<%
LoginController loginCtrl = new LoginController();
loginCtrl.logout(permissions, request, response);
return;
%>