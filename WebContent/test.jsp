<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.io.File"%>
<%@page import="com.picsauditing.util.FileUtils"%><html>
<head>
<title>File Test</title>
</head>
<body>

<%
	File sourceFile = new File("/var/pics/test");
	FileUtils.moveFile(sourceFile, "/var/pics/www_files/");
	if (!sourceFile.exists()) {
		%>File does not exist<%
	} else {
		File destinationFile = new File("/var/pics/test2");
		sourceFile.renameTo(destinationFile);
	}
%>
</body>
</html>
