<%@page import="com.picsauditing.util.SpringUtils"%>
<%@ page language="java" import="com.picsauditing.dao.UserDAO"%>
<%
	try {
		String username = request.getParameter("username");

		if (username != null && username.length() > 0) {
			int userID = 0;
			try {
				userID = Integer.parseInt(request
						.getParameter("userID"));
			} catch (NumberFormatException e) {}
		
			UserDAO ud = (UserDAO) SpringUtils.getBean("UserDAO");
			if (ud.duplicateUsername(username, userID)) {
				%><%=username%> is NOT available. Please choose a different username.<%
			} else {
				%><%=username%> is available<%
			}
		}

	} catch (Exception e) {
		%>Unknown Error Occurred: <%=e.getMessage()%><%
	}
%>