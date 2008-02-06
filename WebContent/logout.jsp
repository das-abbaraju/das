<%@ page language="java" errorPage="exception_handler.jsp" %>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session"/>
<% 
	if (permissions.getAdminID() > 0) {
		response.sendRedirect("login.jsp?switchUser=logout");
	}
	permissions.clear();
	session.invalidate();
	
	String temp = request.getParameter("msg");
	String query = "";
	if (null != temp && temp.length()>0)
		query = "?msg="+temp;
	response.sendRedirect("login.jsp"+query);
%>