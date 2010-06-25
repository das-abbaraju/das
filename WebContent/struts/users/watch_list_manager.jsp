<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Watch List Manager</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<style type="text/css">
#addNewForm {
	display: none;
}
fieldset.form {
	width: 50%;
}
</style>
<s:include value="../jquery.jsp" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
function sortTable(sortBy) {
	var tbody = $('table.report').find('tbody');
	var rows = $(tbody).children();
	$(tbody).empty();

	var sortBys = sortBy.split(',');
	rows.sort(function(a, b) {
		var sort1 = 0;
		var index = 0;

		while (sort1 == 0 && index < sortBys.length) {
			sort1 = sort(a, b, sortBys[index]);
			index++;
		}

		return sort1;
	});

	$.each(rows, function (index, row) { $(tbody).append(row); });
}

function sort(a, b, sortBy) {
	var a1 = $(a).find('.' + sortBy).text().toUpperCase();
	var b1 = $(b).find('.' + sortBy).text().toUpperCase();
	return (a1 < b1) ? -1 : (a1 > b1) ? 1 : 0;
}

$(function() {
	$('#addUser').autocomplete('UserSelectAjax.action', 
		{
			minChars: 3,
			extraParams: {'userInfo': function() {return $('#addUser').val();} }
		}
	);

	$('#addContractor').autocomplete('ContractorSelectAjax.action', 
		{
			minChars: 3,
			extraParams: {'filter.accountName': function() {return $('#addContractor').val();} }
		}
	);
});
</script>
</head>
<body>
<h1>Watch List Manager</h1>

<s:include value="../actionMessages.jsp"></s:include>

<table class="report">
	<thead>
		<tr>
			<th></th>
			<th><a href="#" onclick="sortTable('user,contractor'); return false;">User</a></th>
			<th><a href="#" onclick="sortTable('contractor,user'); return false;">Contractor</a></th>
			<pics:permission perm="WatchListManager" type="Delete">
				<th>Remove</th>
			</pics:permission>
		</tr>
	</thead>
	<tbody>
		<s:if test="watchLists.size() > 0">
			<s:iterator value="watchLists" status="stat">
				<tr>
					<td class="id"><s:property value="#stat.count" /></td>
					<td class="user"><a href="UsersManage.action?accountId=<s:property value="user.account.id" />&user.id=<s:property value="user.id" />"><s:property value="user.name" /></a></td>
					<td class="contractor"><a href="ContractorView.action?id=<s:property value="contractor.id" />"><s:property value="contractor.name" /></a></td>
					<pics:permission perm="WatchListManager" type="Delete">
						<td class="center"><a href="?button=Remove&userID=<s:property value="user.id" />&conID=<s:property value="contractor.id" />" onclick="return confirm('Are you sure you want to remove this contractor watch for this user?');" class="remove"></a></td>
					</pics:permission>
				</tr>
			</s:iterator>
		</s:if>
		<s:else>
			<tr><td colspan="<pics:permission perm="WatchListManager" type="Delete">4</pics:permission>
				<pics:permission perm="WatchListManager" type="Delete" negativeCheck="true">3</pics:permission>">
				No users currently using the activity watch.
				</td>
			</tr>
		</s:else>
	</tbody>
</table>

<pics:permission perm="WatchListManager" type="Edit">
	<a href="#" id="addNewLink" onclick="$('#addNewForm').show(); $(this).hide(); return false;" class="add">Add New Contractor Watch</a>
	<s:form id="addNewForm">
		<fieldset class="form bottom">
			<legend><span>Add New Contractor Watch</span></legend>
			<ol>
				<li>
					<label>User:</label>
					<input type="text" id="addUser" name="userName" />
				</li>
				<li>
					<label>Contractor:</label>
					<input type="text" id="addContractor" name="contractorName" />
				</li>
			</ol>
			<div align="center">
				<input type="submit" value="Save" name="button" class="picsbutton positive" />
				<input type="button" value="Cancel" onclick="$('#addNewForm').hide(); $('#addNewLink').show();" class="picsbutton negative" />
			</div>
		</fieldset>
	</s:form>
</pics:permission>

</body>
</html>
