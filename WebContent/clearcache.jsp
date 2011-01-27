<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>  
<%@page import="com.picsauditing.dao.AppPropertyDAO"%>
<%@page import="com.picsauditing.jpa.entities.AppProperty"%>
<%@page import="com.picsauditing.util.SpringUtils"%>

<% 
AppPropertyDAO appPropertyDAO = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");
AppProperty appProp = appPropertyDAO.find("clear_cache");
String clear = "";
if("true".equals(appProp.getValue()))
	clear = "CLEAR";
%>
<%=clear%>