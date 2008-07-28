<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage SubCategory</title>
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
<s:hidden name="subCategory.category.id" />

<table class="forms">
<tr>
<th>ID:</th>
<td><s:property value="subCategory.id"></s:property></td>
</tr>
<tr>
<th>Sub Category Name:</th>
<td><s:textfield name="subCategory.subCategory" size="50" /></td>
</tr>
<tr>
<th>Order:</th>
<td><s:textfield name="subCategory.number" size="4"/></td>
</tr>
</table>
<button class="positive" name="button" value="save">Save</button>
<s:if test="subCategory.questions.size == 0">
	<button class="positive" name="button" value="delete">Delete</button>
</s:if>

</s:form>
</td>
<s:if test="id != 0">
<td style="vertical-align: top">
	<table class="report">
	<thead>
	<tr>
		<th>#</th>
		<th>Question</th>
		<th>Req</th>
		<th>Type</th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="subCategory.questions">
	<tr>
		<td><s:property value="number"/></td>
		<td><a href="ManageQuestion.action?id=<s:property value="questionID"/>"><s:property value="question.length()>50 ? question.substring(0,47) + '...' : question"/></a></td>
		<td><s:property value="isRequired"/></td>
		<td><s:property value="questionType"/></td>
	</tr>
	</s:iterator>
	<tr>
		<td>*</td>
		<td><a href="ManageQuestion.action?button=AddNew&parentID=<s:property value="subCategory.id"/>&question.subCategory.id=<s:property value="subCategory.id"/>">Add New Question</a></td>
		<td></td>
		<td></td>
	</tr>
	</tbody>
	</table>
</td>
</s:if>
</tr>
</table>

</body>

</html>