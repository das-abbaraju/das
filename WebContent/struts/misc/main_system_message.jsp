<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.picsauditing.PICS.MainPage" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
	MainPage mainPage = new MainPage(request, session);

	if (mainPage.isDisplaySystemMessage()) {
%>
<div id="systemMessage">
	<s:text name="SYSTEM.message" />
</div>
<%	} %>