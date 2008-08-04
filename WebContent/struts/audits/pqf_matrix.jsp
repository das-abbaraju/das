<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Operator/PQF Category Matrix</title>
<script type="text/javascript" src="js/prototype.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Operator/PQF Category Matrix</h1>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ManagePQFMatrix.action" class="current">PQF Matrix</a></li>
	<li><a href="ManageDesktopMatrix.action">Desktop Matrix</a></li>
</ul>
</div>

<div id="search">
<s:form id="form1" method="post" cssStyle="background-color: #F4F4F4;">
	<div class="buttons"><a href="#" class="positive"
		onclick="$('form1').submit(); return false;">Search</a></div>

	<div class="filterOption">Operators:<br />
		<s:select name="operators" list="operatorList" listKey="id" listValue="name" multiple="true" size="10"></s:select></div>
	<div class="filterOption">Risk Levels:<br />
		<s:select name="riskLevels" list="#{1:'Low',2:'Medium',3:'High'}" multiple="true" size="3"></s:select> </div>
	<br clear="all" />
</s:form>
</div>

<s:form id="formdata">
	<div class="buttons"><a href="#" class="positive"
		onclick="$('formdata').submit(); return false;">Save</a></div>
<table class="report">
<thead>
<tr>
	<th></th>
<s:iterator value="operatorAccounts">
	<th colspan="<s:property value="riskLevels.length" />"><s:property value="name" /></th>
</s:iterator>
	<th rowspan="2" width="30px">&nbsp;</th>
</tr>
<tr>
	<th></th>
<s:iterator value="columns">
	<th><s:property value="riskLevel" /></th>
</s:iterator>
</tr>
</thead>
<tbody>
<s:iterator value="categories">
	<tr>
		<th><s:property value="number" />. <s:property value="category" /></th>
		<s:iterator value="columns">
			<td class="center"><s:checkbox name="data[]" value="isChecked([1].id, [0].riskLevel, [0].operatorAccount.id)"></s:checkbox></td>
		</s:iterator>
	</tr>
</s:iterator>
</tbody>
</table>
<div class="buttons"><a href="#" class="positive"
	onclick="$('formdata').submit(); return false;">Save</a></div>
</s:form>

</body>
</html>