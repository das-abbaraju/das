<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="EmployeeAssessmentResults.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>

<script type="text/javascript">
function sortTable(sortBys, tableName) {
	if (tableName == null)
		tableName = "table.report";
	else
		tableName = "table#" + tableName;
	
	var rows = $(tableName + " tbody").children();
	$(tableName + " tbody").empty();

	var sorts = sortBys.split(',');
	rows.sort(function(a, b) {
		var count = 0;

		var result = sort(a, b, sorts[count]);
		while (result == 0 && count < sorts.length) {
			count++;
			result = sort(a, b, sorts[count]);
		}

		return result;
	});

	$.each(rows, function(index, row) { $(row).find('.id').text(index + 1); $(tableName + ' tbody').append(row); });
}

function sort(a, b, sortBy) {
	var a1;
	var b1;
	
	if (sortBy.indexOf("#") >= 0) {
		a1 = new Number($(a).find('.' + sortBy.substr(1)).text());
		b1 = new Number($(b).find('.' + sortBy.substr(1)).text());
	} else if (sortBy.indexOf("%") >= 0) {
		a1 = new Date($(a).find('.' + sortBy.substr(1)).text());
		b1 = new Date($(b).find('.' + sortBy.substr(1)).text());
	} else {
		a1 = $(a).find('.' + sortBy).text().toUpperCase();
		b1 = $(b).find('.' + sortBy).text().toUpperCase();
	}

	return a1 > b1 ? 1 : a1 < b1 ? -1 : 0;
}

$(function() {
	$('#effective').delegate('.sortColumn', 'click', function(e) {
		e.preventDefault();
		var ordering = $(this).attr('rel');
		var table = $(this).closest('table').attr('id');
		sortTable(ordering, table);
	});
});
</script>
</head>
<body>

<h1><s:property value="account.name" /><span class="sub"><s:property value="subHeading" escape="false" /></span></h1>

<s:if test="effective.size > 0">
	<table class="report" id="effective">
		<thead>
			<tr>
				<th></th>
				<th>
					<a href="#" class="sortColumn" rel="assessmentCenter,employee,result">
						<s:text name="global.AssessmentCenter" />
					</a>
				</th>
				<th>
					<a href="#" class="sortColumn" rel="result,description,employee">
						<s:text name="EmployeeAssessmentResults.header.AssessmentResults" />
					</a>
				</th>
				<th>
					<a href="#" class="sortColumn" rel="description,result,employee">
						<s:text name="EmployeeAssessmentResults.header.TestDescription" />
					</a>
				</th>
				<th>
					<a href="#" class="sortColumn" rel="employee,result,description">
						<s:text name="global.Employee" />
					</a>
				</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="effective" id="result" status="stat">
				<tr>
					<td class="id"><s:property value="#stat.count" /></td>
					<td class="assessmentCenter">
						<a href="?account=<s:property value="account.id" />&assessmentCenter=<s:property value="#result.assessmentTest.assessmentCenter.id" />">
							<s:property value="#result.assessmentTest.assessmentCenter.name" />
						</a>
					</td>
					<td class="result">
						<s:property value="#result.assessmentTest.qualificationType" /> -
						<s:property value="#result.assessmentTest.qualificationMethod" />
					</td>
					<td class="description"><s:property value="#result.assessmentTest.description" /></td>
					<td class="employee">
						<a href="?account=<s:property value="account.id" />&employee=<s:property value="#result.employee.id" />">
							<s:property value="#result.employee.displayName" />
						</a>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	<br />
</s:if>
<s:else>
	<s:if test="employee != null">
		<div class="info">
			<s:text name="EmployeeAssessmentResults.message.EmployeeHasNoAssessments">
				<s:param value="%{assessmentCenter != null ? 1 : 0}" />
				<s:param value="%{assessmentCenter.name}" />
			</s:text>
		</div>
	</s:if>
	<s:elseif test="assessmentCenter != null">
		<div class="info">
			<s:text name="EmployeeAssessmentResults.message.NoResultsFromAssessmentCenter">
				<s:param value="%{assessmentCenter.name}" />
			</s:text>
		</div>
	</s:elseif>
</s:else>

<s:if test="expired.size > 0">
	<h3><s:text name="AuditStatus.Expired" /></h3>
	<table class="report" id="expired"><thead>
		<tr>
			<th>
				<a href="#" class="sortColumn" rel="assessmentCenter,employee,%date">
					<s:text name="global.AssessmentCenter" />
				</a>
			</th>
			<th>
				<a href="#" class="sortColumn" rel="results,description,employee,%date">
					<s:text name="EmployeeAssessmentResults.header.AssessmentResults" />
				</a>
			</th>
			<th>
				<a href="#" class="sortColumn" rel="description,results,employee,%date">
					<s:text name="EmployeeAssessmentResults.header.TestDescription" />
				</a>
			</th>
			<th>
				<a href="#" class="sortColumn" rel="employee,results,description,%date">
					<s:text name="global.Employee" />
				</a>
			</th>
			<th>
				<a href="#" class="sortColumn" rel="%date,results,description,employee">
					<s:text name="AssessmentResult.expirationDate" />
				</a>
			</th>
		</tr>
	</thead><tbody>
		<s:iterator value="expired" id="result" status="stat">
			<tr>
				<td class="assessmentCenter">
					<a href="?account=<s:property value="account.id" />&assessmentCenter=<s:property value="#result.assessmentTest.assessmentCenter.id" />">
						<s:property value="#result.assessmentTest.assessmentCenter.name" />
					</a>
				</td>
				<td class="results">
					<s:property value="#result.assessmentTest.qualificationType" /> -
					<s:property value="#result.assessmentTest.qualificationMethod" />
				</td>
				<td class="description"><s:property value="#result.assessmentTest.description" /></td>
				<td class="employee">
					<a href="?account=<s:property value="account.id" />&employee=<s:property value="#result.employee.id" />">
						<s:property value="#result.employee.lastName" />, <s:property value="#result.employee.firstName" />
					</a>
				</td>
				<td class="center date"><s:date name="#result.expirationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" /></td>
			</tr>
		</s:iterator>
	</tbody></table>
</s:if>

<s:if test="employee != null || assessmentCenter != null">
	<a href="?account=<s:property value="account.id" />" class="preview"><s:text name="EmployeeAssessmentResults.link.ViewAll" /></a>
</s:if>

</body>
</html>