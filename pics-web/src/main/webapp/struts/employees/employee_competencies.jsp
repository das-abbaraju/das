<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title>
		<s:text name="EmployeeCompetencies.title" />
	</title>
	
	<s:include value="../reports/reportHeader.jsp" />
	<style type="text/css">
		.box
		{
			background-color: #F9F9F9;
		}
		
		.red,
		.green
		{
			text-align: center;
		}
		
		.red
		{
			background-color: #FAA;
		}
		
		.green
		{
			background-color: #AFA;
		}
		
		.selected
		{
			background-color: LightBlue;
			color: #003768;
			font-weight: bold;
			padding: 0;
			text-align: center;
			vertical-align: middle;
		}
		
		table.legend
		{
			clear: both;
			margin: 20px 0px;
		}
		
		table.legend td
		{
			padding: 3px;
			vertical-align: middle;
		}
		
		div.box
		{
			width: 16px;
			height: 16px;
			border: 1px solid #012142;
		}
	</style>
</head>
<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<s:if test="audit.id > 0">
			<div class="info">
				<s:text name="EmployeeCompetencies.Step5">
					<s:param>
						${audit.id}
					</s:param>
					<s:param>
						<s:text name="AuditType.99.name" />
					</s:param>
				</s:text>
			</div>
		</s:if>
		
		<h1>
			${account.name}
			<span class="sub">
				<s:text name="EmployeeCompetencies.title" />
			</span>
		</h1>
		
		<s:set name="saveAccountAndAudit" value="true" />
		<s:include value="../reports/filters_employee.jsp" />
		
		<div class="right">
			<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
				href="javascript: download('EmployeeCompetencies');" title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>">
				<s:text name="global.Download" />
			</a>
		</div>
		
		<table class="legend">
			<tr>
				<td>
					<div class="box green"></div>
				</td>
				<td>
					<s:text name="EmployeeCompetencies.help.Green" />
				</td>
			</tr>
			<tr>
				<td>
					<div class="box red"></div>
				</td>
				<td>
					<s:text name="EmployeeCompetencies.help.Red" />
				</td>
			</tr>
			<tr>
				<td><div class="box"></div></td>
				<td>
					<s:text name="EmployeeCompetencies.help.Blank" />
					<s:if test="permissions.contractor">
						<s:text name="EmployeeCompetencies.help.ContractorLinks" />
					</s:if>
				</td>
			</tr>
		</table>
		
		<a href="javascript:;" class="preview">
			<s:text name="JobCompetencyMatrix.title" />
		</a>
		<a href="javascript:;" class="help cluetip" rel="#matrix_information" title="<s:text name="JobCompetencyMatrix.title" />"></a>
		<div id="matrix_information">
			<s:text name="EmployeeCompetencies.ReviewCompetencies" />
		</div>
		
		<div id="job_competency_matrix" class="hide">
			<s:include value="job_competency_matrix_table.jsp" />
		</div>
		
		<div id="report_data">
			<s:include value="employee_competencies_data.jsp" />
		</div>
	</div>
</body>