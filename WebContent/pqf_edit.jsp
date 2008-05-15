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
	action.setAuditID(request.getParameter("auditID"));
	String auditType = action.getAudit().getAuditType().getLegacyCode();
	String conID = action.getAudit().getContractorAccount().getId().toString();
	String id = conID;
	
	try {
		String catID = request.getParameter("catID");
		String actionString = request.getParameter("action");
		boolean isCategorySelected = (null != catID && !"0".equals(catID));
		if (!isCategorySelected) {
			response.sendRedirect("pqf_edit.jsp?auditID=" + action.getAuditID());
			return;
		}
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
				response.sendRedirect("pqf_viewOSHA.jsp?id=" + conID + "&catID=" + catID);
			else
				response.sendRedirect("pqf_OSHA.jsp?action=Edit&oID=New&id=" + conID + "&catID=" + catID);
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
			pdBean.savePQF(request, conID, catID, auditType, pBean.userID);
			//TODO Make a contractor edit a pqf. 
			//cBean.setPercentComplete(auditType, action.getPercentComplete());
			//cBean.setPercentVerified(auditType, pdBean.getPercentVerified(conID, auditType));
			cBean.writeToDB();
			pdBean.updatePercentageCompleted(action.getAuditID(), catID);
			response.sendRedirect("pqf_edit.jsp?auditID=" + action.getAuditID());
			return;
		}//if
		psBean.setPQFSubCategoriesArray(catID);
		pdBean.setFromDB(action.getAuditID(), conID, catID);
		pqBean.highlightRequired = pdBean.alreadySavedCat;
%>
<html>
<head>
<title>Edit PQF</title>
<script language="JavaScript" src="js/TimeOutWarning.js"></script>
<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" SRC="js/validateForms.js"></SCRIPT>
<meta name="header_gif" content="header_prequalification.gif" />
</head>
<body onload="return window_onload();">
<form name="formEdit" method="post" action="pqf_edit.jsp">
<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
	<tr align="center" class="blueMain">
		<td align="left"><h1><%=aBean.getName(id)%><span class="sub"><%=auditType%> for <%=aBean.name%></span></h1>
		<%@ include file="utilities/adminOperatorContractorNav.jsp"%></td>
	</tr>
	<tr align="center">
		<td class="redmain"><strong><%=errorMsg%></strong></td>
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
			value="Yes" <%=Inputs.getChecked("Yes",pdBean.catDoesNotApply)%>>
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
		your PQF</strong></span> <%
 	}//if
 %>
		<table width="657" border="0" cellpadding="1" cellspacing="0">
			<tr class="blueMain">
				<td bgcolor="#003366" colspan="3" align="center"><font
					color="#FFFFFF"><strong>Category <%=pcBean.number%>
				- <%=pcBean.category%></strong></font></td>
			</tr>
			<%
				numSections = 0;
						for (java.util.ListIterator li = psBean.subCategories.listIterator(); li.hasNext();) {
							numSections++;
							String subCatID = (String) li.next();
							String subCat = (String) li.next();
							pqBean.setSubListWithData("number", subCatID, conID);
			%>
			<tr class="blueMain">
				<td bgcolor="#003366" colspan="3" align="center"><font
					color="#FFFFFF"><strong>Sub Category <%=pcBean.number%>.<%=numSections%>
				- <%=subCat%></strong></font></td>
			</tr>
			<%
				numQuestions = 0;
							while (pqBean.isNextRecord()) {
								if (pqBean.calcIsRequired(pdBean))
									requiredCount++;
								numQuestions++;
			%>
			<%=pqBean.getTitleLine("blueMain")%>
			<tr <%=pqBean.getGroupBGColor()%> class=blueMain>
				<td valign="top" <%=pqBean.getClassAttribute(pdBean)%>><%=pqBean.number%>.</td>
				<td valign="top" <%=pqBean.getClassAttribute(pdBean)%>><%=pqBean.question%>
				<%=pqBean.getLinksWithCommas()%></td>
				<td width=50 valign="bottom"><%=pqBean.getInputElement()%> <input
					type=hidden name=pqfQuestionID_ <%=pqBean.questionID%>
					value="<%=pqBean.questionID%>"> <input type=hidden
					name=isRequired_ <%=pqBean.questionID%>
					value="<%=pqBean.isRequired%>"> <input type=hidden
					name=oldAnswer_ <%=pqBean.questionID%>
					value="<%=pqBean.data.answer%>"> <input type=hidden
					name=oldComment_ <%=pqBean.questionID%>
					value="<%=pqBean.data.comment%>"> <input type=hidden
					name=oldDateVerified_ <%=pqBean.questionID%>
					value="<%=pqBean.data.dateVerified%>"> <input type=hidden
					name=oldAuditorID_ <%=pqBean.questionID%>
					value="<%=pqBean.data.auditorID%>"> <input type=hidden
					name=wasChanged_ <%=pqBean.questionID%>
					value="<%=pqBean.data.wasChanged%>"> <%
 	if ("Depends".equals(pqBean.isRequired)) {
 %>
				<input type=hidden name=dependsOnQID_ <%=pqBean.questionID%>
					value="<%=pqBean.dependsOnQID%>"> <input type=hidden
					name=dependsOnAnswer_ <%=pqBean.questionID%>
					value="<%=pqBean.dependsOnAnswer%>"> <%
 	}//if
 %> <input
					type="hidden" name="pqfQuestionType_<%=pqBean.questionID%>"
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
	<%
		}
	%>
</table>
<input type="hidden" name="catID" value="<%=catID%>"> <input
	name="auditID" type="hidden" value="<%=action.getAuditID()%>">
<input type="hidden" name="requiredCount" value="<%=requiredCount%>">
</form>
</body>
</html>
<%
	} finally {
		pqBean.closeList();
		pcBean.closeList();
	}
%>