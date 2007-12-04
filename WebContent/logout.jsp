<%@ page language="java" errorPage="exception_handler.jsp" %>
<% 
	session.removeAttribute("userid");
	session.removeAttribute("username");
	session.removeAttribute("usertype");
	session.removeAttribute("accessID");
	session.removeAttribute("canSeeSet");
	session.removeAttribute("hasCertSet");
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