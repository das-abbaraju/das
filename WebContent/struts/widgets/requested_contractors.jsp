<%@ taglib prefix="s" uri="/struts-tags"%>
<script type="text/javascript">
function sortTable(sortBy) {
	var tbody = $('#requestedContractor table.report').find('tbody');
	var rows = $(tbody).children();
	$(tbody).empty();

	rows.sort(function(a, b) {
		if (sortBy == 'call') {
			var a1 = new Date($(a).find('.' + sortBy).text());
			var b1 = new Date($(b).find('.' + sortBy).text());
		} else {
			var a1 = $(a).find('.' + sortBy).text().toUpperCase();
			var b1 = $(b).find('.' + sortBy).text().toUpperCase();
		}
		
		return (a1 < b1) ? -1 : (a1 > b1) ? 1 : 0;
	});

	$.each(rows, function (index, row) { $(tbody).append(row); });
}
</script>
<table class="report" id="requestedContractor">
	<thead>
		<tr>
			<td>Requested Contractor</td>
			<td>Requested By</td>
			<td>Deadline</td>
			<td><a href="#" onclick="sortTable('call'); return false;">Last Call Date</a></td>
		</tr>
	</thead>
	<s:iterator value="requestedContractors">
		<tr>
			<td><a href="RequestNewContractor.action?requestID=<s:property value="id"/>"><s:property value="name" /></a></td>
			<td><s:property value="requestedBy.name" /></td>
			<td><nobr><s:property value="maskDateFormat(deadline)"/></nobr></td>
			<td class="call"><nobr><s:property value="maskDateFormat(lastContactDate)"/></nobr></td>
		</tr>
	</s:iterator>
</table>
