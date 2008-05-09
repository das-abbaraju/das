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
<div id="showSearch"><a href="#" onclick="showSearch()">Show Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<s:form id="form1" method="post" cssStyle="display: none">
	<table>
	<tr>
		<td style="vertical-align: middle;"><s:textfield name="accountName" cssClass="forms" size="8" onfocus="clearText(this)"  />
			<s:select list="auditTypeList" cssClass="forms" name="auditTypeID" listKey="auditTypeID" listValue="auditName" />
			<s:select list="auditStatusList" cssClass="forms" name="auditStatus" />
			<s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch( 'form1')" />
			</td></tr>
			<tr><td><s:if test="%{value = !permissions.operator}">
			<s:select list="operatorList" cssClass="forms" name="operator" listKey="id" listValue="name" />
			</s:if>
			<s:action name="AuditorsGet" executeResult="true">
				<s:param name="controlName" value="%{'auditorId'}"/>
				<s:param name="presetValue" value="auditorId"/>		
			</s:action>
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
	    <td><a href="?orderBy=at.auditName DESC" >Type</a></td>
	    <td><a href="?orderBy=ca.createdDate DESC" >Created</a></td>
	    <td><a href="?orderBy=ca.auditStatus DESC" >Status</a></td>
	    <td><a href="?orderBy=ca.percentComplete" >Comp%</a></td>
	    <td><a href="?orderBy=ca.percentVerified" >Ver%</a></td>
	    <td><a href="?orderBy=au.name" >Auditor</a></td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>" 
			><s:property value="[0].get('name')"/></a>
		</td>
		<td><s:property value="[0].get('auditName')"/></td>
		<td class="center"><s:date name="[0].get('createdDate')" format="M/d/yy" /></td>
		<td><s:property value="[0].get('auditStatus')"/></td>
		<td class="right"><s:property value="[0].get('percentComplete')"/>%</td>
		<td class="right"><s:property value="[0].get('percentVerified')"/>%</td>
		<td><s:property value="[0].get('auditor_name')"/></td>
	</tr>
	</s:iterator>
</table>
<center>
<s:property value="report.pageLinksWithDynamicForm" escape="false"/>
</center>

</body>
</html>
