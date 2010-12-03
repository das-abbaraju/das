<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>  
<%@page import="java.lang.management.OperatingSystemMXBean"%>
<%@page import="java.lang.management.ManagementFactory"%>

<% 
OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
String status = "";
if(os.getSystemLoadAverage()>os.getAvailableProcessors())
	status = "SYSTEM LOAD = " + os.getSystemLoadAverage();
else
	status = "SYSTEM OK";
%>
<%=status%>