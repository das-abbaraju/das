<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.picsauditing.util.KeepAlive" %>
<%@ page session="false" %>
<%
	// If this file ever gets refactored, please update the picsd.cloudServers
	// script to match the changes as well
	
	// Update this boolean to trigger SYSTEM NOT OK
	boolean manualShutdown = false;

	if (manualShutdown) {
		out.print("SYSTEM NOT OK");
	} else {
		KeepAlive keepAlive = new KeepAlive(request);
		out.print(keepAlive.getKeepAliveStatus());
	}
%>