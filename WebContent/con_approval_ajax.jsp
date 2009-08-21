<%@page import="com.picsauditing.jpa.entities.ContractorOperator"%>
<%@page import="com.picsauditing.dao.ContractorOperatorDAO"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
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
	
	ContractorOperatorDAO dao = (ContractorOperatorDAO)SpringUtils.getBean("ContractorOperatorDAO");
	ContractorOperator co = dao.find(Integer.parseInt(request.getParameter("conID")), permissions.getAccountId());
	co.setWorkStatus(request.getParameter("workStatus"));
	dao.save(co);
	
	%>Saved<%
} catch (Exception e) {
	%>Failed to save<%
}
%>