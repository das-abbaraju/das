<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
This should come back on the ajax response - <%=request.getParameter("type") %>
<%
	System.out.println("Hit this server");
	System.out.println("Remote Addr: " + request.getRemoteAddr());
	System.out.println("Local Addr: " + request.getLocalAddr());
	System.out.println(request.getRequestURI());
	System.out.println("?"+request.getQueryString());
	System.out.println();
%>