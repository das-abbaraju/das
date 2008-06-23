<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="action" class="com.picsauditing.actions.auditType.AuditTypeLegacy" scope="page" />
<%
	permissions.tryPermission(OpPerms.ManageAudits);
	action.setAuditTypeID(request.getParameter("auditTypeID"));
	String auditTypeID = request.getParameter("auditTypeID");
	String actionString = request.getParameter("action");
	String editID = request.getParameter("editID");
	String categoryID = request.getParameter("categoryID");
 
	boolean addingNew = (null == editID || "null".equals(editID));
	boolean isCategorySelected = (null != categoryID && !"0".equals(categoryID));
	if (addingNew && isCategorySelected)
		psBean.categoryID = categoryID;	
	if (!addingNew)
		psBean.setFromDB(editID);
	if (null != actionString && "Submit".equals(actionString)) {
		psBean.setFromRequest(request);
		if (psBean.isOK()) {
			if (addingNew) {
				psBean.writeNewToDB();
				psBean.renumberPQFSubCategories(psBean.categoryID, action.getAuditTypeID());
			} else {
				psBean.writeToDB();
				psBean.renumberPQFSubCategories(psBean.categoryID, action.getAuditTypeID());
			}
			response.sendRedirect("pqf_editSubCategories.jsp?auditTypeID="+auditTypeID+"&editCatID="+psBean.categoryID);
			return;
		}//if
	}//if
%>

<html>
<head>
<title>Audit Sub Category</title>
</head>
<body>
<h1>Audit Management
<span class="sub"><%= (addingNew) ? "Add" : "Edit" %> <%=action.getAuditType().getAuditName()%>
		Sub Category</span>
</h1>
<div><a href="AuditTypeChoose.action">Select a different Audit Type</a></div>

<form name="form1" method="post" action="pqf_editSubCategory.jsp?editID=<%=editID%>">
	<input type="hidden" name="auditTypeID" value="<%=action.getAuditTypeID() %>" />
<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
	<tr align="center" class="blueMain">
		<td class="redMain"><%=psBean.getErrorMessages()%></td>
	</tr>
	<tr align="center">
		<td><br>
		<table border="1" cellpadding="5" cellspacing="0"
			bordercolor="#FFFFFF" class="blueMain">
			<tr>
				<td align="right" class="redMain">Audit Type:</td>
				<td align="left" class="blueMain"><%=action.getAuditType().getAuditName()%></td>
			</tr>
			<tr>
				<td align="right" class="redMain">Number:</td>
				<td><input name="number" type="text" class="forms"
					value="<%=psBean.number%>" size="3"></td>
			</tr>
			<tr>
				<td align="right" class="redMain">Category:</td>
				<td><%=pcBean.getPqfCategorySelect("categoryID","forms",psBean.categoryID, action.getAuditTypeID())%></td>
			</tr>
			<tr>
				<td align="right" class="redMain">Sub Category Name:</td>
				<td><input name="subCategory" size=50 maxlength="250"
					class="forms" value="<%=psBean.subCategory%>"></td>
			</tr>
		</table>
		<br>
		<input name="action" type="submit" class="forms" value="Submit">
		<br>
		<br>
		</td>
	</tr>
</table>
</form>
</body>
</html>