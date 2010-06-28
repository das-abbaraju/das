<%@page import="com.picsauditing.jpa.entities.ContractorAccount"%>
<%@page import="com.picsauditing.dao.ContractorAccountDAO"%>
<%@page import="com.picsauditing.util.Strings"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@ page language="java" import="com.picsauditing.dao.UserDAO"%>
<%
	try {
		String username = request.getParameter("username".trim());
		String taxId = request.getParameter("taxId");
		String companyName = request.getParameter("companyName");
		if (!Strings.isEmpty(username)) {
			String result = Strings.validUserName(username);
			if (!result.equals("valid")) {
				%> <%= result %> <%
				return;
			}

			int userID = 0;
			try {
				userID = Integer.parseInt(request.getParameter("userID"));
				UserDAO ud = (UserDAO) SpringUtils.getBean("UserDAO");
				if (ud.duplicateUsername(username, userID)) {
					%><img src="images/notOkCheck.gif" title="Username is NOT available" /> <%=username%> is NOT available. Please choose a different Username.<%
				} else {
					%><img src="images/okCheck.gif" title="Username is available" /> <%=username%> is available<%
				}
			} catch (NumberFormatException e) {}
		}	
		ContractorAccountDAO cAccountDAO = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
		if (!Strings.isEmpty(taxId)) {
			ContractorAccount cAccount = cAccountDAO.findTaxID(taxId, "US");
			if (cAccount != null) {
				%><%=taxId%> already exists. Please contact a PICS representative at 949-387-1940 ext 1.<br/><%
				} else {
				%><%
				}
		}
		if (!Strings.isEmpty(companyName)) {
			ContractorAccount cAccount = cAccountDAO.findConID(companyName);
			if (cAccount != null) {
				%><%=companyName%> already exists. Please contact a PICS representative at 949-387-1940 ext 1.<br/><%
				} else {
				%><%
				}
		}
	} catch (Exception e) {
		%>Unknown Error Occurred: <%=e.getMessage()%><%
	}
%>