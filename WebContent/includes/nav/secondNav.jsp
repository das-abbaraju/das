<%	if (pBean.isContractor()) {%>
		<%@ include file="/utilities/contractorNav.jsp"%>
<%	}//if
	if (pBean.isAdmin()) {
%>  	<%@ include file="/utilities/adminContractorNav.jsp"%>
<%	}//if
	if (pBean.isAuditor()) { %>
		<%@ include file="/utilities/auditorContractorNav.jsp"%>
<%	}//if
	if (pBean.isOperator() || pBean.isCorporate()) { %>
		<%@ include file="/utilities/opContractorNav.jsp"%>
<%	}//if %>