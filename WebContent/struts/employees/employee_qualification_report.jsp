<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="ReportOQEmployees.title" /></title>
<s:include value="../reports/reportHeader.jsp" />
<style type="text/css">
.red {
	background-color: #fbb;
	text-align: center;
}
</style>
<script type="text/javascript">
function orderBy(orderBy) {
	$('#form1').find('input[name="orderBy"]').val(orderBy);
	$('#form1').submit();
	return false;
}

$(function() {
	$('a.excel').live('click', function(e) {
		e.preventDefault;
		download('ReportOQEmployees');
	});
});
</script>
</head>
<body>
<h1><s:text name="ReportOQEmployees.title" /></h1>

<s:include value="../reports/filters_employee.jsp" />

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
	<div class="right">
		<a href="#" target="_blank" class="excel"><s:text name="global.Download" /></a>
	</div>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
	<s:if test="jobSiteTasks.keySet().size == 0">
		<div class="alert"><s:text name="ReportOQEmployees.help.FindNewProjects" /></div>
	</s:if>
	<table class="report">
		<thead>
			<tr>
				<th rowspan="2"><a href="javascript: orderBy('e.lastName,e.firstName')"><s:text name="global.Employee" /></a></th>
				<s:if test="!permissions.contractor">
					<th rowspan="2"><a href="javascript: orderBy('a.name,e.lastName')"><s:text name="global.Company" /></a></th>
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
							<th title="<s:property value="#jst.task.name" />, 
<s:text name="ReportOQEmployees.label.SpanOfControl" /> = <s:property value="#jst.controlSpan" />">
								<s:property value="#jst.task.label" /></th>
						</s:iterator>
					</s:if>
					<s:else>
						<th><span title="<s:text name="ReportOQEmployees.help.NoJobTaskOrCompanies" />"><s:text name="global.NA" /></span></th>
					</s:else>
				</s:iterator>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="employees" id="e">
				<tr>
					<td><a href="EmployeeDetail.action?employee=<s:property value="#e.id" />"><s:property
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
											<a href="#" class="cluetip" 
												rel="#cluetip_<s:property value="#jst.id" />_<s:property value="#e.id" />"
												title="<s:property value="#e.displayName" /> - <s:property value="#jst.task.name" />">
												<img alt="Qualified" src="images/okCheck.gif">
											</a>
											<div id="cluetip_<s:property value="#jst.id" />_<s:property value="#e.id" />">
												<s:iterator value="results.get(#jst.task, #e)" var="ar" status="step">
													<s:property value="#ar.assessmentTest.assessmentCenter.name" />:
													<s:property value="#ar.assessmentTest.qualificationMethod" />
													<s:property value="#ar.assessmentTest.qualificationType" /> -
													<s:property value="#ar.assessmentTest.description" />,
													<s:date name="#ar.effectiveDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" /> -
													<s:date name="#ar.expirationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
													<s:if test="!#step.last">
														<br /><br />
													</s:if>
												</s:iterator>
											</div>
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
				<th <s:if test="!permissions.contractor">colspan="2" </s:if>class="right"><s:text name="ReportOQEmployees.label.Total" /></th>
				<s:iterator value="jobSiteTasks.keySet()" id="key">
					<s:if test="jobSiteTasks.get(#key).size > 0">
						<s:iterator value="jobSiteTasks.get(#key)" id="jst">
							<th <s:if test="getTotalQualified(#jst) < getMinimumQualified(employees.size())">class="red"</s:if>>
								<s:text name="ReportOQEmployees.label.SpanOfControlNumbers">
									<s:param value="%{getTotalQualified(#jst)}" />
									<s:param value="%{getMinimumQualified(employees.size())}" />
								</s:text>
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
