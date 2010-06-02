<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="../reports/reportHeader.jsp" />
</head>
<body>
<h1>OQ Employee</h1>
<div class="beta"></div>
<table class="report">
	<thead>
		<tr>
			<th>Employee</th>
			<s:iterator value="jobSiteTasks">
				<th title="<s:property value="task.name" />
Span of Control = <s:property value="controlSpan" />"><s:property value="task.label" /></th>
			</s:iterator>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="employees" id="e">
			<tr>
				<td><a href="EmployeeDetail.action?employee.id=<s:property value="#e.id" />"><s:property
					value="#e.lastName" />, <s:property value="#e.firstName" /></a></td>
				<s:iterator value="jobSiteTasks" id="jst">
					<td class="center"><s:if test="qualifications.get(#e, #jst.task).qualified">
						<img alt="X" src="images/checkBoxTrue.gif">
					</s:if></td>
				</s:iterator>
			</tr>
		</s:iterator>
	</tbody>
	<tfoot>
		<tr>
			<th>Total</th>
			<s:iterator value="jobSiteTasks" id="jst">
				<s:set name="jstTotal" value="0" />
				<s:iterator value="employees" id="e">
					<s:if test="qualifications.get(#e, #jst.task).qualified">
						<s:set name="jstTotal" value="1 + #jstTotal" />
					</s:if>
				</s:iterator>
				<th><s:property value="#jstTotal" /> of <s:property value="getMinimumQualified(employees.size())" /></th>
			</s:iterator>
		</tr>
	</tfoot>
</table>

</body>
</html>
