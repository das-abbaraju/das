<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean"
	scope="page" />
<%
	String action = request.getParameter("action");
	String id = request.getParameter("id");
	String auditType = request.getParameter("auditType");
	if (null == auditType)
		auditType = "Office";
	boolean isAuditTypeSelected = (null != auditType && !aqBean.DEFAULT_AUDIT_TYPE.equals(auditType));
	String orderBy = request.getParameter("orderBy");
	try {
		if (isAuditTypeSelected && "Change Numbering".equals(action)) {
			aqBean.updateNumbering(request);
			aqBean.renumberAudit(auditType);
		}//if
		if (isAuditTypeSelected && "Delete".equals(action)) {
			String delID = request.getParameter("deleteID");
			aqBean.deleteQuestion(delID);
			aqBean.renumberAudit(auditType);
		}//if
%>
<html>
<head>
<title>Edit Audit Questions</title>
</head>
<body>
<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
	<tr align="center" class="blueMain">
		<td class="blueHeader">Edit Audit Questions</td>
	</tr>
	<tr align="center" class="blueMain">
		<td class="blueMain"><%@ include
			file="includes/nav/editAuditNav.jsp"%></td>
	</tr>
	<tr>
		<td align=center>
		<br>
		<form name=form1 method=post action=audit_editQuestions.jsp>
			Select Audit:<%=aqBean.getAuditTypeSelectSubmit("auditType", "forms", auditType)%>
		</form>
		</td>
	</tr>
	<%
		if (isAuditTypeSelected) {
	%>
	<tr align="center">
		<td>
		<form name="form1" method="post" action="audit_editQuestions.jsp">
		<table width="657" border="0" cellpadding="1" cellspacing="1">
			<tr class="whiteTitle">
				<td bgcolor="#003366"><a href="?orderBy=num" class="whiteTitle">Num</a></td>
				<td bgcolor="#003366"><a href="?orderBy=category"
					class="whiteTitle">Category</a></td>
				<td bgcolor="#003366">Question</td>
				<td width="50" bgcolor="#003366">OK Ans</td>
				<td bgcolor="#003366">Requirement</td>
				<td bgcolor="#993300"></td>
				<td bgcolor="#993300"></td>
			</tr>
			<%
				aqBean.setList(orderBy, auditType);
						while (aqBean.isNextRecord()) {
			%>
			<tr class="blueMain" <%=aqBean.getBGColor()%>>
				<td><input name="num_<%=aqBean.questionID%>" type="text"
					class="forms" id="num_<%=aqBean.questionID%>"
					value="<%=aqBean.num%>" size="3"></td>
				<td><%=aqBean.getCategoryName()%></td>
				<td><%=aqBean.question%></td>
				<td><%=aqBean.okAnswer%></td>
				<td><%=aqBean.getRequirement()%></td>
				<td align="center"><a
					href="audit_editQuestion.jsp?editID=<%=aqBean.questionID%>">Edit</a></td>
				<td align="center"><a
					href="audit_editQuestions.jsp?deleteID=<%=aqBean.questionID%>&action=Delete&auditType=<%=auditType%>">Del</a></td>
			</tr>
			<%
				}//while
						aqBean.closeList();
			%>
		</table>
		<br>
		<input name="action" type="submit" class="forms"
			value="Change Numbering"> <br>
		<br>
		<input type=hidden name=auditType value="<%=auditType%>">
		</form>
		</td>
	</tr>
	<%
		}//if
	%>
</table>
<%
	} finally {
		aqBean.closeList();
	}
%>
</body>
</html>
