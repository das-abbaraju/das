<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<title><s:text name="global.Login" /></title>

<div id="server_id" title="Server: <%= java.net.InetAddress.getLocalHost().getHostName() %>"></div>

<div class="login-container">
    <s:include value="/struts/user/login/_login-form.jsp"/>
</div>