<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.picsauditing.util.KeepAlive" %>
<%@ page session="false" %>
<%
	boolean manualShutdown = false;

	if (manualShutdown) {
		out.print("SYSTEM NOT OK");
	} else {
		KeepAlive keepAlive = new KeepAlive(request);
		out.print(keepAlive.getKeepAliveStatus());
	}
%>