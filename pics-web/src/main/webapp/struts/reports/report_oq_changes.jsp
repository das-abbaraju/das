<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:text name="%{scope}.title" /></h1>
<table class="report">
	<thead>
		<tr>
			<th><s:text name="global.Type" /></th>
			<th><s:text name="%{scope}.label.Task" /></th>
			<th><s:text name="%{scope}.label.Criteria" /></th>
			<th><s:text name="%{scope}.label.Action" /></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="dataCriteria">
			<tr>
				<td><s:property value="get('taskType')" /></td>
				<td><s:property value="get('task')" /></td>
				<td><s:property value="get('criteria')" /></td>
				<td>
					<s:if test="get('daysFromExpiration') > 0"><s:text name="%{scope}.message.Added"><s:param><s:date name="get('effectiveDate')" format="format="%{@com.picsauditing.util.PicsDateFormat@Iso}"" /></s:param></s:text></s:if>
					<s:else><s:text name="%{scope}.message.Removed"><s:param><s:date name="get('expirationDate')" format="format="%{@com.picsauditing.util.PicsDateFormat@Iso}"" /></s:param></s:text></s:else>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<table class="report">
	<thead>
		<tr>
			<th><s:text name="global.Type" /></th>
			<th><s:text name="%{scope}.label.Task" /></th>
			<th><s:text name="%{scope}.label.Site" /></th>
			<th><s:text name="%{scope}.label.Action" /></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="dataSites">
			<tr>
				<td><s:property value="get('taskType')" /></td>
				<td><s:property value="get('task')" /></td>
				<td><s:if test="!permissions.operator"><s:property value="get('opName')" />: </s:if><s:property value="get('name')" /></td>
				<td>
					<s:if test="get('daysFromExpiration') > 0"><s:text name="%{scope}.message.Added"><s:param><s:date name="get('effectiveDate')" format="format="%{@com.picsauditing.util.PicsDateFormat@Iso}"" /></s:param></s:text></s:if>
					<s:else><s:text name="%{scope}.message.Removed"><s:param><s:date name="get('expirationDate')" format="format="%{@com.picsauditing.util.PicsDateFormat@Iso}"" /></s:param></s:text></s:else>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>
</body>
</html>
