<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Search - Operator</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects"
	type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Contractor Limited Search <span class="sub">Operator
Version</span></h1>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="2" align="center" class="blueMain"><span
			class="redMain">You have <strong><s:property
			value="contractorCount" /></strong> contractors in your database.</span></td>
	</tr>
</table>
<div id="search">
<div id="showSearch"><a href="#" onclick="showSearch()">Show Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<s:form id="form1" method="post" cssStyle="display: none">
	<table border="0" align="center" cellpadding="2" cellspacing="0">
		<tr>
			<td align="left"><s:textfield name="accountName"
				cssClass="forms" size="8" onfocus="clearText(this)" /><s:select
				list="tradeList" cssClass="forms" name="trade" /><s:submit
				name="imageField" type="image" src="images/button_search.gif"
				onclick="runSearch( 'form1')" /></td>
		</tr>
		<tr>
			<td><s:if test="permissions.operator">
				<s:select list="flagStatusList" cssClass="forms" name="flagStatus" />
			</s:if> <s:if test="permissions.corporate">
				<s:select list="operatorList" cssClass="forms" name="operator" />
			</s:if></td>
		</tr>
	</table>
	<s:hidden name="showPage" value="1" />
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
		<td colspan="2">Contractor Name</td>
		<s:if test="permissions.operator">
			<td align="center" bgcolor="#6699CC"><a
				href="?orderBy=flag DESC" class="whiteTitle">Flag</a></td>
		</s:if>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><s:property value="[0].get('name')" /></td>
			<td class="center"><s:if test="permissions.operator">
				<a href="ContractorFlag.action?id=<s:property value="[0].get('id')"/>"><img
					src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif"
					width="12" height="15" border="0"></a>
			</s:if></td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
