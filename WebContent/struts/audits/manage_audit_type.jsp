<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Audit Types</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<table>
<tr><td style="vertical-align: top">

<s:form id="save">
<s:hidden name="id"></s:hidden>
<table class="forms">
<tr>
<th>ID:</th>
<td><s:property value="auditType.auditTypeID"></s:property></td>
</tr>
<tr>
<th>Name:</th>
<td><s:textfield name="auditType.auditName"></s:textfield></td>
</tr>
<tr>
<th>Description:</th>
<td><s:textarea name="auditType.description" rows="2" cols="30" /></td>
</tr>
<tr>
<th>Has Multiple:</th>
<td><s:checkbox name="auditType.hasMultiple" /></td>
</tr>
<tr>
<th>Is Scheduled:</th>
<td><s:checkbox name="auditType.isScheduled" /></td>
</tr>
<tr>
<th>Has Auditor:</th>
<td><s:checkbox name="auditType.hasAuditor" /></td>
</tr>
<tr>
<th>Has Requirements:</th>
<td><s:checkbox name="auditType.hasRequirements" /></td>
</tr>
<tr>
<th>Contractor Can View:</th>
<td><s:checkbox name="auditType.canContractorView" /></td>
</tr>
<tr>
<th>Contractor Can Edit:</th>
<td><s:checkbox name="auditType.canContractorEdit" /></td>
</tr>
<tr>
<th>Months to Expire:</th>
<td><s:textfield name="auditType.monthsToExpire" /></td>
</tr>
<tr>
<th>Date to Expire:</th>
<td><s:textfield name="auditType.dateToExpire" /></td>
</tr>
<tr>
<th>Legacy Code:</th>
<td><s:textfield name="auditType.legacyCode" /></td>
</tr>
<tr>
<th>Order:</th>
<td><s:textfield name="auditType.displayOrder" /></td>
</tr>
</table>
<button class="positive" name="button" value="save">Save</button>
<button class="positive" name="button" value="delete">Delete</button>
</s:form>
</td>
<td style="vertical-align: top">
	<table class="report">
	<s:iterator value="auditType.categories">
	<tr>
	<td><s:property value="number"/></td>
	<td><a href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="category"/></a></td>
	</tr>
	</s:iterator>
	<tr>
	<td>*</td>
	<td><a href="ManageCategory.action?button=AddNew&parentID=<s:property value="auditType.auditTypeID"/>&category.auditType.auditTypeID=<s:property value="auditType.auditTypeID"/>">Add New Category</a></td>
	</tr>
	</table>
</td>
</tr>
</table>

</body>
</html>