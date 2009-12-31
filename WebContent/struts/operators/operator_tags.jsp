<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="operator.name" /> Tags</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />
</head>
<body>
<s:if test="permissions.admin">
	<s:include value="opHeader.jsp"></s:include>
</s:if>
<a href="OperatorTags.action?id=<s:property value="id" />">Refresh</a>

<s:form>
	<s:hidden name="id" />
	<table class="report">
		<thead>
			<tr>
				<th>Tag Name</th>
				<th>Visible</th>
			</tr>
		</thead>
		<s:iterator value="tags" status="rowstatus">
			<tr>
				<td><s:hidden name="tags[%{#rowstatus.index}].id" value="%{id}" /> <s:textfield
					name="tags[%{#rowstatus.index}].tag" value="%{tag}" /></td>
				<td><s:checkbox name="tags[%{#rowstatus.index}].active" value="%{active}" /></td>
			</tr>
		</s:iterator>
		<tr>
			<td><s:textfield name="tags[%{tags.size}].tag" value="%{tag}" /></td>
			<td>new</td>
		</tr>
	</table>

	<div>
	<button type="submit" name="button" value="Save" class="picsbutton positive">Save</button>
	</div>
</s:form>

</body>
</html>
