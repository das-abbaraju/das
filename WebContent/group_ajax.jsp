
<%@page import="com.picsauditing.util.Strings"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="java.util.Locale"%>
<%@page import="com.picsauditing.service.i18n.TranslationService"%>
<%@page import="com.picsauditing.service.i18n.TranslationServiceFactory"%>
<%@page import="com.picsauditing.validator.InputValidator"%>
<%@ page language="java" import="com.picsauditing.dao.UserDAO"%>
<!-- FIXME Clean up this validation, possibly moving it into a controller solely for AJAX validation -->
<%
	try {
		String groupName = request.getParameter("groupName".trim());
		TranslationService translationService = getTranslationService.getTranslationService();
		Locale locale = Locale.getDefault();
		
		if (!Strings.isEmpty(groupName)) {
			String message = null;

            InputValidator inputValidator = SpringUtils.getBean("InputValidator");
            if (inputValidator != null) {
                String errorMessageKey = inputValidator.validateUsername(groupName);
                if (Strings.isNotEmpty(errorMessageKey)) {
                    message = translationService.getText(errorMessageKey, locale);
                }
            }

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
					String msg = translationService.getText("Status.GroupNameNotAvailable", locale, new Object[] {groupName});
					%><img src="images/notOkCheck.gif" title="Group Name is NOT available" /> <%=msg%><%
							
				} else {
					String msg = translationService.getText("Status.GroupNameAvailable", locale, new Object[] {groupName});
					%><img src="images/okCheck.gif" title="Group Name is available" /> <%=msg%><%
							
				}
			} catch (NumberFormatException e) {}
		}	
		
	} catch (Exception e) {
		%>Unknown Error Occurred: <%=e.getMessage()%><%
	}
%>