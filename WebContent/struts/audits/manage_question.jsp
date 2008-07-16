<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Question</title>
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
<s:hidden name="question.subCategory.id" />
<table class="forms">
<tr>
<th>ID:</th>
<td><s:property value="question.questionID"></s:property></td>
</tr>
<tr>
<th>Order:</th>
<td><s:textfield name="question.number" size="4"/></td>
</tr>
<tr>
<th>Question:</th>
<td><s:textarea name="question.question" rows="2" cols="50" /></td>
</tr>
<tr>
<th>Added:</th>
<td><s:date name="question.dateCreated" /></td>
</tr>
<tr>
<th>Updated:</th>
<td><s:date name="question.lastModified" /></td>
</tr>
<tr>
<th>Has Requirement:</th>
<td><s:checkbox name="question.hasRequirement" /></td>
</tr>
<tr>
<th>OK Answer:</th>
<td><s:textfield name="question.okAnswer" /></td>
</tr>
<tr>
<th>Requirement:</th>
<td><s:textarea name="question.requirement" rows="2" cols="50" /></td>
</tr>
<tr>
<th>Flaggable:</th>
<td><s:checkbox name="question.isRedFlagQuestion" /></td>
</tr>
<tr>
<th>Required:</th>
<td><s:select list="#{'No':'No','Yes':'Yes','Depends':'Depends'}" name="question.isRequired" /></td>
</tr>
<tr>
<th>Depends on Question ID:</th>
<td><s:textfield name="question.dependsOnQuestion.questionID" /></td>
</tr>
<tr>
<th>Depends on Answer:</th>
<td><s:textfield name="question.dependsOnAnswer" /></td>
</tr>
<tr>
<th>Question Type:</th>
<td><s:select list="questionTypes" name="question.questionType" /></td>
</tr>
<tr>
<th>Title:</th>
<td><s:textfield name="question.title" /></td>
</tr>
<tr>
<th>Visible:</th>
<td><s:checkbox name="question.isVisible" /></td>
</tr>
<tr>
<th>Grouped with Previous:</th>
<td><s:checkbox name="question.isGroupedWithPrevious" /></td>
</tr>
<tr>
<th>Url:</th>
<td><s:textfield name="question.linkUrl1" /></td>
</tr>
<tr>
<th>Label:</th>
<td><s:textfield name="question.linkText1" /></td>
</tr>
</table>
<div class="buttons"><a href="#" class="positive"
	onclick="$('save').submit(); return false;">Search</a></div>
</s:form>
</td>
<td style="vertical-align: top">
	<table class="report">
	</table>
</td>
</tr>
</table>

</body>

</html>