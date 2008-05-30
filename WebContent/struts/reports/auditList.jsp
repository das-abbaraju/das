<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Audit List</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects"
	type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript">
var currentUser = 0;

function toggleBox(name) {
	var box = $(name);
	var result = $(name+'_query');
	result.hide();
	box.toggle();
	if (box.visible())
		return;

	updateQuery(name);
	result.show();
}

function updateQuery(name) {
	var box = $(name);
	var result = $(name+'_query');
	var queryText = '';
	var values = $F(box);
	for(i=0; i < box.length; i++) {
		if (box.options[i].selected) {
			if (queryText != '') queryText = queryText + ", ";
			queryText = queryText + box.options[i].text;
		}
	}
	
	if (queryText == '') {
		queryText = 'ALL';
	}
	result.update(queryText);
}

</script>
<style>
div.filterOption {
	margin: 0px;
	padding: 0px;
	float: left;
}
</style>
</head>
<body>
<h1>Audit List</h1>

<div id="search">
<div id="showSearch" onclick="showSearch()" <s:if test="filtered">style="display: none"</s:if> ><a href="#">Show Filter Options</a></div>
<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if> ><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<s:form id="form1" method="post" cssStyle="background-color: #F4F4F4; %{filtered ? '' : 'display: none;'}">
	<table width="100%">
	<tr>
		<td>
			<div class="filterOption"><s:textfield name="accountName" cssClass="forms" size="8" onfocus="clearText(this)"  /></div>
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_auditTypeID'); return false;">Audit Type</a> =
				<span id="form1_auditTypeID_query">Current selected options here</span><br />
				<s:select list="auditTypeList" cssClass="forms" name="auditTypeID" listKey="auditTypeID" listValue="auditName" multiple="true" size="5" cssStyle="display: none"/></div>
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_auditStatus'); return false;">Audit Status</a> =
				<span id="form1_auditStatus_query">Current selected options here</span><br />
				<s:select list="auditStatusList" cssClass="forms" name="auditStatus" multiple="true" size="5" cssStyle="display: none"/></div>
		<s:if test="%{value = (!permissions.operator && !permissions.corporate)}">
			<div class="filterOption">
				<a href="#" onclick="toggleBox('auditorId'); return false;">Auditors</a> =
				<span id="auditorId_query">Current selected options here</span><br />
				<s:action name="AuditorsGet" executeResult="true">
					<s:param name="controlName" value="%{'auditorId'}"/>
					<s:param name="presetValue" value="auditorId"/>
				</s:action>
			</div>
		</s:if>
		<s:if test="!permissions.operator">
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_operator'); return false;">Operators</a> =
				<span id="form1_operator_query">Current selected options here</span><br />
				<s:select list="operatorList" cssClass="forms" name="operator" listKey="id" listValue="name" multiple="true" size="5" cssStyle="display: none"/></div>
		</s:if>
		</td>
		<td style="padding: 10px;">
			<s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch( 'form1')" />
		</td>
	</tr>
	</table>
	<s:hidden name="showPage" value="1"/>
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
	<div class="alphapaging">
	<s:property value="report.startsWithLinksWithDynamicForm" escape="false" />
	</div>
</s:form>
</div>

<script type="text/javascript">
updateQuery('form1_auditTypeID');
updateQuery('form1_auditStatus');
updateQuery('form1_operator');
updateQuery('auditorId');
</script>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="?orderBy=a.name" >Contractor</a></th>
	    <td><a href="?orderBy=atype.auditName" >Type</a></td>
	    <td><a href="?orderBy=ca.createdDate DESC" >Created</a></td>
	    <td><a href="?orderBy=ca.auditStatus DESC" >Status</a></td>
	    <td><a href="?orderBy=ca.percentComplete" >Comp%</a></td>
	    <td><a href="?orderBy=ca.percentVerified" >Ver%</a></td>
	    <s:if test="%{value = (!permissions.operator && !permissions.corporate)}">
	    <td><a href="?orderBy=auditor.name" >Auditor</a></td>
	    </s:if>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a></td>
		<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a></td>
		<td class="center"><s:date name="[0].get('createdDate')" format="M/d/yy" /></td>
		<td><s:property value="[0].get('auditStatus')"/></td>
		<td class="right"><s:property value="[0].get('percentComplete')"/>%</td>
		<td class="right"><s:property value="[0].get('percentVerified')"/>%</td>
	    <s:if test="%{value = (!permissions.operator && !permissions.corporate)}">
		<td><s:property value="[0].get('auditor_name')"/></td>
		</s:if>
	</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
