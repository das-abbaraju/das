<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manual Audit Matrix</title>
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
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
<h1>Manual Audit Matrix</h1>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ManagePQFMatrix.action">PQF Matrix</a></li>
	<li><a href="ManageDesktopMatrix.action" class="current">Manual Audit Matrix</a></li>
</ul>
</div>

<s:include value="../actionMessages.jsp"></s:include>

<s:form id="form1" method="post">
<div id="search">
<s:if test="questions.size() > 0">
	<div id="changecolumns"><a id="changecolumnslink" href="#" onclick="$('#filters').show(); $('#changecolumns').hide(); return false;">Change Columns</a></div>
</s:if>
<div id="filters" <s:if test="questions.size() > 0">style="display: none;"</s:if>>
	<div>
		<button class="picsbutton positive" name="button" type="submit" value="Search">Search</button>
	</div>

	<div class="filterOption">Type of Work:<br />
		<s:select id="questionsTOW" name="questionIDs" multiple="true" size="10" listKey="id" listValue="question" list="typeOfWork"></s:select>
		<br /><a class="clearLink" href="#" onclick="clearSelected('questionsTOW'); return false;">Clear</a>
	</div>
	<div class="filterOption">Industries:<br />
		<s:select id="questionsInd" name="questionIDs" multiple="true" size="10" listKey="id" listValue="question" list="industries"></s:select>
		<br /><a class="clearLink" href="#" onclick="clearSelected('questionsInd'); return false;">Clear</a>
	</div>
	<div class="filterOption">Services Performed:<br />
		<s:select id="questionsSP" name="questionIDs" multiple="true" size="10" listKey="id" listValue="question" list="servicesPerformed"></s:select>
		<br /><a class="clearLink" href="#" onclick="clearSelected('questionsSP'); return false;">Clear</a>
	</div>
	<br clear="all" />
</div>
</div>
<s:if test="questions.size() > 0">
	<div>
		<button class="picsbutton positive" name="button" type="submit" value="Save">Save</button>
	</div>
	<table class="report">
	<thead>
	<tr>
		<th>Manual Audit Category</th>
	<s:iterator value="questions">
		<th><s:property value="question" /></th>
	</s:iterator>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="categories">
		<tr>
			<th><s:property value="number" />. <s:property value="category" /></th>
			<s:iterator value="questions">
				<td title="<s:property value="question"/>" class="center<s:if test="data.get([1].id).get(id)"> selected</s:if>">
					<s:checkbox name="incoming['%{[1].id}_%{id}']" value="data.get([1].id).get(id)"></s:checkbox>
				</td>
			</s:iterator>
		</tr>
	</s:iterator>
	</tbody>
	<thead>
	<tr>
		<th>Manual Audit Category</th>
	<s:iterator value="questions">
		<th><s:property value="question" /></th>
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