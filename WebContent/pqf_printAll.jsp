<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope="page" />
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope="page" />
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope="page" />
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope="page" />
<%@page import="java.util.Set"%>
<%@page import="com.picsauditing.actions.audits.ContractorAuditLegacy"%>
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<%
	ContractorAuditLegacy action = new ContractorAuditLegacy();
	action.setAuditID(request.getParameter("auditID"));
	AuditType aType = action.getAudit().getAuditType();
	String auditType = action.getAudit().getAuditType().getLegacyCode();
	String conID = ((Integer) action.getAudit().getContractorAccount().getId()).toString();
	String id = conID;

	try {
		boolean isViewAll = true;
		aBean.setFromDB(conID);
		cBean.setFromDB(conID);
		cBean.tryView(permissions);
		Set<String> showCategoryIDs = null;
		if (permissions.isOperator())
			showCategoryIDs = pcBean.getCategoryForOpRiskLevel(permissions.getAccountIdString(),
					cBean.riskLevel);
%>
<html>
<head>
<title>PICS: <%=aType.getAuditName()%></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" media="screen" href="css/audit_print.css" />
</head>
<body onLoad="window.print();">
<h1><%=aType.getAuditName()%> for <%=aBean.name%></h1>
<%
	pcBean.setListWithData("number", action.getAudit().getAuditType().getLegacyCode(), conID);
		int catCount = 0;
		while (pcBean.isNextRecord(pBean, conID)) {
			if ("Yes".equals(pcBean.applies)
					&& (!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType)
							|| !(pBean.isOperator() || pBean.isCorporate())
							|| (permissions.isCorporate() && pBean.oBean.PQFCatIDsAL.contains(pcBean.catID)) || (permissions
							.isOperator() && showCategoryIDs.contains(pcBean.catID)))) {
				catCount++;
				String catID = pcBean.catID;
				psBean.setPQFSubCategoriesArray(catID);
				pdBean.setFromDB(action.getAuditID(), conID, catID);
				boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(catID);
				boolean isServices = pcBean.SERVICES_CATEGORY_ID.equals(pcBean.catID);
%>
<h2>Category <%=catCount%> - <%=pcBean.category%></h2>
<%
	int numSections = 0;
	for (java.util.ListIterator li = psBean.subCategories.listIterator(); li.hasNext();) {
		numSections++;
		String subCatID = (String) li.next();
		String subCat = (String) li.next();
		pqBean.setSubListWithData("number", subCatID, action.getAuditID());
		if (isOSHA) {
			%>
			<table><%@ include file="includes/pqf/view_OSHA.jsp"%></table>
			<%
		} else if (isServices) {
			%>
			<h3>Sub Category <%=catCount%>.<%=numSections%>	- <%=subCat%></h3>
			<table><%@ include file="includes/pqf/viewServices.jsp"%></table>
			<%
		} else {
			%>
			<h3>Sub Category <%=catCount%>.<%=numSections%>	- <%=subCat%></h3>
			<table class="audit">
			<%
			int numQuestions = 0;
			while (pqBean.isNextRecord()) {
				numQuestions++;
				%>
				<tr class="group<%=pqBean.getGroupNum()%>">
					<td class="groupTitle" colspan="3"><%=pqBean.getTitle() %></td>
				</tr>
				<tr>
					<td><%=catCount%>.<%=numSections%>.<%=pqBean.number%></td>
					<td><%=pqBean.question%> <%=pqBean.getLinks()%><br>
					<%=pqBean.getOriginalAnswerView()%> <%=pqBean.getVerifiedAnswerView()%>
					<%=pqBean.getCommentView()%></td>
					<td></td>
				</tr>
				<%
			}//while
			%>
			</table>
			<%
		}//else
		pqBean.closeList();
	}//for
	%>
</table>
<%
	}//if
		}//while
		pcBean.closeList();
	} finally {
		pqBean.closeList();
		pcBean.closeList();
	}//finally
%>
</table>
</body>
</html>
