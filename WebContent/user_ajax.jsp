<%@page import="com.picsauditing.jpa.entities.ContractorAccount"%>
<%@page import="com.picsauditing.dao.ContractorAccountDAO"%>
<%@page import="com.picsauditing.util.Strings"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.picsauditing.PICS.I18nCache"%>
<%@page import="com.picsauditing.PICS.InputValidator"%>
<%@ page language="java" import="com.picsauditing.dao.UserDAO"%>
<!-- FIXME Clean up this validation, possibly moving it into a controller solely for AJAX validation -->
<%
	try {
		String username = request.getParameter("username".trim());
		String taxId = request.getParameter("taxId");
		String companyName = request.getParameter("companyName");
		I18nCache cache = I18nCache.getInstance();
		Locale locale = Locale.getDefault();

		if (!Strings.isEmpty(username)) {
			String message = null;

            InputValidator inputValidator = SpringUtils.getBean("InputValidator");
            if (inputValidator != null) {
                String errorMessageKey = inputValidator.validateUsername(username);
                if (Strings.isNotEmpty(errorMessageKey)) {
                    message = cache.getText(errorMessageKey, locale);
                }
            }
			
			if (!Strings.isEmpty(message)) {
				%> <%= message %> <%
				return;
			}

			int userID = 0;
			try {
				userID = Integer.parseInt(request.getParameter("userID"));
				UserDAO ud = (UserDAO) SpringUtils.getBean("UserDAO");
				if (ud.duplicateUsername(username, userID)) {
					String msg = cache.getText("Status.UsernameNotAvailable", locale, new Object[] {username});
					%><img src="images/notOkCheck.gif" title="Username is NOT available" /> <%=msg%><%
				} else {
					String msg = cache.getText("Status.UsernameAvailable", 
							locale, new Object[] {username});
					%><img src="images/okCheck.gif" title="Username is available" /> <%=msg%><%
				}
			} catch (NumberFormatException e) {}
		}

		ContractorAccountDAO cAccountDAO = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
		if (!Strings.isEmpty(taxId)) {
			ContractorAccount cAccount = cAccountDAO.findTaxID(taxId, "US");
			if (cAccount != null) {
				String msg = cache.getText("Status.TaxIdInUse", 
						locale, new Object[] {taxId, "United States"});
				%><%=msg%><br/><%
				} else {
				%><%
				}
		}
		if (!Strings.isEmpty(companyName)) {
			ContractorAccount cAccount = cAccountDAO.findConID(companyName);
			if (cAccount != null) {
				String msg = cache.getText("Status.CompanyInUse", 
						locale, new Object[] {companyName});
				%><%=msg%><br/><%
				} else {
				%><%
				}
		}
	} catch (Exception e) {
		%>Unknown Error Occurred: <%=e.getMessage()%><%
	}
%>