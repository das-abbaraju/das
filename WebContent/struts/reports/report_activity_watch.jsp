<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Contractor Activity Watch</title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<style type="text/css">
table.report {
	margin-right: 10px;
}

#search {
	margin-bottom: 10px;
}

#addWatch {
	display: none;
	width: 350px;
}

fieldset.form ol li label {
	width: auto;
}

.leftSide {
	min-width: 400px; 
}

* html .leftSide {
	width: 400px;
}
</style>
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
$(function() {
	$('#newContractor').autocomplete('ContractorSelectAjax.action', 
		{
			minChars: 3,
			extraParams: {'filter.accountName': function() {return $('#newContractor').val();} },
			formatResult: function(data,i,count) { return data[0]; }
		}
	).result(function(event, data){
		$('input#findConID').val(data[1]);
	});
});
</script>
</head>
<body>
<h1>Contractor Activity Watch</h1>
<s:include value="../actionMessages.jsp"></s:include>
<div id="search"><s:form>
	<s:hidden name="conID" />
	<div>
		<button id="searchfilter" type="submit" name="button" value="Search" class="picsbutton positive">Search</button>
	</div>
	<div class="filterOption">
		<s:textfield name="filter.accountName" cssClass="forms" size="17" onfocus="clearText(this)" />
	</div>
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_auditStatus'); return false;">Audit Status</a> =
		<span id="form1_auditStatus_query">ALL</span><br />
		<span id="form1_auditStatus_select" style="display: none" class="clearLink">
			<s:select id="form1_auditStatus" list="filter.auditStatusList" cssClass="forms"
				name="filter.auditStatus" multiple="true" size="5" />
			<script type="text/javascript">updateQuery('form1_auditStatus');</script>
			<br />
			<a class="clearLink" href="#" onclick="clearSelected('form1_auditStatus'); return false;">Clear</a>
		</span>
	</div>
	<br clear="all" />
	<div class="filterOption">
		<s:checkbox value="audits" name="audits" id="audits"></s:checkbox>
		<label for="audits">PQFs, Annual Updates, & Audits</label>
	</div>
	<div class="filterOption">
		<s:checkbox value="flagColorChange" name="flagColorChange" id="flagColorChange"></s:checkbox>
		<label for="flagColorChange">Flag Changes</label>
	</div>
	<div class="filterOption">
		<s:checkbox value="login" name="login" id="login"></s:checkbox>
		<label for="login">User Logins</label>
	</div>
	<div class="filterOption">
		<s:checkbox value="notesAndEmail" name="notesAndEmail" id="notesAndEmail"></s:checkbox>
		<label for="notesAndEmail">Notes & Emails</label>
	</div>
	<div class="filterOption">
		<s:checkbox value="flagCriteria" name="flagCriteria" id="flagCriteria"></s:checkbox>
		<label for="flagCriteria">Flag Criteria</label>
	</div>
	<br clear="all" />
</s:form></div>

<table>
	<tr>
		<s:if test="!permissions.admin">
			<td class="leftSide">
				<s:if test="watched.size() > 0">
					<table class="report">
						<thead>
							<tr>
								<th>Contractor</th>
								<th>Remove</th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="watched" id="watch">
								<tr>
									<td><a href="ContractorView.action?id=<s:property value="contractor.id" />"><s:property value="contractor.name" /></a></td>
									<td class="center"><a href="?button=Remove&watchID=<s:property value="#watch.id" />" onclick="return confirm('Are you sure you want to remove this contractor from the watch list?');" class="remove"></a></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</s:if>
				<s:else>
					<div class="info">This report will show a list of contractor activity for the contractors you have selected to watch. To add a contractor activity watch, click on Add New Contractor below and type in the name of a contractor.</div>
				</s:else>
				<a href="#" id="addLink" onclick="$(this).hide(); $('#addWatch').show(); return false;" class="add">Add New Contractor</a>
				<s:form id="addWatch">
					<input type="hidden" id="findConID" name="conID" value="0" />
					<fieldset class="form">
						<h2 class="formLegend">Add New Contractor</h2>
						<ol>
							<li>
								<label>Contractor Name:</label>
								<s:textfield id="newContractor" />
							</li>
						</ol>
					</fieldset>
					<fieldset class="form submit">
						<input type="submit" value="Add" name="button" class="picsbutton positive" />
						<input type="button" onclick="$('#addWatch').hide(); $('#addLink').show(); return false;" value="Cancel" class="picsbutton negative" />
					</fieldset>
				</s:form>
			</td>
		</s:if>
		<td>
			<s:if test="data.size() > 0">
				<table class="report">
					<thead>
						<tr>
							<th>Contractor</th>
							<th>Activity Details</th>
							<th>Activity Date</th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="data" status="stat">
						<tr>
							<td><s:property value="get('name')" /></td>
							<td>
								<s:if test="get('url').length() > 0"><a href="<s:property value="get('url')" />"><s:property value="get('body')" /></a></s:if>
								<s:else><s:property value="get('body')" /></s:else>
							</td>
							<td><span title="<s:date name="get('activityDate')" nice="true" />"><s:date name="get('activityDate')" format="MM/dd/yyyy HH:mm:ss" /></span></td>
						</tr>
					</s:iterator>
					</tbody>
				</table>
			</s:if>
		</td>
	</tr>
</table>

</body>
</html>
