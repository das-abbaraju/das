<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<jsp:useBean id="action"
	class="com.picsauditing.actions.audits.ContractorAuditLegacy"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<%
	action.setAuditID(request.getParameter("auditID"));
	String conID = action.getAudit().getContractorAccount().getId().toString();
	cBean.setFromDB(conID);
	String catID = null;
%>
<%@ include file="/includes/nav/pqfHeader.jsp"%>