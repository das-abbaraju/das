<%@page language="java" import="com.picsauditing.PICS.*, com.picsauditing.jpa.entities.*"
	errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean"
	scope="page" />
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean"
	scope="page" />
<jsp:useBean id="psBean"
	class="com.picsauditing.PICS.pqf.SubCategoryBean" scope="page" />
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<jsp:useBean id="action"
	class="com.picsauditing.actions.audits.ContractorAuditLegacy"
	scope="page" />
<%@page import="com.picsauditing.actions.audits.ContractorAuditLegacy"%>
<%@page import="java.util.Set"%>
<%
	action.setPermissions(permissions);
	action.setAuditID(request.getParameter("auditID"));
	String auditType = action.getAudit().getAuditType().getLegacyCode();
	String conID = action.getAudit().getContractorAccount().getId().toString();
	try {
		String catID = request.getParameter("catID");
		if (catID == null || catID.length() == 0)
			throw new Exception("Missing catID");
		
		boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(catID);
		boolean isServices = pcBean.SERVICES_CATEGORY_ID.equals(catID);
		cBean.setFromDB(conID);
		cBean.tryView(permissions);
		if (action.getAudit().getAuditType().getAuditTypeID() == AuditType.NCMS) {
			response.sendRedirect("pqf_viewNCMS.jsp?auditID=" + action.getAudit());
			return;
		}
		pcBean.setFromDBWithData(catID, action.getAuditID());
		pdBean.setFromDB(action.getAuditID(), conID, catID);
		psBean.setPQFSubCategoriesArray(catID);
		int catCount = 0;
%>
<html>
<head>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<title>PQF for <%=action.getAudit().getContractorAccount().getName()%></title>
</head>
<body>
<%@ include file="includes/conHeaderLegacy.jsp"%>

<h2>Category <%=pcBean.number%> - <%=pcBean.category%></h2>
<div>View:
<% if (action.canEdit()) { %><a href="pqf_edit.jsp?auditID=<%=action.getAuditID()%>&catID=<%=catID %>">Switch to Edit Mode</a><% } %>
<% if (action.canVerify()) { %>| <a href="pqf_verify.jsp?auditID=<%=action.getAuditID()%>&catID=<%=catID %>">Switch to Verify Mode</a><% } %>
</div>

<% if (action.getAudit().getAuditType().isPqf()) { %>
<form name="form1" method="post" action="pqf_view.jsp">
	<%=pcBean.getPqfCategorySelectDefaultSubmit("catID", "blueMain", catID, 
		action.getAudit().getAuditType().getAuditTypeID())%>
	<input type="hidden" name="auditID" value="<%=action.getAuditID()%>">
</form>
<% } %>

<% if (pcBean.showLicenses()) { %>
	<a href="con_stateLicenses.jsp?id=<%=conID%>&auditID=<%=action.getAuditID()%>">Check Licenses</a>
<% } %>

<table class="audit">
<%
	if ("Yes".equals(pcBean.applies)) {
		int numSections = 0;
		for (java.util.ListIterator li = psBean.subCategories
				.listIterator(); li.hasNext();) {
			numSections++;
			String subCatID = (String) li.next();
			String subCat = (String) li.next();
			pqBean.setSubListWithData("number", subCatID, conID);
			if (isOSHA) {
				%>
				<%@ include file="includes/pqf/view_OSHA.jsp"%>
				<%
			} else if (isServices) {
				%>
				<tr class="blueMain">
					<td bgcolor="#003366" colspan="3" align="center"><font
						color="#FFFFFF"><strong>Sub Category <%=catCount%>.<%=numSections%>
					- <%=subCat%></strong></font></td>
				</tr>
				<%@ include file="includes/pqf/viewServices.jsp"%>
				<%
			} else {
				%>
				<tr class="subCategory">
					<td colspan="3">Sub Category <%=pcBean.number%>.<%=numSections%> - <%=subCat%></td>
				</tr>
				<%
				int numQuestions = 0;
				while (pqBean.isNextRecord()) {
					numQuestions = numQuestions + 1;
					%>
					<% if (pqBean.getTitle().length() > 0) { %>
					<tr class="group<%=pqBean.getGroupNum()%>">
						<td class="groupTitle" colspan="3"><%=pqBean.getTitle() %></td>
					</tr>
					<% } %>
					<tr class="group<%=pqBean.getGroupNum()%>">
						<td class="right"><%=pcBean.number%>.<%=numSections%>.<%=pqBean.number%>&nbsp;&nbsp;</td>
						<td class="question"><%=pqBean.question%><%=pqBean.getLinksWithCommas()%>
						<br>&nbsp;&nbsp;&nbsp;
						<%=pqBean.getOriginalAnswerView()%>
						<%=pqBean.getVerifiedAnswerView()%>
						<%=pqBean.getCommentView()%></td>
						<td></td>
					</tr>
					<%
					if ((com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE
												.equals(auditType)
												|| com.picsauditing.PICS.pqf.Constants.DA_TYPE
														.equals(auditType) || com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE
												.equals(auditType))
												&& pqBean.hasReq()) {
						%>
						<tr <%=pqBean.getGroupBGColor()%> class=blueMain>
							<td valign="top">Req:</td>
							<td valign="top"><%=pqBean.getRequirementShow()%></td>
							<td></td>
						</tr>
						<%
					}//if
				}//while
			}//else
			pqBean.closeList();
		}//for
	}//else
%>
</table>
<%
	} finally {
		pqBean.closeList();
		pcBean.closeList();
	}
%>
</body>
</html>
