<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>My Audit History</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>My Audit History</h1>

<div id="search">
<div id="showSearch" onclick="showSearch()" <s:if test="filtered">style="display: none"</s:if> ><a href="#">Show Filter Options</a></div>
<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if> ><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<s:form id="form1" method="post" cssStyle="%{filtered ? '' : 'display: none'}">
	<table>
	<tr>
		<td style="vertical-align: middle;"><s:textfield name="accountName" cssClass="forms" size="8" onfocus="clearText(this)"  />
			<s:select list="auditTypeList" cssClass="forms" name="auditTypeID" listKey="auditTypeID" listValue="auditName" />
			<s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch( 'form1')" />
			</td></tr>
			<tr><td>
			<s:select list="operatorList" cssClass="forms" name="operator" listKey="id" listValue="name" multiple="true" size="3" />
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
	    <td><a href="?orderBy=ca.closedDate DESC" >Created</a></td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><s:property value="[0].get('name')"/></td>
		<td><s:property value="[0].get('auditName')"/></td>
		<td class="center"><s:date name="[0].get('closedDate')" format="M/d/yy" /></td>
	</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
