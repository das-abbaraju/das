<%@ page language="java" errorPage="exception_handler.jsp" %>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session"/>
<% 
	if (permissions.getAdminID() > 0) {
		response.sendRedirect("login.jsp?switchUser=logout");
	}
	session.invalidate();
	
//	Cookie temp = new Cookie("from","contractor_detail.jsp?id=249");
//	temp.setMaxAge(10);
//	response.addCookie(temp);
	String temp = request.getParameter("msg");
	String query = "";
	if (null != temp && temp.length()>0)
		query = "?msg="+temp;
	response.sendRedirect("login.jsp"+query);
%>