<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="../reports/reportHeader.jsp" />
<style type="text/css">
.red {
	background-color: #fbb;
}
</style>
</head>
<body>
<h1>OQ Employee</h1>
<table class="report">
	<thead>
		<tr>
			<th rowspan="2">Employee</th>
			<th rowspan="2">Contractor</th>
			<s:iterator value="jobSites.keySet()" id="key">
				<th colspan="<s:property value="jobSites.get(#key).size()" />">
					<span title="<s:property value="#key.name" />"><s:property value="#key.label" /></span>
				</th>
			</s:iterator>
		</tr>
		<tr>
			<s:iterator value="jobSites.keySet()" id="key">
				<s:iterator value="jobSites.get(#key)" id="jst">
					<th title="<s:property value="#jst.task.name" />
Span of Control = <s:property value="#jst.controlSpan" />">
						<s:property value="#jst.task.label" /></th>
				</s:iterator>
			</s:iterator>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="employees" id="e">
			<tr>
				<td><a href="EmployeeDetail.action?employee.id=<s:property value="#e.id" />"><s:property
					value="#e.lastName" />, <s:property value="#e.firstName" /></a></td>
				<td><a href="ContractorView.action?id=<s:property value="#e.account.id" />">
					<s:property value="#e.account.name" /></a></td>
				<s:iterator value="jobSites.keySet()" id="key">
					<s:iterator value="jobSites.get(#key)" id="jst">
						<td class="center"><s:if test="qualifications.get(#e, #jst.task).qualified">
							<img alt="X" src="images/checkBoxTrue.gif">
						</s:if></td>
					</s:iterator>
				</s:iterator>
			</tr>
		</s:iterator>
	</tbody>
	<tfoot>
		<tr>
			<th colspan="2" class="right">Total</th>
			<s:iterator value="jobSites.keySet()" id="key">
				<s:iterator value="jobSites.get(#key)" id="jst">
					<s:set name="jstTotal" value="0" />
					<s:iterator value="employees" id="e">
						<s:if test="qualifications.get(#e, #jst.task).qualified">
							<s:set name="jstTotal" value="1 + #jstTotal" />
						</s:if>
					</s:iterator>
					<th <s:if test="#jstTotal < getMinimumQualified(employees.size())">class="red"</s:if>>
						<s:property value="#jstTotal" /> of <s:property value="getMinimumQualified(employees.size())" />
					</th>
				</s:iterator>
			</s:iterator>
		</tr>
	</tfoot>
</table>

</body>
</html>
