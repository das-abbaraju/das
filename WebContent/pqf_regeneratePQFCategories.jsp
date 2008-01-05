<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/admin_secure.jsp" %>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<%	com.picsauditing.PICS.SearchBean sBean = new com.picsauditing.PICS.SearchBean();
	sBean.doSearch(request, sBean.ACTIVE_AND_NOT, 20000, pBean, sBean.ADMIN_ID);
	while (sBean.isNextRecord())
		pcBean.generateDynamicCategories(sBean.aBean.id,com.picsauditing.PICS.pqf.Constants.PQF_TYPE,sBean.cBean.riskLevel);
	sBean.closeSearch();	
	if (pBean.isAdmin()){
		response.sendRedirect("audit_selectType.jsp");
		return;
	}//if
%>
