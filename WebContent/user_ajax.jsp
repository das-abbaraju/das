<%@page import="com.picsauditing.jpa.entities.ContractorAccount"%>
<%@page import="com.picsauditing.dao.ContractorAccountDAO"%>
<%@page import="com.picsauditing.util.Strings"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@ page language="java" import="com.picsauditing.dao.UserDAO"%>
<%
	try {
		String username = request.getParameter("username");
		String taxId = request.getParameter("taxId");
		if (!Strings.isEmpty(username)) {
			int userID = 0;
			try {
				userID = Integer.parseInt(request
						.getParameter("userID"));
				UserDAO ud = (UserDAO) SpringUtils.getBean("UserDAO");
				if (ud.duplicateUsername(username, userID)) {
					%><%=username%> is NOT available. Please choose a different username.<%
				} else {
					%><%=username%> is available<%
				}
			} catch (NumberFormatException e) {}
		}	
		
		if (!Strings.isEmpty(taxId)) {
			ContractorAccountDAO cAccountDAO = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
			ContractorAccount cAccount = cAccountDAO.findTaxID(taxId);
			if (cAccount != null) {
				%><%=taxId%> already exists. Please contact a company representative.<%
				} else {
				%><%
				}
		}		

	} catch (Exception e) {
		%>Unknown Error Occurred: <%=e.getMessage()%><%
	}
%>