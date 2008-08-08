<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Desktop Matrix</title>
<script type="text/javascript" src="js/prototype.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Desktop Matrix</h1>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ManagePQFMatrix.action">PQF Matrix</a></li>
	<li><a href="ManageDesktopMatrix.action" class="current">Desktop Matrix</a></li>
</ul>
</div>

<s:if test="questions.size() == 0">
<div id="search">
<s:form id="form1" method="post" cssStyle="background-color: #F4F4F4;">
	<div class="buttons"><a href="#" class="positive"
		onclick="$('form1').submit(); return false;">Search</a></div>

	<div class="filterOption">Type of Work:<br />
		<s:select name="questionIDs" multiple="true" size="10" listKey="questionID" listValue="question" list="typeOfWork"></s:select>
	</div>
	<div class="filterOption">Industries:<br />
		<s:select name="questionIDs" multiple="true" size="10" listKey="questionID" listValue="question" list="industries"></s:select>
	</div>
	<div class="filterOption">Services Performed:<br />
		<s:select name="questionIDs" multiple="true" size="10" listKey="questionID" listValue="question" list="servicesPerformed"></s:select>
	</div>
	<br clear="all" />
</s:form>
</div>
</s:if>

<s:if test="questions.size() > 0">
<s:form id="formdata">
	<div><a href="ManageDesktopMatrix.action">Change Columns</a></div>
	<div class="buttons"><a href="#" class="positive"
		onclick="$('formdata').submit(); return false;">Save</a></div>
<table class="report">
<thead>
<tr>
	<th>Desktop Category</th>
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
			<td class="center"><s:checkbox name="%{'data['+[1].id+']['+questionID+']'}" value="data.get([1].id).get(questionID)"></s:checkbox></td>
		</s:iterator>
	</tr>
</s:iterator>
</tbody>
</table>
<div class="buttons"><a href="#" class="positive"
	onclick="$('formdata').submit(); return false;">Save</a></div>
</s:form>
</s:if>

</body>
</html>