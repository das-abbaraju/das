<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<%
if (!permissions.isAdmin()) throw new com.picsauditing.access.NoRightsException("Admin");
try {
	com.picsauditing.PICS.pqf.QuestionTypeList statesLicensedInList = new com.picsauditing.PICS.pqf.QuestionTypeList();
	tBean.setFromDB();

	String action = request.getParameter("action");
	String action_id = request.getParameter("action_id");
	String action_type = request.getParameter("action_type");

	if (("D".equals(action)) || ("Delete".equals(action))) {
		AccountBean aBean = new AccountBean();
		aBean.deleteAccount(action_id, config.getServletContext().getRealPath("/"));
	}
	if ("Edit".equals(action)){
		response.sendRedirect("accounts_edit_contractor.jsp?id="+action_id);
		return;
	}
	
	List<BasicDynaBean> searchData = null;
	sBean.orderBy = "name";
	sBean.searchType = request.getParameter("type");
	
	sBean.doSearch(request, SearchBean.ACTIVE_AND_NOT, 100, pBean, "-1");
	
%>
<html>
<head>
<title>Accounts Manage</title>
<meta name="header_gif" content="header_manageAccounts.gif" />
  <script language="JavaScript" SRC="js/Search.js"></script>
  <script language="JavaScript" SRC="js/ImageSwap.js"></script>
</head>
<body onLoad="MM_preloadImages('images/button_search_o.gif')">
<%@ include file="includes/selectReport.jsp"%>
<form id="form1" name="form1" method="post" action="accounts_manage.jsp">
            <table border="0" align="center" cellpadding="2" cellspacing="0">
              <tr>
                <td align="left">
                <input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="8" onFocus="clearText(this)">
			    <%=sBean.getSearchIndustrySelect("industry","forms",sBean.selected_industry)%>
                <%=tBean.getTradesSelect("trade", "forms", sBean.selected_trade)%>
                <%=Inputs.inputSelect("performedBy","forms",sBean.selected_performedBy,TradesBean.PERFORMED_BY_ARRAY)%>
<!--			<td><%=sBean.getSearchZipSelect("zip","forms",sBean.selected_zip)%></td>
-->             <input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0" onClick="runSearch( 'form1')" onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
              </tr>
              <tr> 
                <td>
                  <%=sBean.getStatusSelect("status","blueMain", sBean.selected_status)%>
                  <%=sBean.getSearchGeneralSelect("generalContractorID", "blueMain", sBean.selected_generalContractorID)%>
                  <%=sBean.getAuditStatusSelect("auditStatus", "blueMain", sBean.selected_auditStatus)%> <input name="city" type="text" class="forms" value="<%=sBean.selected_city%>" size="15" onFocus="clearText(this)">
                  <%=sBean.getStateSelect("state","forms", sBean.selected_state)%>
                  <input name="zip" type="text" class="forms" value="<%=sBean.selected_zip%>" size="5" onFocus="clearText(this)">
                </td>
              </tr>
              <tr> 
                <td class=blueMain>
                  <%=Inputs.inputSelect("certsOnly","forms",sBean.selected_certsOnly,SearchBean.CERTS_SEARCH_ARRAY)%>
                  <%=Inputs.inputSelect("auditLocation", "forms", sBean.selected_auditLocation, SearchBean.AUDITLOCATION_SEARCH_ARRAY)%>
                  <%=Inputs.inputSelect("visible", "forms", sBean.selected_visible, SearchBean.VISIBLE_SEARCH_ARRAY)%>
                  <%=statesLicensedInList.getQuestionListQIDSelect("License","stateLicensedIn","forms", sBean.selected_stateLicensedIn,SearchBean.DEFAULT_LICENSED_IN)%>
                  <input name="taxID" type="text" class="forms" value="<%=sBean.selected_taxID%>" size="9" onFocus="clearText(this)"><span class=redMain>*must be 9 digits</span>
                </td>
              </tr>
            </table>
            <input type="hidden" name="showPage" value="1"/>
            <input type="hidden" name="startsWith" value="<%=sBean.selected_startsWith == null ? "" : sBean.selected_startsWith %>"/>
            <input type="hidden" name="orderBy"  value="<%=sBean.orderBy == null ? "" : sBean.orderBy %>"/>
</form>
<center><%=sBean.getStartsWithLinksWithDynamicForm()%></center>
<table width="657" height="40" border="0" cellpadding="0" cellspacing="0">
  <tr>
	<td align="right"><%=sBean.getLinksWithDynamicForm()%></td>
  </tr>
</table>
<table width="757" border="0" cellpadding="1" cellspacing="1">
<tr bgcolor="#003366" class="whiteTitle">
	<td height="25" colspan="2" align="center" bgcolor="#993300"></td>
	<td colspan="2">Contractor</td>
	<td>Industry</td>
	<td>Trade</td>
	<td align="center" bgcolor="#336699">PQF</td>
	<td align="center" bgcolor="#993300">Desktop</td>
	<td align="center" bgcolor="#993300">D&A</td>
	<td align="center" bgcolor="#6699CC">Old Office</td>
	<td align="center" bgcolor="#6699CC">New Office</td>
	<td align="center" bgcolor="#993300">Insur</td>
</tr>
<%	while (sBean.isNextRecord()) {
		String thisClass = sBean.cBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>
            <tr <%=sBean.getBGColor()%> class="<%=thisClass%>"> 
			  <form name="form2" method="post" action="accounts_manage.jsp">
				<td align="center" bgcolor="#FFFFFF"> 
				  <input name="action" type="submit" class="buttons" value="D" onClick="return confirm('Are you sure you want to delete this account?');">
				</td>
				<td bgcolor="#FFFFFF" align="center"> 
				  <input name="action" type="submit" class="buttons" value="Edit">
				</td>
				<td align="right"><%=sBean.count-1%></td>
                <td>
				  <%=sBean.getActiveStar()%>
				  <a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" class="<%=thisClass%>">
				  <%=sBean.aBean.name%></a>
			    </td>
                <td><%=sBean.aBean.industry%></td>
                <td><%=sBean.getSearchTrade()%></td>
                <td align="center"><%=sBean.getPercentCompleteLink(com.picsauditing.PICS.pqf.Constants.PQF_TYPE)%></td>
                <td align="center"><%=sBean.getPercentCompleteLink(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE)%></td>
                <td align="center"><%=sBean.getPercentCompleteLink(com.picsauditing.PICS.pqf.Constants.DA_TYPE)%></td>
                <td align="center"><%=sBean.getListLink(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE)%></td>
                <td align="center"><%=sBean.getPercentCompleteLink(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE)%></td>
                <td align="center"><%=sBean.getCertsAdminLink()%></td>
				<input name="action_type" type="hidden" value="Contractor">
				<input name="action_id" type="hidden" value="<%=sBean.aBean.id%>">
              </form>
              </tr>
<%
	}// while
%>
</table>
<br><center><%=sBean.getLinksWithDynamicForm()%></center><br>			  
<%
sBean.closeSearch();
%>
<br><center><%@ include file="utilities/contractor_key.jsp"%></center>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>