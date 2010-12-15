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

<s:include value="../reports/filters_employee.jsp" />
<br />

<a href="javascript: download('ReportOQEmployees');" target="_blank" class="excel">Download</a>
<table class="report">
	<thead>
		<tr>
			<th rowspan="2"><a href="?orderBy=e.lastName,e.firstName">Employee</a></th>
			<s:if test="!permissions.contractor">
				<th rowspan="2"><a href="?orderBy=a.name,e.lastName">Company</a></th>
			</s:if>
			<s:iterator value="jobSiteTasks.keySet()" var="js">
				<th colspan="<s:property value="jobSiteTasks.get(#js).size" />">
					<span title="<s:property value="%{#js.operator.name + ': ' + #js.name}" />"><s:property value="#js.label" /></span>
				</th>
			</s:iterator>
		</tr>
		<tr>
			<s:iterator value="jobSiteTasks.keySet()" var="js">
				<s:iterator value="jobSiteTasks.get(#js)" var="jst">
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
				<s:iterator value="jobSiteTasks.keySet()" id="js">
					<s:iterator value="jobSiteTasks.get(#js)" id="jst">
						<td class="center">
							<s:if test="map.get(#e, #jst) != null">
								<s:if test="map.get(#e, #jst) == true">
									<img alt="Qualified" src="images/okCheck.gif" title="<s:iterator value="results.get(#jst.task, #e)" var="ar"><s:property value="#ar.assessmentTest.assessmentCenter.name" />: <s:property value="#ar.assessmentTest.qualificationMethod" /> <s:property value="#ar.assessmentTest.qualificationType" /> - <s:property value="#ar.assessmentTest.description" />, <s:date name="#ar.effectiveDate" format="M/d/yyyy" /> - <s:date name="#ar.expirationDate" format="M/d/yyyy" />

</s:iterator>">
								</s:if>
								<s:else>
									<img alt="Expired" src="images/notOkCheck.gif">
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
			<s:iterator value="jobSiteTasks.keySet()" id="key">
				<s:iterator value="jobSiteTasks.get(#key)" id="jst">
					<s:set name="jstTotal" value="0" />
					<s:iterator value="employees" id="e">
						<s:if test="map.get(#e, #jst) != null && map.get(#e, #jst) == true">
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
