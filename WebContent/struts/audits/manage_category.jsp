<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Category</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<table>
<tr><td style="vertical-align: top">

<s:form id="save">
<s:hidden name="id" />
<s:hidden name="category.auditType.auditTypeID" />
<table class="forms">
<tr>
<th>ID:</th>
<td><s:property value="category.id"></s:property></td>
</tr>
<tr>
<th>Category Name:</th>
<td><s:textfield name="category.category" size="30" /></td>
</tr>
<tr>
<th>Order:</th>
<td><s:textfield name="category.number" size="4"/></td>
</tr>
</table>
<button class="positive" name="button" value="save">Save</button>
<s:if test="category.subCategories.size == 0">
	<button class="positive" name="button" value="delete">Delete</button>
</s:if>
</s:form>
</td>
<s:if test="id != 0">
<td style="vertical-align: top">
	<table class="report">
	<s:iterator value="category.subCategories">
	<tr>
		<td><s:property value="number"/></td>
		<td><a href="ManageSubCategory.action?id=<s:property value="id"/>"><s:property value="subCategory"/></a></td>
	</tr>
	</s:iterator>
	<tr>
		<td>*</td>
		<td><a href="ManageSubCategory.action?button=AddNew&parentID=<s:property value="category.id"/>&subCategory.category.id=<s:property value="category.id"/>">Add New Sub Category</a></td>
	</tr>
	</table>
</td>
</s:if>
</tr>
</table>

</body>

</html>