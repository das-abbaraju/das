<% if (permissions.isContractor()) { %>
	<%@ include file="/utilities/contractorNav.jsp"%>
<% } else { %>
	<%@ include file="/utilities/adminContractorNav.jsp"%>
<% } %>
