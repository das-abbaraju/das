<%@page import="com.picsauditing.jpa.entities.ContractorAccount"%>
<%@page import="com.picsauditing.dao.ContractorAccountDAO"%>
<%@page import="com.picsauditing.util.Strings"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.picsauditing.PICS.I18nCache"%>
<%@ page language="java" import="com.picsauditing.dao.UserDAO"%>
<%
	try {
		String username = request.getParameter("username".trim());
		String taxId = request.getParameter("taxId");
		String companyName = request.getParameter("companyName");
		if (!Strings.isEmpty(username)) {
			String result = Strings.validUserName(username);
			if (!result.equals("valid")) {
				String msg = I18nCache.getInstance().getText("status.valid", Locale.getDefault());
				%> <%= msg %> <%
				return;
			}

			int userID = 0;
			try {
				userID = Integer.parseInt(request.getParameter("userID"));
				UserDAO ud = (UserDAO) SpringUtils.getBean("UserDAO");
				if (ud.duplicateUsername(username, userID)) {
					String msg = I18nCache.getInstance().getText("status.usernamenotavailable", 
							Locale.getDefault(), new Object[] {username});
					%><img src="images/notOkCheck.gif" title="Username is NOT available" /> <%=msg%><%
				} else {
					String msg = I18nCache.getInstance().getText("status.usernameavailable", 
							Locale.getDefault(), new Object[] {username});
					%><img src="images/okCheck.gif" title="Username is available" /> <%=msg%><%
				}
			} catch (NumberFormatException e) {}
		}	
		ContractorAccountDAO cAccountDAO = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
		if (!Strings.isEmpty(taxId)) {
			ContractorAccount cAccount = cAccountDAO.findTaxID(taxId, "US");
			if (cAccount != null) {
				String msg = I18nCache.getInstance().getText("status.taxidinuse", 
						Locale.getDefault(), new Object[] {taxId, "United States"});
				%><%=msg%><br/><%
				} else {
				%><%
				}
		}
		if (!Strings.isEmpty(companyName)) {
			ContractorAccount cAccount = cAccountDAO.findConID(companyName);
			if (cAccount != null) {
				String msg = I18nCache.getInstance().getText("status.companyinuse", 
						Locale.getDefault(), new Object[] {companyName});
				%><%=msg%><br/><%
				} else {
				%><%
				}
		}
	} catch (Exception e) {
		%>Unknown Error Occurred: <%=e.getMessage()%><%
	}
%>