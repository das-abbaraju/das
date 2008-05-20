<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope="page" />
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope="page" />
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope="page" />
<jsp:useBean id="action" class="com.picsauditing.actions.auditType.AuditTypeLegacy" scope="page" />
<%
	permissions.tryPermission(OpPerms.ManageAudits);
	action.setAuditTypeID(request.getParameter("auditTypeID"));

	try {
		String actionString = request.getParameter("action");
		String editCatID = request.getParameter("editCatID");
		String editSubCatID = request.getParameter("editSubCatID");
		boolean isCategorySelected = (null != editCatID && !"0".equals(editCatID));
		boolean isSubCategorySelected = (null != editSubCatID && !"0".equals(editSubCatID));
		if ("Change Numbering".equals(actionString)) {
			pqBean.updateNumbering(request);
			pqBean.renumberPQF(editSubCatID, action.getAuditTypeID());
		}//if
		if ("Delete".equals(actionString)) {
			String delID = request.getParameter("deleteID");
			pqBean.deleteQuestion(delID, config.getServletContext().getRealPath("/"));
			pqBean.renumberPQF(editSubCatID, action.getAuditTypeID());
		}//if
		String orderBy = request.getParameter("orderBy");
%>

<html>
<head>
<title>Audit Questions</title>
</head>
<body>
<h1>Audit Management
<span class="sub">Edit <%=action.getAuditType().getAuditName()%> Sub Categories</span>
</h1>
<div><a href="AuditTypeChoose.action">Select a different Audit Type</a></div>

<table border="0" cellspacing="0" cellpadding="1" class="blueMain"
	width="100%">
	<tr align="center" class="blueMain">
		<td class="blueHeader">Edit <%=action.getAuditType().getAuditName()%>
		Questions</td>
	</tr>
	<tr>
		<td align="center">
		<form name="form" method="get" action="pqf_editQuestions.jsp">
		<input type="hidden" name="auditTypeID" value="<%=action.getAuditTypeID() %>" />
			<%=pcBean.getPqfCategorySelectDefaultSubmit("editCatID", "blueMain", editCatID, action
								.getAuditTypeID())%></form>
		</td>
	</tr>
	<%
		if (isCategorySelected) {
	%>
	<tr>
		<td align="center">
		<form name="form" method="get" action="pqf_editQuestions.jsp">
		<input type="hidden" name="auditTypeID"
			value="<%=action.getAuditTypeID() %>" /><input type=hidden
			name=editCatID value=<%=editCatID%>> <%=psBean.getPqfSubCategorySelectDefaultSubmit("editSubCatID", "blueMain", editSubCatID,
									editCatID)%></form>
		</td>
	</tr>
	<%
		}//if
	%>
	<%
		if (isSubCategorySelected) {
	%>
	<tr>
		<td align="center"><a
			href="pqf_editQuestion.jsp?auditTypeID=<%=action.getAuditTypeID() %>&subCategoryID=<%=editSubCatID%>">Add Question</a></td>
	</tr>
	<tr align="center">
		<td>
		<form name="form1" method="post" action="pqf_editQuestions.jsp">
		<input type="hidden" name="auditTypeID" value="<%=action.getAuditTypeID() %>" />
		<input type="hidden" name="editCatID" value="<%=editCatID%>" />
		<table width="750" border="0" cellpadding="1" cellspacing="1">
			<tr class="whiteTitle">
				<td bgcolor="#003366"><a
					href="?orderBy=number&auditTypeID=<%=action.getAuditTypeID() %>&editCatID=<%=editCatID %>&editSubCatID=<%=editSubCatID%>"
					class="whiteTitle">Num</a></td>
				<td bgcolor="#003366"><a
					href="?orderBy=questionID&auditTypeID=<%=action.getAuditTypeID() %>&editCatID=<%=editCatID %>&editSubCatID=<%=editSubCatID%>"
					class="whiteTitle">qID</a></td>
				<td bgcolor="#003366">Text</td>
				<td bgcolor="#003366">Type</td>
				<td bgcolor="#003366">Required</td>
				<td bgcolor="#993300"></td>
				<td bgcolor="#993300"></td>
			</tr>
			<%
				pqBean.setSubList(orderBy, editSubCatID);
						while (pqBean.isNextRecord()) {
							if (!"".equals(pqBean.title)) {
			%>
			<tr class="blueMain" <%=pqBean.getGroupBGColor()%>>
				<td colspan=6><strong><%=pqBean.title%></strong></td>
			</tr>
			<%
				}//if
			%>
			<tr class="blueMain" <%=pqBean.getGroupBGColor()%>>
				<td><input name="num_<%=pqBean.questionID%>" type="text"
					class="forms" id="num_<%=pqBean.questionID%>"
					value="<%=pqBean.number%>" size="3"></td>
				<td><%=pqBean.questionID%></td>
				<td><%=pqBean.question%></td>
				<td><%=pqBean.questionType%></td>
				<td><%=pqBean.isRequired%></td>
				<td align="center"><a
					href="pqf_editQuestion.jsp?auditTypeID=<%=action.getAuditTypeID() %>&editID=<%=pqBean.questionID%>">Edit</a></td>
				<td align="center"><a
					href="pqf_editQuestions.jsp?auditTypeID=<%=action.getAuditTypeID() %>&editCatID=<%=editCatID%>&editSubCatID=<%=editSubCatID%>&deleteID=<%=pqBean.questionID%>&action=Delete"
					onClick="return confirm('Hold on dude!! Are you sure you want to delete this question?  Cuz if you do, it is never coming back!');">Del</a></td>
			</tr>
			<%
				}//while
						pqBean.closeList();
			%>
		</table>
		<br>
		<%
			if (isSubCategorySelected)
						out.println("<input name=editSubCatID type=hidden value=" + editSubCatID + ">\n");
		%> <input name="action" type="submit" class="forms"
			value="Change Numbering"> <br>
		<br>
		</form>
		</td>
	</tr>
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