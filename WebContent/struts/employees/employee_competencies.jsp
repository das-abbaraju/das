<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
	<head>
		<title>
			<s:text name="EmployeeCompetencies.title" />
		</title>
		
		<s:include value="../reports/reportHeader.jsp" />
		<style type="text/css">
			.box {
				background-color: #F9F9F9;
			}
			.red, .green {
				text-align: center;
			}
			.red {
				background-color: #FAA;
			}
			.green {
				background-color: #AFA;
			}
			table.legend {
				clear: both;
				margin: 20px 0px;
			}
			table.legend td {
				padding: 3px;
				vertical-align: middle;
			}
			div.box {
				width: 16px;
				height: 16px;
				border: 1px solid #012142;
			}
		</style>
	</head>
	<body>
		<s:if test="auditID > 0">
			<div class="info">
				<s:text name="EmployeeCompetencies.Step5">
					<s:param>
						<s:property value="auditID" />
					</s:param>
					<s:param>
						<s:text name="AuditType.99.name" />
					</s:param>
				</s:text>
			</div>
		</s:if>
		
		<h1>
			<s:property value="account.name" />
			<span class="sub">
				<s:text name="EmployeeCompetencies.title" />
			</span>
		</h1>
		
		<s:include value="../reports/filters_employee.jsp" />
		
		<div class="right">
			<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
				href="javascript: download('EmployeeCompetencies');" title="<s:text name="javascript.DownloadAllRows"><s:param value="report.allRows" /></s:text>">
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
		
		<div id="report_data">
			<s:include value="employee_competencies_data.jsp" />
		</div>
	
		<script type="text/javascript">
			$(function() {
				$('#report_data').delegate('input[type=checkbox]', 'click', function(e) {
					var checkbox = $(this);
					var checked = checkbox.is(":checked");
					var ids = $(this).attr('id').split('_');
					
					var data = {
						employee: ids[0],
						competency: ids[1],
						skilled: checked
					};
					
			
					$("#messages").load('EmployeeCompetencies!changeCompetency.action', data, function(r, status, xhr) {
						if (status == "success")
							checkbox.closest("td").removeClass('green').removeClass('red').addClass(checked ? 'green' : 'red');
						else
							checkbox.attr('checked', !checked);
					});
				});
			});
		</script>
	</body>
</html>