<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="java.util.Enumeration"%><html>
<head>
<title>Ajax Test</title>
</head>
<body>
<p>LocalPort: <%=request.getLocalPort()%></p>
<p>RemotePort: <%=request.getRemotePort()%></p>
<p>ServerPort: <%=request.getServerPort()%></p>
<p>AuthType: <%=request.getAuthType()%></p>
<p>Protocol: <%=request.getProtocol()%></p>
<p>RequestURI: <%=request.getRequestURI()%></p>
<p>RequestURL: <%=request.getRequestURL()%></p>
<p>Secure: <%=request.isSecure()%></p>

<table>
<%
	String headername = "";
	for(Enumeration e = request.getHeaderNames(); e.hasMoreElements();){
		headername = (String)e.nextElement();
%>
	<tr>
		<td><b><%= headername %></b></td>
		<td><b><%= request.getHeader(headername) %></b></td>
	</tr>
<%}%>

</body>
</html>