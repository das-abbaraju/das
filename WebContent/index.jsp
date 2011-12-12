<jsp:useBean id="permissions"
	class="com.picsauditing.access.Permissions" scope="session" />
<%
	if (permissions.isLoggedIn())
		if (permissions.isContractor() && !permissions.getAccountStatus().isActive())
			// Redirecting to payment page, which will redirect to proper step if not ready
			response.sendRedirect("RegistrationMakePayment.action");
		else
			response.sendRedirect("Home.action");
	else
		response.sendRedirect("Login.action");
%>