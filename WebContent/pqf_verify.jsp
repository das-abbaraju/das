<%@page language="java"
	import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*"
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
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<jsp:useBean id="action"
	class="com.picsauditing.actions.audits.ContractorAuditLegacy"
	scope="page" />
<%
	action.setPermissions(permissions);
	action.setAuditID(request.getParameter("auditID"));
	String auditType = action.getAudit().getAuditType().getLegacyCode();
	String conID = action.getAudit().getContractorAccount().getId().toString();
	String catID = request.getParameter("catID");
	String actionString = request.getParameter("action");
	if (null == catID || catID.equals(""))
		throw new Exception("Missing catID");
	try {
		boolean isOSHA = CategoryBean.OSHA_CATEGORY_ID.equals(catID);
		boolean isServices = CategoryBean.SERVICES_CATEGORY_ID.equals(catID);
		aBean.setFromDB(conID);
		cBean.setFromDB(conID);
		cBean.tryView(permissions);
		String message = "";
		if ("Save".equals(actionString)) {
			if (pBean.canVerifyAudit(auditType, conID)) {
				pdBean.saveVerificationNoUpload(request, conID, pBean.userID);

				String percent = pdBean.getPercentVerified(conID, auditType);
				action.getAudit().setPercentVerified(new Integer(percent));
				action.saveAudit();
				response.sendRedirect("Audit.action?auditID=" + action.getAuditID());
				return;
			} else
				message = "You do not have permission to verify this " + auditType + " Audit";
		}
		psBean.setPQFSubCategoriesArray(catID);
		boolean generateReqs = ("Generate Reqs".equals(actionString));
		if (generateReqs && pBean.canVerifyAudit(auditType, conID)) {
			if (action.getAudit().getPercentVerified() < 100)
				message = "You must complete verifying all questions to generate requirements";
			else {
				cBean.writeToDB();
				response.sendRedirect("pqf_editRequirements.jsp?auditID=" + action.getAuditID());
				return;
			}
		}
%>
<html>
<head>
<title>PQF Verify</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />

<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
</head>
<body>

<%@ include file="includes/conHeaderLegacy.jsp"%>

<div>Verify:<a href="pqf_view.jsp?auditID=<%=action.getAuditID()%>&catID=<%=catID %>">Switch to View Mode</a>
</div>

<form name="formEdit" method="post"><input type="hidden"
	name="auditID" value="<%=action.getAuditID()%>">
<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
	<tr align="center">
		<td class="redMain"><strong><%=message%></strong></td>
	</tr>
	<%
		int catCount = 0;
			pcBean.setFromDBWithData(catID, action.getAuditID());
			if (permissions.isPicsEmployee()) {
	%>
	<tr>
		<td><input name="action" type="submit" class="forms" value="Save">
		Click to save your work. You may still edit your information later.<br>
		</td>
	</tr>
	<%
		} //if
	%>
	<tr align="center" class="blueMain">
		<td class="redMain">&nbsp;</td>
	</tr>
	<tr align="center">
		<td align="left">
		<table width="657" border="0" cellpadding="1" cellspacing="1">
			<tr class="blueMain">
				<td bgcolor="#003366" colspan="3" align="center"><font
					color="#FFFFFF"><strong>Category <%=pcBean.number%>
				- <%=pcBean.category%></strong></font></td>
			</tr>
			<%
				int numSections = 0;
					for (java.util.ListIterator li = psBean.subCategories.listIterator(); li.hasNext();) {
						numSections++;
						String subCatID = (String) li.next();
						String subCat = (String) li.next();
						pqBean.setSubListWithData("number", subCatID, action.getAuditID());
						if (isOSHA) {
			%>
			<%@ include file="includes/pqf/view_OSHA.jsp"%>
			<%
				} else {
			%>
			<tr class="blueMain">
				<td bgcolor="#003366" colspan="3" align="center"><font
					color="#FFFFFF"><strong>Sub Category <%=pcBean.number%>.<%=numSections%>
				- <%=subCat%></strong></font></td>
			</tr>
			<%
				if (permissions.isPicsEmployee()) {
			%>
			<tr>
				<td bgcolor="#003366" colspan="2">&nbsp;</td>
				<td bgcolor="#003366" class=whiteSmall>Last Action</td>
				<%
					} // if
								int numQuestions = 0;
								while (pqBean.isNextRecord()) {
									numQuestions++;
				%>
				<%=pqBean.getTitleLine("blueMain")%>
			<tr <%=pqBean.getGroupBGColor()%> class=blueMain>
				<td valign="top" width="1%"><%=pcBean.number%>.<%=numSections%>.<%=pqBean.number%></td>
				<td valign="top"><%=pqBean.question%> <%=pqBean.getLinksWithCommas()%><br>
				<%=pqBean.getOriginalAnswerView()%></td>
				<td><%=pqBean.data.dateVerified%></td>
			</tr>
			<tr <%=pqBean.getGroupBGColor()%> class=blueMain>
				<td></td>
				<td>Is Original Answer Correct? <%=pqBean.onClickCorrectYesNoRadio(pqBean.questionID, pqBean.questionType, "forms",
											pqBean.data.isCorrect)%> <br>
				Verified Answer: <%=pqBean.getVerifiedInputElement()%> <br>
				Comment: <input type=text name=comment_ <%=pqBean.questionID%>
					class=forms size=70 value="<%=pqBean.data.comment%>"> <%%>
				
				     
			
			<%
				} // while
							pqBean.closeList();
						}//else
			%>
			<tr class=blueMain>
				<td colspan=2><br>
				<input name="action" type="submit" class="forms" value="Save">
				Click to save your work.</td>
			</tr>
			<%
				}//for
			%>
		</table>
		</td>
	</tr>
</table>
</form>
<%
	} finally {
		pqBean.closeList();
		pcBean.closeList();
	}
%>
</body>
</html>
