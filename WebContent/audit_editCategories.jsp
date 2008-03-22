<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean"
	scope="page" />
<%
	permissions.tryPermission(OpPerms.ManageAudits);

	String action = request.getParameter("action");
	String auditType = request.getParameter("auditType");
	boolean isAuditTypeSelected = (null != auditType && !aqBean.DEFAULT_AUDIT_TYPE.equals(auditType));
	if (!isAuditTypeSelected) {
		auditType = "Field";
		isAuditTypeSelected = true;
	}//if
	String id = request.getParameter("id");
	if (isAuditTypeSelected) {
		if ("Add".equals(action)) {
			String newCategory = request.getParameter("newCategory");
			if (!"".equals(newCategory)) {
				aqBean.addCategory(newCategory, auditType);
				aqBean.resetCategoryIDNameTypeAL();
			}//if
		}//if
		if ("Delete".equals(action)) {
			String deleteCategoryID = request.getParameter("deleteCategoryID");
			aqBean.deleteCategory(deleteCategoryID);
			aqBean.resetCategoryIDNameTypeAL();
		}//if
	}//if
%>
<html>
<head>
<title>Edit Audit Categories</title>
</head>
<body>
<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
	<tr align="center" class="blueMain">
		<td colspan="2" class="blueHeader">Edit Audit Categories</td>
	</tr>
	<tr align="center" class="blueMain">
		<td colspan="2" class="blueMain"><%@ include
			file="includes/nav/editAuditNav.jsp"%></td>
	</tr>
	<tr align="center">
		<td colspan="2"><br>
		<form name="form1" method="post" action="audit_editCategories.jsp">
		<table border="1" cellpadding="5" cellspacing="0"
			bordercolor="#FFFFFF" class="blueMain">
			<tr>
				<td align="right" valign="top" class="redMain">Audit Type:</td>
				<td><%=aqBean.getAuditTypeSelectSubmit("auditType", "forms", auditType)%></td>
			</tr>
			<%
				if (isAuditTypeSelected) {
			%>
			<tr>
				<td align="right" valign="top" class="redMain">New Category:</td>
				<td><input name="newCategory" type="text" class="forms"
					size="50"> <input name="action" type="submit" class="forms"
					value="Add"></td>
			</tr>
			<tr>
				<td width="100" align="right" valign="top" class="redMain">Delete
				Category:</td>
				<td><%=aqBean.getAuditCategoriesSelect2("deleteCategoryID", "blueMain", "", auditType)%>
				<input name="action" type="submit" class="forms" value="Delete">
				</td>
			</tr>
			<%
				}//if
			%>
		</table>
		</form>
		<br>
		<br>
		<br>
		</td>
	</tr>
</table>
</body>
</html>
