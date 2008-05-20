<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Audit List</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects"
	type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Audit List</h1>

<div id="search">
<div id="showSearch" onclick="showSearch()" <s:if test="filtered">style="display: none"</s:if> ><a href="#">Show Filter Options</a></div>
<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if> ><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<s:form id="form1" method="post" cssStyle="%{filtered ? '' : 'display: none'}">
	<table>
	<tr>
		<td style="vertical-align: middle;"><s:textfield name="accountName" cssClass="forms" size="8" onfocus="clearText(this)"  />
			<s:select list="auditTypeList" cssClass="forms" name="auditTypeID" listKey="auditTypeID" listValue="auditName" />
			<s:select list="auditStatusList" cssClass="forms" name="auditStatus" />
			<s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch( 'form1')" />
			</td></tr>
			<tr><td><s:if test="%{value = (!permissions.operator && !permissions.corporate)}">
			<s:select list="operatorList" cssClass="forms" name="operator" listKey="id" listValue="name" />
			<s:action name="AuditorsGet" executeResult="true">
				<s:param name="controlName" value="%{'auditorId'}"/>
				<s:param name="presetValue" value="auditorId"/>		
			</s:action>
			</s:if>
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
