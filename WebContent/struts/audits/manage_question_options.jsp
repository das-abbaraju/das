<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:property value="operator.name" /> Tags</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function() {
	$('a[rel*=facebox]').facebox({
 		loading_image : 'loading.gif',
 		close_image : 'closelabel.gif'
 	});
 });
</script>
</head>
<body>
<a href="ManageQuestion.action?id=<s:property value="id"/>">&gt;&gt; Go Back</a>
<s:form>
	<s:hidden name="id" />
	<table class="report">
		<thead>
			<tr>
				<th>Option Name</th>
				<th>Visible</th>
				<th>Number</th>
				<s:if test="question.category.auditType.scoreable">
					<th>Score</th>
				</s:if>
			</tr>
		</thead>
		<s:iterator value="options" status="rowstatus">
			<tr><s:hidden name="options[%{#rowstatus.index}].id" value="%{id}" />
			<td><s:textfield
						name="options[%{#rowstatus.index}].optionName" value="%{optionName}" /></td>
					<td><s:radio list="#{'Yes':'Yes','No':'No'}"
						name="options[%{#rowstatus.index}].visible" value="%{visible}" theme="pics" />
					</td>
					<td><s:textfield
						name="options[%{#rowstatus.index}].number" value="%{number}" /></td>
					<s:if test="question.category.auditType.scoreable">
						<td><s:textfield
							name="options[%{#rowstatus.index}].score" value="%{score}" /></td>
					</s:if>
			</tr>
		</s:iterator>
			<tr>
				<td>NEW <s:textfield name="options[%{options.size}].optionName" value="%{optionName}" /></td>
				<td></td>
				<td><s:textfield
						name="options[%{options.size}].number" value="%{number}" /></td>
				<s:if test="question.category.auditType.scoreable">				
					<td><s:textfield
							name="options[%{options.size}].score" value="%{score}" />
					</td>
				</s:if>
			</tr>
		</table>
	<div>
	<button type="submit" name="button" value="Save" class="picsbutton positive">Save</button>
	</div>
</s:form>
</body>
</html>
