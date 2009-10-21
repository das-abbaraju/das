<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>PQF Matrix</title>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<style>
td.selected {
	background-color: #FEC;
}
</style>
<script type="text/javascript">
function clearSelected(name) {
	$('#'+name).find('option').attr({'selected': false});
}
</script>
</head>
<body>
<h1>PQF Matrix</h1>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ManagePQFMatrix.action" class="current">PQF Matrix</a></li>
	<li><a href="ManageDesktopMatrix.action">Desktop Matrix</a></li>
</ul>
</div>

<s:include value="../actionMessages.jsp"></s:include>

<s:form id="form1" method="post">
<div id="search">
<s:if test="columns.size() > 0">
	<div id="changecolumns"><a href="#" onclick="$('#filters').show(); $('#changecolumns').hide(); return false;">Change Columns</a></div>
</s:if>
<div id="filters" <s:if test="columns.size() > 0">style="display: none;"</s:if>>
	<div>
		<button class="picsbutton positive" name="button" type="submit" value="Search">Search</button>
	</div>

	<div class="filterOption">Operators:<br />
		<s:select id="operatorList" name="operators" list="operatorList" listKey="id" listValue="name" multiple="true" size="10"></s:select>
		<br /><a class="clearLink" href="#" onclick="clearSelected('operatorList'); return false;">Clear</a>
	</div>
	<div class="filterOption">Risk Levels:<br />
		<s:select name="riskLevels" list="#{1:'Low',2:'Medium',3:'High'}" multiple="true" size="3"></s:select>
	</div>
	<br clear="all" />
</div>
</div>

<div>
<table style="width: 100%;">
	<s:if test="operatorAccounts.size > 0">
		<s:iterator value="operatorChildren.entrySet()">
			<tr><td style="padding: 10px;">
			<h3>Companies that inherit the Audit Categories from <s:property value="key.name"/></h3>
			<ul>
				<s:iterator value="value">
					<li><a href="FacilitiesEdit.action?id=<s:property value="id"/>"><s:property value="name" /></a></li>
				</s:iterator>
			</ul>
			</td></tr>
		</s:iterator>
	</s:if>
</table>
</div>

<s:if test="columns.size() > 0">
	<div>
		<button class="picsbutton positive" name="button" type="submit" value="Save">Save</button>
	</div>
	<table class="report">
	<thead>
	<tr>
		<th>PQF Category</th>
	<s:iterator value="operatorAccounts">
		<th colspan="<s:property value="riskLevels.length" />"><s:property value="name" /></th>
	</s:iterator>
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
				<td title="<s:property value="%{[1].name} %{riskLevel.name()}"/>" class="center<s:if test="flagData[operatorAccount.id][[1].id][riskLevel.name()]"> selected</s:if>">
					<s:checkbox name="incoming['%{operatorAccount.id}_%{[1].id}_%{riskLevel.name()}']" value="flagData[operatorAccount.id][[1].id][riskLevel.name()]" />
				</td>
			</s:iterator>
		</tr>
	</s:iterator>
	</tbody>
	<thead>
	<tr>
		<th></th>
	<s:iterator value="columns">
		<th><s:property value="riskLevel" /></th>
	</s:iterator>
	</tr>
	<tr>
		<th>PQF Category</th>
	<s:iterator value="operatorAccounts">
		<th colspan="<s:property value="riskLevels.length" />"><s:property value="name" /></th>
	</s:iterator>
	</tr>
	</thead>
	</table>
	<div>
		<button class="picsbutton positive" name="button" type="submit" value="Save">Save</button>
	</div>
</s:if>
</s:form>

</body>
</html>