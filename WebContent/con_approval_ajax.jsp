<%@page language="java" %>
<%@include file="includes/main.jsp" %>
<%
try {
	if (!permissions.isLoggedIn()) {
		%>Not Logged in Anymore<%
		return;
	}
	if (!permissions.hasPermission(OpPerms.ContractorApproval, OpType.Edit)) {
		%>Edit ContractorApproval Permission required<%
		return;
	}
	GeneralContractor gcBean = new GeneralContractor();
	gcBean.setConID(Integer.parseInt(request.getParameter("conID")));
	gcBean.setOpID(permissions.getAccountId());
	gcBean.setWorkStatus(request.getParameter("workStatus"));
	gcBean.save();
	%>Saved<%
} catch (Exception e) {
	%>Failed to save<%
}
%>