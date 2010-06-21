<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><s:property value="subHeading" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=<s:property value="version"/>" />
<s:include value="../reportHeader.jsp" />
<script type="text/javascript">
function sortTable(sortBy) {
	var tbody = $('table.report').find('tbody');
	var rows = $(tbody).children();
	$(tbody).empty();

	var sortBys = sortBy.split(',');
	rows.sort(function(a, b) {
		var sort1 = sort(a, b, sortBys[0]);
		var count = 0;

		while (sort1 == 0 && count < sortBys.length) {
			sort1 = sort(a, b, sortBys[count]);
			count++;
		}

		return sort1;
	});

	$.each(rows, function (index, row) { $(row).find('.index').text(index + 1); $(tbody).append(row); });
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

<s:include value="assessmentHeader.jsp" />

<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Result ID</th>
			<th><a href="#" onclick="sortTable('qtype,qmethod'); return false;">Qualification Type</a></th>
			<th><a href="#" onclick="sortTable('qmethod,qtype'); return false;">Qualification Method</a></th>
			<th>Description</th>
			<th><a href="#" onclick="sortTable('#test'); return false;">Test ID</a></th>
			<th colspan="2"><a href="#" onclick="sortTable('name,qtype,qmethod'); return false;">Employee</a></th>
			<th>Employee ID</th>
			<th><a href="#" onclick="sortTable('company,qtype,qmethod'); return false;">Company</a></th>
			<th>Company ID</th>
			<th><a href="#" onclick="sortTable('%date,name'); return false;">Qualification Date</a></th>
			<th>Remove</th>
		</tr>
	</thead>
	<tbody>	
		<s:iterator value="staged" status="stat">
			<tr>
				<td class="right index"><s:property value="#stat.count" /></td>
				<td><s:property value="resultID" /></td>
				<td class="qtype"><s:property value="qualificationType" /></td>
				<td class="qmethod"><s:property value="qualificationMethod" /></td>
				<td><s:property value="description" /></td>
				<td class="right test"><s:property value="testID" /></td>
				<td><s:property value="firstName" /></td>
				<td class="name"><s:property value="lastName" /></td>
				<td><s:property value="employeeID" /></td>
				<td class="company"><s:property value="companyName" /></td>
				<td class="right"><s:property value="companyID" /></td>
				<td class="center date"><s:date name="qualificationDate" format="MM/dd/yyyy" /></td>
				<td class="center"><a href="?id=<s:property value="center.id" />&button=Remove&stageID=<s:property value="id" />" class="remove"
					onclick="return confirm('Are you sure you want to remove this test result?');"></a></td>
			</tr>
		</s:iterator>
		<s:if test="staged.size() == 0">
			<tr>
				<td colspan="13">No results found</td>
			</tr>
		</s:if>
	</tbody>
</table>

</body>
</html>