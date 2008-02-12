<%	if (pBean.isContractor()) { %>
<%@ include file="/utilities/contractorNav.jsp"%>
<%	}
	if (pBean.isAdmin()) { %>
<%@ include file="/utilities/adminContractorNav.jsp"%>
<%	}
	if (pBean.isAuditor()) { %>
<%@ include file="/utilities/auditorContractorNav.jsp"%>
<%	}
	if (pBean.isOperator() || pBean.isCorporate()) { %>
<%@ include file="/utilities/opContractorNav.jsp"%>
<%	} %>
