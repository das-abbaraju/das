<jsp:useBean id="permissions"
	class="com.picsauditing.access.Permissions" scope="session" />
<%
	if (permissions.isLoggedIn()) {
		if (permissions.isContractor()) {
			if (!permissions.getAccountStatus().isActive()) {
				response.sendRedirect("RegistrationMakePayment.action");
			} else {
				response.sendRedirect("ContractorView.action");
			}
		} else {
			response.sendRedirect("Home.action");
		}
		// Redirecting to payment page, which will redirect to proper step if not ready
	} else {
		response.sendRedirect("Login.action");
	}
%>