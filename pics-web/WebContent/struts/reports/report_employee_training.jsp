<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>
			<s:text name="ReportEmployeeTraining.title" />
		</title>
		<s:include value="reportHeader.jsp" />
	</head>
	<body>
		<h1>
			<s:text name="ReportEmployeeTraining.title" />
		</h1>
		
		<s:include value="../actionMessages.jsp" />
		
		<s:include value="filters_employee.jsp" />
		<br clear="all" />
		
		<table class="report">
			<thead>
				<tr>
					<th></th>
					<th>
						<s:text name="global.Company" />
					</th>
					<th>
						<s:text name="Employee" />
					</th>
					<th>
						<s:text name="ReportEmployeeTraining.Training" />
					</th>
					<th>
						<s:text name="global.Date" />
					</th>
					<th>
						<s:text name="global.ExpirationDate" />
					</th>
					<th>
						<s:text name="global.Status" />
					</th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="employees" status="stat" begin="%{(page - 1) * 100}" end="%{end}">
					<tr>
						<td class="right">
							<s:property value="#stat.index + ((page - 1) * 100) + 1" />
						</td>
						<td>
							<a href="ContractorView.action?id=<s:property value="employee.account.id" />">
								<s:property value="employee.account.name" />
							</a>
						</td>
						<td>
							<a href="EmployeeDetail.action?employee=<s:property value="employee.id" />">
								<s:property value="employee.lastName" />, <s:property value="employee.firstName" />
							</a>
						</td>
						<td>
							<s:property value="training" />
						</td>
						<td>
							<s:date name="completed" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
						</td>
						<td>
							<s:date name="expiration" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
						</td>
						<td class="center">
							<s:if test="complete">
								<img src="images/okCheck.gif" alt="<s:text name="AuditStatus.Complete" />" />
							</s:if>
							<s:else>
								<img src="images/notOkCheck.gif" alt="<s:text name="AuditStatus.Complete" />" />
							</s:else>
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</body>
</html>
