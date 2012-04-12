
<%@page import="com.picsauditing.util.Strings"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.picsauditing.PICS.I18nCache"%>
<%@ page language="java" import="com.picsauditing.dao.UserDAO"%>
<%
	try {
		String groupName = request.getParameter("groupName".trim());
		I18nCache cache = I18nCache.getInstance();
		Locale locale = Locale.getDefault();
		
		if (!Strings.isEmpty(groupName)) {
			String message = null;
			
			if (Strings.isEmpty(groupName))
				message = cache.getText("User.groupname.error.Empty", locale);
			else if (groupName.length() < 3)
				message = cache.getText("User.groupname.error.Short", locale);
			else if (groupName.length() > 100)
				message = cache.getText("User.groupname.error.Long", locale);
			else if (!groupName.matches("^[a-zA-Z0-9+._@-]{3,50}$"))
				message = cache.getText("User.groupname.error.Special", locale);
			
			if (!Strings.isEmpty(message)) {
				%> <%= message %> <%
				return;
			}

			int userID = 0;
			String accountID;
			try {				
				userID = Integer.parseInt(request.getParameter("userID"));						
				
				UserDAO ud = (UserDAO) SpringUtils.getBean("UserDAO");
				accountID = request.getParameter("accountId");
				
				if (ud.duplicateUsername("GROUP"+accountID+groupName, userID)) {
					String msg = cache.getText("Status.GroupNameNotAvailable", locale, new Object[] {groupName});
					%><img src="images/notOkCheck.gif" title="Group Name is NOT available" /> <%=msg%><%
							
				} else {
					String msg = cache.getText("Status.GroupNameAvailable", locale, new Object[] {groupName});
					%><img src="images/okCheck.gif" title="Group Name is available" /> <%=msg%><%
							
				}
			} catch (NumberFormatException e) {}
		}	
		
	} catch (Exception e) {
		%>Unknown Error Occurred: <%=e.getMessage()%><%
	}
%>