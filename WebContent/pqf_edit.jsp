<%@page language="java"
	import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*"
	errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope="page" />
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope="page" />
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope="page" />
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope="page" />
<jsp:useBean id="action" class="com.picsauditing.actions.audits.ContractorAuditLegacy" scope="page" />
<%
	action.setPermissions(permissions);
	action.setAuditID(request.getParameter("auditID"));
	String auditType = action.getAudit().getAuditType().getLegacyCode();
	String conID = action.getAudit().getContractorAccount().getId().toString();
	String id = conID;
	
	try {
		String catID = request.getParameter("catID");
		String actionString = request.getParameter("action");
		boolean isOSHA = CategoryBean.OSHA_CATEGORY_ID.equals(catID);
		boolean isFileUpload = pdBean.isFileUpload(catID);
		if (isFileUpload) {
			response.sendRedirect("pqf_uploadFile.jsp?auditID=" + action.getAuditID() + "&catID=" + catID);
			return;
		}
		if (isOSHA) {
			OSHABean oBean = new OSHABean();
			oBean.setListFromDB(conID);
			if (oBean.isInDB)
				response.sendRedirect("pqf_viewOSHA.jsp?auditID=" + conID + "&catID=" + catID);
			else
				response.sendRedirect("pqf_OSHA.jsp?action=Edit&oID=New&auditID=" + action.getAudit().getId() + "&catID=" + catID);
			return;
		}
		int numQuestions = 0;
		int numSections = 0;
		int requiredCount = 0;
		String errorMsg = "";
		aBean.setFromDB(conID);
		cBean.setFromDB(conID);
		cBean.tryView(permissions);
		if ("Save".equals(actionString)) {
			pdBean.savePQF(request, action.getAudit(), permissions);
			//TODO Make a contractor edit a pqf. 
			//cBean.setPercentComplete(auditType, action.getPercentComplete());
			//cBean.setPercentVerified(auditType, pdBean.getPercentVerified(conID, auditType));
			cBean.writeToDB();
			pdBean.updatePercentageCompleted(action.getAuditID(), catID);
			response.sendRedirect("pqf_edit.jsp?auditID=" + action.getAuditID());
			return;
		}//if
		pcBean.setFromDBWithData(catID, action.getAuditID());
		psBean.setPQFSubCategoriesArray(catID);
		pdBean.setFromDB(action.getAuditID(), conID, catID);
		pqBean.highlightRequired = pdBean.alreadySavedCat;
%>
<html>
<head>
<title>Edit PQF</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<script language="JavaScript" src="js/TimeOutWarning.js"></script>
<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" SRC="js/validateForms.js"></SCRIPT>
</head>
<body>
<%@ include file="includes/conHeaderLegacy.jsp"%>

<h2>Category <%=pcBean.number%> - <%=pcBean.category%></h2>
<div>Edit:
<a href="pqf_view.jsp?auditID=<%=action.getAuditID()%>&catID=<%=catID %>">Switch to View Mode</a>
<% if (action.canVerify()) { %>| <a href="pqf_verify.jsp?auditID=<%=action.getAuditID()%>&catID=<%=catID %>">Switch to Verify Mode</a><% } %>
</div>

<div class="required">
<%=errorMsg%>
<% if (pqBean.highlightRequired) { %>
	* Blue questions are required
<% } %>
</div>

<form name="formEdit" method="post" action="pqf_edit.jsp">
<div>
<input name="action" type="submit" class="auditSave" value="Save">
Click to save your work. You may still edit your information later.
<% if (permissions.isPicsEmployee()) { %>
<br /><input type="checkbox" name="catDoesNotApply"
	value="Yes" <%=Inputs.getChecked("Yes",pdBean.catDoesNotApply)%>>
Check here if this entire category does not apply
<% } %>
</div>

<table class="audit">
	<%
	numSections = 0;
	for (java.util.ListIterator li = psBean.subCategories.listIterator(); li.hasNext();) {
		numSections++;
		String subCatID = (String) li.next();
		String subCat = (String) li.next();
		pqBean.setSubListWithData("number", subCatID, conID);
	%>
	<tr class="subCategory">
		<td colspan="3">Sub Category <%=pcBean.number%>.<%=numSections%> - <%=subCat%></td>
	</tr>
	<%
		numQuestions = 0;
		while (pqBean.isNextRecord()) {
			if (pqBean.calcIsRequired(pdBean))
				requiredCount++;
			numQuestions++;
			%>
			<% if (pqBean.getTitle().length() > 0) { %>
			<tr class="group<%=pqBean.getGroupNum()%>">
				<td class="groupTitle" colspan="3"><%=pqBean.getTitle() %></td>
			</tr>
			<% } %>
			<tr class="group<%=pqBean.getGroupNum()%>">
				<td class="right"><%=pcBean.number%>.<%=numSections%>.<%=pqBean.number%>&nbsp;&nbsp;</td>
				<td <%=pqBean.getClassAttribute(pdBean)%>><%=pqBean.question%>
					<%=pqBean.getLinksWithCommas()%></td>
				<td class="answer"><%=pqBean.getInputElement()%> <input
					type="hidden" name="pqfQuestionID_<%=pqBean.questionID%>"
					value="<%=pqBean.questionID%>"> <input type="hidden"
					name="isRequired_<%=pqBean.questionID%>"
					value="<%=pqBean.isRequired%>"> <input type="hidden"
					name="oldAnswer_<%=pqBean.questionID%>"
					value="<%=pqBean.data.answer%>"> <input type="hidden"
					name="oldComment_<%=pqBean.questionID%>"
					value="<%=pqBean.data.comment%>"> <input type="hidden"
					name="oldDateVerified_ <%=pqBean.questionID%>"
					value="<%=pqBean.data.dateVerified%>"> <input type=hidden
					name="oldAuditorID_<%=pqBean.questionID%>"
					value="<%=pqBean.data.auditorID%>"> <input type=hidden
					name="wasChanged_<%=pqBean.questionID%>"
					value="<%=pqBean.data.wasChanged%>"> <%
				if ("Depends".equals(pqBean.isRequired)) {
					%>
				<input type="hidden" name="dependsOnQID_<%=pqBean.questionID%>"
					value="<%=pqBean.dependsOnQID%>"> <input type="hidden"
					name="dependsOnAnswer_<%=pqBean.questionID%>"
					value="<%=pqBean.dependsOnAnswer%>"> <%
				} %>
				<input
					type="hidden" name="pqfQuestionType_<%=pqBean.questionID%>"
					value="<%=pqBean.questionType%>"></td>
			</tr>
			<%
		}//while questions
		pqBean.closeList();
	}//for subCategories
	%>
</table>
<div>
<input name="action" type="submit" class="auditSave" value="Save">
Click to save your work. You may still edit your information later.</td>
<input type="hidden" name="catID" value="<%=catID%>"> <input
	name="auditID" type="hidden" value="<%=action.getAuditID()%>">
<input type="hidden" name="requiredCount" value="<%=requiredCount%>">
</div>
<%
	} finally {
		pqBean.closeList();
		pcBean.closeList();
	}
%>
</form>
</body>
</html>
