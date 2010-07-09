<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="../exception_handler.jsp"%>
<html>
<head>
<title>Assessments</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>

<script type="text/javascript">
function getCenter(id) {
	self.location = "?id=" + <s:property value="account.id" /> + "&employeeID="
		+ <s:property value="employeeID" /> + "&centerID=" + id;
}

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
</script>
</head>
<body>

<h1><s:property value="account.name" /><span class="sub">
<s:if test="subHeading.length() > 0">
	<s:property value="subHeading" escape="false" />
</s:if>
</span></h1>

<div style="margin: 10px 0px;">
	Assessment Center:
	<s:select list="allAssessmentCenters" name="centerID" listKey="id" listValue="name" value="centerID" 
		headerKey="0" headerValue="- Assessment Center -" onchange="getCenter(this.value);"></s:select>
</div>

<s:if test="effective.size() > 0">
	<table class="report" id="effective"><thead>
		<tr>
			<th></th>
			<th><a href="#" onclick="sortTable('result,description,employee', 'effective'); return false;">Assessment Results</a></th>
			<th><a href="#" onclick="sortTable('description,result,employee', 'effective'); return false;">Test Description</a></th>
			<s:if test="employee == null">
				<th><a href="#" onclick="sortTable('employee,result,description', 'effective'); return false;">Employee</a></th>
			</s:if>
			<s:if test="canEdit">
				<th>Remove</th>
			</s:if>
		</tr>
	</thead><tbody>
		<s:iterator value="effective" id="result" status="stat">
			<tr>
				<td class="id"><s:property value="#stat.count" /></td>
				<td class="result">
					<s:property value="#result.assessmentTest.assessmentCenter.name" />:
					<s:property value="#result.assessmentTest.qualificationType" /> -
					<s:property value="#result.assessmentTest.qualificationMethod" />
				</td>
				<td class="description"><s:property value="#result.assessmentTest.description" /></td>
				<s:if test="employeeID == 0">
					<td class="employee">
						<a href="?id=<s:property value="account.id" />&employeeID=<s:property value="#result.employee.id" />&centerID=<s:property value="centerID" />">
							<s:property value="#result.employee.displayName" />
						</a>
					</td>
				</s:if>
				<s:if test="canEdit">
					<td class="center">
						<a href="?id=<s:property value="account.id" />&button=Remove&resultID=<s:property value="#result.id" />"
							onclick="return confirm('Are you sure you want to expire this assessment result?');"
							class="remove"></a>
					</td>
				</s:if>
			</tr>
		</s:iterator>
	</tbody></table>
	<br />
</s:if>
<s:else>
	<s:if test="employeeID > 0">
		<div class="info">This employee has no assessments associated or in effect<s:if test="centerID > 0"> with <s:property value="assessmentCenter.name" /></s:if>.</div>
	</s:if>
	<s:elseif test="centerID > 0">
		<div class="info">There are no employees with assessment results from <s:property value="assessmentCenter.name" />.</div>
	</s:elseif>
</s:else>

<s:if test="expired.size() > 0">
	<h3>Expired</h3>
	<table class="report" id="expired"><thead>
		<tr>
			<th><a href="#" onclick="sortTable('results,description,employee,%date', 'expired'); return false;">Assessment Results</a></th>
			<th><a href="#" onclick="sortTable('description,results,employee,%date', 'expired'); return false;">Test Description</a></th>
			<s:if test="employeeID == 0">
				<th><a href="#" onclick="sortTable('employee,results,description,%date', 'expired'); return false;">Employee</a></th>
			</s:if>
			<th><a href="#" onclick="sortTable('%date,results,description,employee', 'expired'); return false;">Expiration Date</a></th>
		</tr>
	</thead><tbody>
		<s:iterator value="expired" id="result" status="stat">
			<tr>
				<td class="results">
					<s:property value="#result.assessmentTest.assessmentCenter.name" />:
					<s:property value="#result.assessmentTest.qualificationType" /> -
					<s:property value="#result.assessmentTest.qualificationMethod" />
				</td>
				<td class="description"><s:property value="#result.assessmentTest.description" /></td>
				<s:if test="employeeID == 0">
					<td class="employee">
						<a href="?id=<s:property value="account.id" />&employeeID=<s:property value="#result.employee.id" />&centerID=<s:property value="centerID" />">
							<s:property value="#result.employee.lastName" />, <s:property value="#result.employee.firstName" />
						</a>
					</td>
				</s:if>
				<td class="center date"><s:date name="#result.expirationDate" format="MM/dd/yyyy" /></td>
			</tr>
		</s:iterator>
	</tbody></table>
</s:if>

<s:if test="employeeID > 0 || centerID > 0">
	<a href="?id=<s:property value="account.id" />">View All Employees and Centers</a>
</s:if>

</body>
</html>
