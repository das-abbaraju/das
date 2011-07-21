<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> Documents</title>

</head>
<body>
<div id="tabs">
	<div id="tabs-audits">
		<table class="report">
			<thead>
				<tr>
					<th>Governing Operator</th>
					<th>Document</th>
					<th>Previous Status</th>
					<th>Current Status</th>
					<th>Changed On</th>
				</tr>
			</thead>
			<s:iterator value="getCaoStats(opID).keySet()" id="status">
				<tr>
					<td><s:property value="#status.operator.name"/></td>
					<td><a href="Audit.action?auditID=<s:property value="audit.id" />">
						<s:if test="audit.auditFor.length() > 0"><s:property value="audit.auditFor" /></s:if>
						<s:property value="audit.auditType.name" /></a></td>
						<td><s:property value="getCaoStats(opID).get(#status)"/></td>
						<td><s:property value="#status.status"/></td>
						<td><s:date name="#status.statusChangedDate"/></td>
				</tr>
			</s:iterator>
		</table>
	</div>
</div>

</body>
</html>
