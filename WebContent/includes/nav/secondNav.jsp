<% if (permissions.isAdmin()) { %>
<%@ include file="/utilities/adminContractorNav.jsp"%>
<% } else if (permissions.isAuditor()) { %>
<%@ include file="/utilities/auditorContractorNav.jsp"%>
<% } else if (permissions.isContractor()) { %>
<%@ include file="/utilities/contractorNav.jsp"%>
<% } else if (permissions.isOperator() || permissions.isCorporate()) { %>
<%@ include file="/utilities/opContractorNav.jsp"%>
<% } %>