<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="WatchListManager.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<style type="text/css">
#addNewForm {
	display: none;
}
fieldset.form {
	width: 50%;
}
</style>
<s:include value="../jquery.jsp" />
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
			extraParams: {'userInfo': function() {return $('#addUser').val();} },
			formatResult: function(data,i,count) { return data[0]; }
		}
	).result(function(event, data){
		$('input#userID').val(data[1]);
	});

	$('#addContractor').autocomplete('ContractorSelectAjax.action', 
		{
			minChars: 3,
			extraParams: {'filter.accountName': function() {return $('#addContractor').val();} },
			formatResult: function(data,i,count) { return data[0]; }
		}
	).result(function(event, data){
		$('input#conID').val(data[1]);
	});

	$('#addUser').blur(function() {
		if ($('#addUser').val() == '')
			$('input#userID').val(0);
	});

	$('#addContractor').blur(function() {
		if ($('#addContractor').val() == '')
			$('input#conID').val(0);
	});
	
	$('a.remove').live('click', function(e) {
		return confirm(translate('JS.WatchListManager.confirm.RemoveContractorWatch'));
	});
	
	$('#addNewLink').live('click', function(e) {
		e.preventDefault();
		$('#addNewForm').show();
		$(this).hide();
	});
	
	$('.cancelButton').live('click', function(e) {
		e.preventDefault();
		$('#addNewForm').hide();
		$('#addNewLink').show();
	});
	
	$('#sortUser').live('click', function(e) {
		e.preventDefault();
		sortTable('user,contractor');
	});
	
	$('#sortContractor').live('click', function(e) {
		e.preventDefault();
		sortTable('contractor,user');
	});
});
</script>
</head>
<body>
<h1><s:text name="WatchListManager.title" /></h1>

<s:include value="../actionMessages.jsp"></s:include>

<table class="report">
	<thead>
		<tr>
			<th></th>
			<th><a href="#" id="sortUser"><s:text name="User" /></a></th>
			<th><a href="#" id="sortContractor"><s:text name="global.Contractor" /></a></th>
			<th><s:text name="global.Flag" /></a></th>
			<pics:permission perm="WatchListManager" type="Delete">
				<th><s:text name="button.Remove" /></th>
			</pics:permission>
		</tr>
	</thead>
	<tbody>
		<s:if test="watchLists.size() > 0">
			<s:iterator value="watchLists" status="stat">
				<tr>
					<td class="id"><s:property value="#stat.count" /></td>
					<td class="user"><a href="UsersManage.action?account=<s:property value="user.account.id" />&user=<s:property value="user.id" />"><s:property value="user.name" /></a></td>
					<td class="contractor"><a href="ContractorView.action?id=<s:property value="contractor.id" />"><s:property value="contractor.name" /></a></td>
					<td class="flagColor center">
						<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="user.account.id" />">
							<s:property value="%{getCoFlag(contractor.id,user.account.id).smallIcon}" escape="false"/>
						</a>
					</td>
					<pics:permission perm="WatchListManager" type="Delete">
						<td class="center"><a href="WatchListManager!remove.action?user=<s:property value="user.id" />&contractor=<s:property value="contractor.id" />" class="remove"></a></td>
					</pics:permission>
				</tr>
			</s:iterator>
		</s:if>
		<s:else>
			<tr>
				<td colspan="<pics:permission perm="WatchListManager" type="Delete">4</pics:permission><pics:permission perm="WatchListManager" type="Delete" negativeCheck="true">3</pics:permission>">
					<s:text name="WatchListManager.message.NoActivityWatchUsers" />
				</td>
			</tr>
		</s:else>
	</tbody>
</table>

<pics:permission perm="WatchListManager" type="Edit">
	<a href="#" id="addNewLink" class="add"><s:text name="WatchListManager.link.AddNewContractorWatch" /></a>
	<s:form id="addNewForm">
		<s:hidden name="user" id="userID" />
		<s:hidden name="contractor" id="conID" />
		<fieldset class="form">
			<h2 class="formLegend"><s:text name="WatchListManager.link.AddNewContractorWatch" /></h2>
			<ol>
				<li>
					<label><s:text name="User" />:</label>
					<input type="text" id="addUser" />
				</li>
				<li>
					<label><s:text name="global.Contractor" />:</label>
					<input type="text" id="addContractor" />
				</li>
			</ol>
		</fieldset>
		<fieldset class="form submit">
			<s:submit method="save" value="%{getText('button.Save')}" cssClass="picsbutton positive" />
			<input type="button" value="<s:text name="button.Cancel" />" class="picsbutton cancelButton" />
		</fieldset>
	</s:form>
</pics:permission>

</body>
</html>
