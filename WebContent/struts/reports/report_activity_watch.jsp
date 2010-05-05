<%@ taglib prefix="s" uri="/struts-tags"%>
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
</style>
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
$(function() {
	$('#newContractor').autocomplete('ContractorSelectAjax.action', 
		{
			minChars: 3,
			extraParams: {'filter.accountName': function() {return $('#newContractor').val();} }
		}
	);
});
</script>
</head>
<body>
<h1>Contractor Activity Watch</h1>
<s:include value="../actionMessages.jsp"></s:include>
<div id="search"><s:form>
	<div>
		<button id="searchfilter" type="submit" name="button" value="Search" class="picsbutton positive">Search</button>
	</div>
	<div class="filterOption">
		<s:textfield name="filter.accountName" cssClass="forms" size="10" onfocus="clearText(this)" />
	</div>
	<div class="filterOption">
		<s:checkbox value="auditExpiration" name="auditExpiration" id="auditExpiration"></s:checkbox>
		<label for="auditExpiration">Expired Audits</label>
	</div>
	<div class="filterOption">
		<s:checkbox value="auditSubmitted" name="auditSubmitted" id="auditSubmitted"></s:checkbox>
		<label for="auditSubmitted">Submitted Audits</label>
	</div>
	<div class="filterOption">
		<s:checkbox value="auditActivated" name="auditActivated" id="auditActivated"></s:checkbox>
		<label for="auditActivated">Activated Audits</label>
	</div>
	<div class="filterOption">
		<s:checkbox value="flagColorChange" name="flagColorChange" id="flagColorChange"></s:checkbox>
		<label for="flagColorChange">Flag Color Changed</label>
	</div>
	<div class="filterOption">
		<s:checkbox value="login" name="login" id="login"></s:checkbox>
		<label for="login">Last Logged In</label>
	</div>
	<div class="filterOption">
		<s:checkbox value="note" name="note" id="note"></s:checkbox>
		<label for="note">Notes Posted</label>
	</div>
	<br clear="all" />
</s:form></div>

<table>
	<tr>
		<td>
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
								<td><a href="?conID=<s:property value="contractor.id" />"><s:property value="contractor.name" /></a></td>
								<td class="center"><a href="?button=Remove&watchID=<s:property value="#watch.id" />" onclick="return confirm('Are you sure you want to remove this contractor from this watch list?');" class="remove"></a></td>
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
				<fieldset class="form">
					<ol>
						<li>
							<label>Contractor Name:</label>
							<s:textfield name="conName" id="newContractor" />
						</li>
					</ol>
					<div style="text-align: center;">
						<input type="submit" value="Add" name="button" class="picsbutton positive" />
						<input type="button" onclick="$('#addWatch').hide(); $('#addLink').show(); return false;" value="Cancel" class="picsbutton negative" />
					</div>
				</fieldset>
			</s:form>
			<s:if test="conID > 0"><br /><a href="ReportActivityWatch.action">View All Contractors</a></s:if>
		</td><td>
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
