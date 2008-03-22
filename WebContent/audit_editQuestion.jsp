<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean"
	scope="page" />
<%
	permissions.tryPermission(OpPerms.ManageAudits);
	String action = request.getParameter("action");
	String id = request.getParameter("id");
	String multireq = request.getParameter("multireq");
	String editID = request.getParameter("editID");
	String auditType = request.getParameter("auditType");
	boolean isAuditTypeSelected = (null != auditType && !aqBean.DEFAULT_AUDIT_TYPE.equals(auditType));
	boolean isAddingNew = (null == editID || "null".equals(editID));
	if (!isAddingNew) {
		aqBean.setFromDB(editID);
		if (!isAuditTypeSelected) {
			auditType = aqBean.getAuditTypeFromCategoryID(aqBean.categoryID);
			isAuditTypeSelected = true;
		}//if
	}//if
	if ("Submit".equals(action)) {
		aqBean.setFromRequest(request);
		if (aqBean.isOK()) {
			if (isAddingNew) {
				aqBean.writeNewToDB();
				aqBean.renumberAudit(auditType);
				response.sendRedirect("audit_editQuestion.jsp?auditType=" + auditType);
				return;
			} else {
				aqBean.writeToDB();
				aqBean.renumberAudit(auditType);
				response.sendRedirect("audit_editQuestions.jsp?auditType=" + auditType);
				return;
			}//else
		}//if
	}//if
	if (null == multireq)
		multireq = aqBean.multireq;
%>

<html>
<head>
<title>Audit Question</title>
</head>
<body>
<form name="form1" method="post"
	action="audit_editQuestion.jsp?multireq=<%=multireq%>">
<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
	<tr align="center" class="blueMain">
		<td class="blueHeader">
		<%
			if (isAddingNew)
				out.print("Add ");
			else
				out.print("Edit ");
		%> Audit Question</td>
	</tr>
	<tr align="center" class="blueMain">
		<td class="blueMain"><%@ include
			file="includes/nav/editAuditNav.jsp"%></td>
	</tr>
	<tr align="center" class="blueMain">
		<td class="redMain"><%=aqBean.getErrorMessages()%></td>
	</tr>
	<tr align="center">
		<td><br>
		<table border="1" cellpadding="5" cellspacing="0"
			bordercolor="#FFFFFF" class="blueMain">
			<tr>
				<td align="right" class="redMain">Audit Type:</td>
				<td><%=aqBean.getAuditTypeSelectSubmit("auditType", "forms", auditType)%></td>
			</tr>
			<%
				if (isAuditTypeSelected) {
			%>
			<tr>
				<td align="right" class="redMain">Num:</td>
				<td><input name="num" type="text" class="forms"
					value="<%=aqBean.num%>" size="3"></td>
			</tr>
			<tr>
				<td align="right" class="redMain">Category:</td>
				<td><%=aqBean.getAuditCategoriesSelect2("categoryID", "blueMain", aqBean.categoryID,
										auditType)%></td>
			</tr>
			<tr>
				<td align="right" class="redMain">Question:</td>
				<td><textarea name="question" cols="50" rows="5" class="forms"><%=aqBean.question%></textarea></td>
			</tr>
			<tr>
				<td align="right" class="redMain">OK Answer</td>
				<td><input name="okYes" type="checkbox" value="Yes"
					<%=aqBean.isOKAnswerChecked("Yes")%>>Yes <input name="okNo"
					type="checkbox" value="No" <%=aqBean.isOKAnswerChecked("No")%>>No
				<input name="okNA" type="checkbox" value="NA"
					<%=aqBean.isOKAnswerChecked("NA")%>>NA</td>
			</tr>
			<%
				if ("Yes".equals(multireq)) {
			%>
			<tr>
				<td align="right" class="redMain">Class: <br>
				<a class="bluemain" href="?multireq=No&editID=<%=editID%>">Single
				Requirement</a></td>
				<td><textarea name="reqclass" cols="50" rows="5" class="forms"><%=aqBean.reqclass%></textarea></td>
			</tr>
			<tr>
				<td align="right" class="redMain">Program:</td>
				<td><textarea name="reqprogram" cols="50" rows="5"
					class="forms"><%=aqBean.reqprogram%></textarea></td>
			</tr>
			<%
				} else {
			%>
			<tr>
				<td align="right" class="redMain">
				<%
					if ("Field".equals(auditType))
								out.println("Comments:");
							else {
				%> Requirement:<br>
				<a class="bluemain" href="?multireq=Yes&editID=<%=editID%>">Multiple<br>
				Requirements</a></td>
				<%
					}//else
				%>
				<td><textarea name="requirement" cols="50" rows="5"
					class="forms"><%=aqBean.requirement%></textarea></td>
			</tr>
			<%
				}//else
			%>
			<tr>
				<td align="right" valign="top" class="redMain">Link 1:</td>
				<td>URL:&nbsp;&nbsp;<input name="linkURL1" type="text"
					class="forms" value="<%=aqBean.getLinkURL(1)%>" size="30"><br>
				Text: <input name="linkText1" type="text" class="forms"
					value="<%=aqBean.getLinkText(1)%>" size="30"></td>
			</tr>
			<tr>
				<td align="right" valign="top" class="redMain">Link 2:</td>
				<td>URL:&nbsp;&nbsp;<input name="linkURL2" type="text"
					class="forms" value="<%=aqBean.getLinkURL(2)%>" size="30"><br>
				Text: <input name="linkText2" type="text" class="forms"
					value="<%=aqBean.getLinkText(2)%>" size="30"></td>
			</tr>
			<tr>
				<td align="right" valign="top" class="redMain">Link 3:</td>
				<td>URL:&nbsp;&nbsp;<input name="linkURL3" type="text"
					class="forms" value="<%=aqBean.getLinkURL(3)%>" size="30"><br>
				Text: <input name="linkText3" type="text" class="forms"
					value="<%=aqBean.getLinkText(3)%>" size="30"></td>
			</tr>
			<tr>
				<td align="right" valign="top" class="redMain">Link 4:</td>
				<td>URL:&nbsp;&nbsp;<input name="linkURL4" type="text"
					class="forms" value="<%=aqBean.getLinkURL(4)%>" size="30"><br>
				Text: <input name="linkText4" type="text" class="forms"
					value="<%=aqBean.getLinkText(4)%>" size="30"></td>
			</tr>
			<tr>
				<td align="right" valign="top" class="redMain">Link 5:</td>
				<td>URL:&nbsp;&nbsp;<input name="linkURL5" type="text"
					class="forms" value="<%=aqBean.getLinkURL(5)%>" size="30"><br>
				Text: <input name="linkText5" type="text" class="forms"
					value="<%=aqBean.getLinkText(5)%>" size="30"></td>
			</tr>
			<tr>
				<td align="right" valign="top" class="redMain">Link 6:</td>
				<td>URL:&nbsp;&nbsp;<input name="linkURL6" type="text"
					class="forms" value="<%=aqBean.getLinkURL(6)%>" size="30"><br>
				Text: <input name="linkText6" type="text" class="forms"
					value="<%=aqBean.getLinkText(6)%>" size="30"></td>
			</tr>
			<%
				}//if
			%>
		</table>
		<br>
		<%
			if (!isAddingNew)
				out.println("<input name=editID type=hidden value=" + editID + ">");
		%> <input type=hidden name=multireq value="<%=multireq%>"> <input
			name=action type=submit class=forms value=Submit> <br>
		<br>
		</td>
	</tr>
</table>
</form>
</body>
</html>

