<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean"
	scope="page" />
<jsp:useBean id="psBean"
	class="com.picsauditing.PICS.pqf.SubCategoryBean" scope="page" />
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean"
	scope="page" />
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean"
	scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<jsp:useBean id="action"
	class="com.picsauditing.actions.audits.ContractorAuditLegacy"
	scope="page" />


<%
	action.setAuditID(request.getParameter("auditID"));
	String auditType = action.getAudit().getAuditType().getLegacyCode();
	String conID = action.getAudit().getContractorAccount().getId().toString();
	String id = conID;

	try {

		String catID = request.getParameter("catID");
		String actionString = request.getParameter("action");
		String auditID = request.getParameter("auditID");


		boolean isCategorySelected = (null != catID && !"0"
		.equals(catID));
		if (!isCategorySelected) {
			response.sendRedirect("pqf_edit.jsp?id=" + conID
			+ "&auditType="
			+ com.picsauditing.PICS.pqf.Constants.PQF_TYPE);
			return;
		}
		int numQuestions = 0;
		int numSections = 0;
		aBean.setFromDB(conID);
		cBean.setFromDB(conID);
		cBean.tryView(permissions);

		if ("Save".equals(actionString)) {
			pdBean.uploadPQFFile(pageContext, conID, catID, auditID);
			if (pdBean.isOK()) {
		pdBean.savePQF(request, action.getAudit(),
				permissions);
		pdBean.savePQFUpload(request, action.getAudit(),permissions);
		pdBean.updatePercentageCompleted(action.getAuditID(), catID);

		action.getAudit().setPercentComplete(new Integer(pdBean.getPercentComplete(conID, action.getAudit().getAuditType().getAuditName())));
		action.getAudit().setPercentVerified(new Integer(pdBean.getPercentVerified(conID, action.getAudit().getAuditType().getAuditName())));
		action.saveAudit();
		
		response.sendRedirect("pqf_edit.jsp?id=" + conID
				+ "&auditType="
				+ com.picsauditing.PICS.pqf.Constants.PQF_TYPE);
		return;
			}
		}
		if ("Delete".equals(actionString)) {
			String qID = request.getParameter("qID");
			String ext = request.getParameter("ext");
			pdBean.deletePQFFile(config, conID, qID, ext);
		}

		psBean.setPQFSubCategoriesArray(catID);
		pdBean.setFromDB(action.getAuditID(), conID, catID);
		pqBean.highlightRequired = pdBean.alreadySavedCat;
%>

<html>
<head>
<title>Upload File</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<script language="JavaScript" src="js/TimeOutWarning.js"></script>
<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" SRC="js/validateForms.js"></SCRIPT>
</head>
<body onload="return window_onload();">
<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
	<form name="formEdit" method="post"
		action="pqf_uploadFile.jsp?auditType=<%=auditType%>&catID=<%=catID%>&id=<%=id%>&action=Save"
		enctype="multipart/form-data">
	<tr align="left" class="blueMain">
		<td><%@ include file="includes/conHeaderLegacy.jsp"%></td>
	</tr>
	<tr align="center" class="blueMain">
		<td class="blueHeader">PQF for <%=aBean.name%></td>
	</tr>
	<tr align="center">
		<td class="redmain"><strong><%=pdBean.getErrorMessages()%></strong></td>
	</tr>
	<tr align="center">
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<%
				if (isCategorySelected) {
				pcBean.setFromDB(catID);
	%>
	<tr align="center">
		<td align="left">
		<%
		if (pBean.isAdmin() || pBean.isAuditor()) {
		%>
		<center><input type="checkbox" name="catDoesNotApply"
			value="Yes"
			<%=com.picsauditing.PICS.Inputs.getChecked("Yes",pdBean.catDoesNotApply)%>>
		Check here if this entire category does not apply</center>
		<br>
		<%
		}//if
		%> <input name="action" type="submit" class="forms"
			value="Save"> Click to save your work. You may still edit
		your information later.<br>
		<%
		if (pqBean.highlightRequired) {
		%> <span class="redMain"><strong>*
		Red questions are required and must be answered to be able to submit
		your PQF</strong></span> <br>
		<span class="redMain">&nbsp;&nbsp;(File uploads must be in
		.pdf, .doc, .txt, .xls or .jpg format.)</strong></span> <%
 }//if
 %>
		<table width="657" border="0" cellpadding="1" cellspacing="0">
			<tr class="blueMain">
				<td bgcolor="#003366" colspan="3" align="center"><font
					color="#FFFFFF"><strong>Category <%=pcBean.number%>
				- <%=pcBean.category%></strong></font></td>
			</tr>
			<tr>
				<td colspan="3"></td>
			</tr>
			<%
						numSections = 0;
						for (java.util.ListIterator li = psBean.subCategories
						.listIterator(); li.hasNext();) {
					numSections++;
					String subCatID = (String) li.next();
					String subCat = (String) li.next();
					pqBean.setSubListWithData("number", subCatID, conID);
			%>
			<tr class="blueMain">
				<td bgcolor="#003366" colspan="3" align="center"><font
					color="#FFFFFF"><strong>Sub Category <%=numSections%>
				- <%=subCat%></strong></font></td>
			</tr>
			<%
					numQuestions = 0;
					while (pqBean.isNextRecord()) {
						numQuestions++;
			%>
			<%=pqBean.getTitleLine("blueMain")%>
			<tr <%=pqBean.getGroupBGColor()%> class=blueMain>
				<td valign="top" <%=pqBean.getClassAttribute(pdBean)%>><%=pqBean.number%>.</td>
				<td valign="top" <%=pqBean.getClassAttribute(pdBean)%>><%=pqBean.question%>
				<%=pqBean.getLinksWithCommas()%></td>
				<td width=200 valign="bottom"><%=pqBean.getInputElement()%> <%=pqBean.getOriginalAnswerView()%>
				<%
								if (!"".equals(pqBean.data.answer)) {
								if (pBean.isAdmin()) {
				%> <span align=right>&nbsp;&nbsp;&nbsp;<a
					href="pqf_uploadFile.jsp?action=Delete&qID=<%=pqBean.questionID%>&ext=<%=pqBean.data.answer%>&catID=<%=catID%>&id=<%=conID%>&auditType=<%=auditType%>">Delete</a></span>
				<%
				}//if
				%> <input type=hidden name=isUploaded_
					<%=pqBean.questionID%> value=Uploaded> <%
 			//	<br><a href=files/pqf/<%=pqBean.questionID%_<%=conID%.pdf target=_blank\>View File</a>		
 			}//if
 %> <input type=hidden name=pqfQuestionID_
					<%=pqBean.questionID%> value=<%=pqBean.questionID%>> <input
					type=hidden name=isRequired_ <%=pqBean.questionID%>
					value="<%=pqBean.calcIsRequired(pdBean)%>"> <%
 			/*		    <input type="hidden" name="pqfQuestion_<%=pqBean.questionID" value="<%=pqBean.question">
 			<input type="hidden" name="pqfQuestionNum_<%=pqBean.questionID" value="<%=pqBean.number">
 			 */
 %> <input type="hidden" name="pqfQuestionType_<%=pqBean.questionID%>"
					value="<%=pqBean.questionType%>"></td>
			</tr>
			<%
					}//while
					pqBean.closeList();
						}//for
			%>
		</table>
		<br>
		<input name="action" type="submit" class="forms" value="Save">
		Click to save your work. You may still edit your information later.</td>
	</tr>
	</form>
	<%
	}//if
	%>
</table>
</body>
</html>
<%
		} finally {
		pqBean.closeList();
	}//finally
%>