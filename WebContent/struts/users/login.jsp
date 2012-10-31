<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<title><s:text name="global.Login" /></title>
<meta name="help" content="Logging_In">

<div id="server_id">
    <div title="Server: <%= java.net.InetAddress.getLocalHost().getHostName() %>"></div>
</div>

<div id="login_wrapper">
    <s:include value="login/_login-form.jsp"/>
</div>