<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.picsauditing.util.KeepAlive" %>
<%@ page session="false" %>
<%
	KeepAlive keepAlive = new KeepAlive(request);
	out.print(keepAlive.getKeepAliveStatus());
%>