<%@ page language="java" import="com.picsauditing.access.User, com.picsauditing.PICS.AccountBean"%>
<%
try {
	String username = request.getParameter("username");
	
	if (username != null && username.length() > 0) {
		int userID = 0;
		try { userID = Integer.parseInt(request.getParameter("userID")); }
		catch (NumberFormatException e) {}
			
		// Check the users table
		User uBean = new User();
		Integer id = uBean.findID(username);
		if (id == 0 || id == userID) {
			// Check the accounts table
			AccountBean aBean = new AccountBean();
			id = aBean.findID(username);
			if (id == 0 || id == userID) {
				%><%=username%> is available<%
				return;
			}
		}
		%><%=username%> is NOT available. Please choose a different username.<%
		return;
	}
} catch (Exception e) {
	%>Unknown Error Occurred: <%=e.getMessage() %><%
	return;
}
%>