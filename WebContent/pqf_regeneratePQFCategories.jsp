<%@page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="com.picsauditing.PICS.SearchBean"%>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<%
String redirectPlace = request.getParameter( "nextLocation" );
if( redirectPlace == null || redirectPlace.equals("") )
{
	redirectPlace = "AuditTypeChoose.action";
}

if (!permissions.isAdmin()) throw new com.picsauditing.access.NoRightsException("isAdmin");
SearchBean sBean = new SearchBean();
sBean.doSearch(request, sBean.ACTIVE_AND_NOT, 20000, pBean, "-1");
while (sBean.isNextRecord())
	pcBean.generateDynamicCategories(sBean.aBean.id,com.picsauditing.PICS.pqf.Constants.PQF_TYPE,sBean.cBean.riskLevel);
sBean.closeSearch();
response.sendRedirect(redirectPlace);
return;
%>