<%@page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.lang.management.OperatingSystemMXBean"%>
<%@page import="java.lang.management.ManagementFactory"%>
<%@page session="false" %>
<% 
String[] loadFactors = request.getParameterValues("load_factor");
float loadFactor = 1f;
try {
	if (loadFactors != null && loadFactors.length > 0) {
		loadFactor = Float.parseFloat(loadFactors[0].toString());
	}
} catch (Exception e) {
}
OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
String status = "";
if(os.getSystemLoadAverage() > os.getAvailableProcessors() * loadFactor)
	status = "SYSTEM LOAD = " + os.getSystemLoadAverage();
else
	status = "SYSTEM OK";

%>
<%=status %>