<%@page language="java" errorPage="exception_handler.jsp"%>
<%
application.removeAttribute(request.getParameter("auditTypes"));
application.removeAttribute(request.getParameter("auditTypesById"));
application.removeAttribute(request.getParameter("auditTypesByName"));
%>
