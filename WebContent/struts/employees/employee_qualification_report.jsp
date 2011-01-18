<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>OQ by Employee</title>
<s:include value="../reports/reportHeader.jsp" />
<style type="text/css">
.red {
	background-color: #fbb;
	text-align: center;
}
</style>
<script type="text/javascript">
function orderBy(orderBy) {
	$('#form1').find('input[name=orderBy]').val(orderBy);
	$('#form1').submit();
	return false;
}
</script>
</head>
<body>
<h1>OQ by Employee</h1>

<s:include value="../reports/filters_employee.jsp" />

<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>
	<div class="right">
		<a href="#" onclick="download('ReportOQEmployees'); return false;" target="_blank" class="excel">Download</a>
	</div>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
	<table class="report">
		<thead>
			<tr>
				<th rowspan="2"><a href="javascript: orderBy('e.lastName,e.firstName')">Employee</a></th>
				<s:if test="!permissions.contractor">
					<th rowspan="2"><a href="javascript: orderBy('a.name,e.lastName')">Company</a></th>
				</s:if>
				<s:iterator value="jobSiteTasks.keySet()" var="js">
					<th colspan="<s:property value="jobSiteTasks.get(#js).size" />">
						<s:if test="permissions.contractor"><s:property value="#js.operator.name" />: </s:if><s:property value="#js.name" />
					</th>
				</s:iterator>
			</tr>
			<tr>
				<s:iterator value="jobSiteTasks.keySet()" var="js">
					<s:if test="jobSiteTasks.get(#js).size > 0">
						<s:iterator value="jobSiteTasks.get(#js)" var="jst">
							<th title="<s:property value="#jst.task.name" />
Span of Control = <s:property value="#jst.controlSpan" />">
								<s:property value="#jst.task.label" /></th>
						</s:iterator>
					</s:if>
					<s:else>
						<th><span title="There are no job tasks or companies associated with this project">N/A</span></th>
					</s:else>
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
							<s:if test="#e.account.contractor">
								<a href="ContractorView.action?id=<s:property value="#e.account.id" />">
									<s:property value="#e.account.name" /></a>
							</s:if>
							<s:else>
								<s:property value="#e.account.name" />
							</s:else>
						</td>
					</s:if>
					<s:iterator value="jobSiteTasks.keySet()" id="js">
						<s:if test="jobSiteTasks.get(#js).size > 0">
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
						</s:if>
						<s:else>
							<td></td>
						</s:else>
					</s:iterator>
				</tr>
			</s:iterator>
		</tbody>
		<tfoot>
			<tr>
				<th <s:if test="!permissions.contractor">colspan="2" </s:if>class="right">Total</th>
				<s:iterator value="jobSiteTasks.keySet()" id="key">
					<s:if test="jobSiteTasks.get(#key).size > 0">
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
					</s:if>
					<s:else>
						<th></th>
					</s:else>
				</s:iterator>
			</tr>
		</tfoot>
	</table>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:else>
</body>
</html>
