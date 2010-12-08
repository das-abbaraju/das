<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="../reports/reportHeader.jsp" />
<style type="text/css">
.red {
	background-color: #fbb;
}

.assigned {
	background-color: #ffa;
}
</style>
</head>
<body>
<h1>OQ Employee</h1>

<s:include value="../reports/filters_employee.jsp" />
<br />

<a href="ReportOQEmployeesCSV.action" target="_blank" class="excel">Download</a>
<table class="report">
	<thead>
		<tr>
			<th rowspan="2"><a href="?orderBy=e.lastName,e.firstName">Employee</a></th>
			<s:if test="!permissions.contractor">
				<th rowspan="2"><a href="?orderBy=e.account.name,e.lastName">Company</a></th>
			</s:if>
			<s:iterator value="jobSites.keySet()" id="key">
				<th colspan="<s:property value="jobSites.get(#key).size()" />">
					<span title="<s:property value="%{#key.operator.name + ': ' + #key.name}" />"><s:property value="#key.label" /></span>
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
				<s:if test="!permissions.contractor">
					<td>
						<a href="ContractorView.action?id=<s:property value="#e.account.id" />">
							<s:property value="#e.account.name" /></a>
					</td>
				</s:if>
				<s:iterator value="jobSites.keySet()" id="key">
					<s:iterator value="jobSites.get(#key)" id="jst">
						<td class="center<s:if test="assigned.get(#e, #jst)"> assigned</s:if>">
							<s:if test="qualifications.get(#e, #jst.task).qualified && worksAtSite.get(#e, #jst.job)">
								<s:set name="checkMark" value="qualifications.get(#e, #jst.task)" />
								<s:if test="#checkMark.current && !#checkMark.manuallyAdded">
									<img alt="Qualified" src="images/okCheck.gif">
								</s:if>
								<s:else>
									<img alt="Expired" src="images/okCheck.gif">
								</s:else>
							</s:if>
						</td>
					</s:iterator>
				</s:iterator>
			</tr>
		</s:iterator>
	</tbody>
	<tfoot>
		<tr>
			<th <s:if test="!permissions.contractor">colspan="2" </s:if>class="right">Total</th>
			<s:iterator value="jobSites.keySet()" id="key">
				<s:iterator value="jobSites.get(#key)" id="jst">
					<s:set name="jstTotal" value="0" />
					<s:iterator value="employees" id="e">
						<s:if test="qualifications.get(#e, #jst.task).qualified && worksAtSite.get(#e, #jst.job)">
							<s:set name="jstTotal" value="1 + #jstTotal" />
						</s:if>
					</s:iterator>
					<th <s:if test="#jstTotal < getMinimumQualified(employees.size())">class="red"</s:if>>
						<s:property value="#jstTotal" /> of <s:property value="getMinimumQualified(employees.size())" />
					</th>
				</s:iterator>
			</s:iterator>
		</tr>
		<tr>
			<th <s:if test="!permissions.contractor">colspan="2" </s:if>class="right">Assigned</th>
			<s:iterator value="jobSites.keySet()" id="key">
				<s:iterator value="jobSites.get(#key)" id="jst">
					<s:set name="jstTotal" value="0" />
					<s:set name="assignedTotal" value="0" />
					<s:iterator value="employees" id="e">
						<s:if test="qualifications.get(#e, #jst.task).qualified && worksAtSite.get(#e, #jst.job) && assigned.get(#e, #jst)">
							<s:set name="jstTotal" value="1 + #jstTotal" />
						</s:if>
						<s:if test="assigned.get(#e, #jst)">
							<s:set name="assignedTotal" value="1 + #jstTotal" />
						</s:if>
					</s:iterator>
					<th <s:if test="#jstTotal < #assignedTotal">class="red"</s:if>>
						<s:property value="#jstTotal" /> of <s:property value="#assignedTotal" />
					</th>
				</s:iterator>
			</s:iterator>
		</tr>
	</tfoot>
</table>

</body>
</html>
