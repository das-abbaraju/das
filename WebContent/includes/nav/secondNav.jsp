<% if (pBean.isAdmin()) { %>
<%@ include file="/utilities/adminContractorNav.jsp"%>
<% } else if (pBean.isAuditor()) { %>
<%@ include file="/utilities/auditorContractorNav.jsp"%>
<% } else if (pBean.isContractor()) { %>
<%@ include file="/utilities/contractorNav.jsp"%>
<% } else if (pBean.isOperator() || pBean.isCorporate()) { %>
<%@ include file="/utilities/opContractorNav.jsp"%>
<% } %>