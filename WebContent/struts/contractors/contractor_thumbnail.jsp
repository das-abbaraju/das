<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /> Thumbnail</title>
<s:include value="../jquery.jsp"/>
<style>
body {
	background-color: #fefefe;
	margin: 0px;
	padding: 0px;
	font-family: Helvetica, Arial, sans-serif;
	text-align: center;
}
h1 {
	margin-bottom: 0px;
}
div.tabs {
	text-align: center;
}
table.tabs {
	border-collapse: collapse;
	padding: 0px;
	margin: 5px;
	width: 95%;
}
th.tabs {
	border: 1px solid black;
	text-align: center;
	font-size: 14px;
	background-color: #EEE;
	width: 33%;
}
th.selected {
	background-color: white;
}
td.tabbody {
	border-bottom: 1px solid black;
	border-left: 1px solid black;
	border-right: 1px solid black;
}
</style>
<script type="text/javascript">
function toggleTab(name) {
	$('#tab_address').hide();
	$('#tab_audits').hide();
	$('#tab_facilities').hide();
	$('#tab_'+name).show();
	$('#head_address').removeClassName('selected');
	$('#head_audits').removeClassName('selected');
	$('#head_facilities').removeClassName('selected');
	$('#head_'+name).addClassName('selected');
}
</script>
</head>
<body>
<h1><s:property value="contractor.name" /></h1>

<div class="tabs">
<table class="tabs">
<tr>
	<th class="tabs selected" id="head_address" onmouseover="toggleTab('address');">Contact info</th>
	<th class="tabs" id="head_audits" onmouseover="toggleTab('audits');">Audits</th>
	<th class="tabs" id="head_facilities" onmouseover="toggleTab('facilities');">Facilities</th>
</tr>
<tr>
	<td colspan="3" class="tabbody">
		<div class="tab" id="tab_address">
			<table>
			<tr><th>Location:</th>
				<td><s:property value="contractor.city" />, <s:property value="contractor.state" /></td>
			</tr>
			<tr><th>Contact:</th>
				<td><s:property value="contractor.contact" /></td>
			</tr>
			<tr><th>Phone:</th>
				<td><s:property value="contractor.phone" /></td>
			</tr>
			<tr><th>Email:</th>
				<td><s:property value="contractor.email" /></td>
			</tr>
			</table>
		</div>
		<div class="tab" id="tab_audits" style="display: none">
			<ul>
			<s:iterator value="contractor.audits">
				<s:if test="auditStatus.toString() == 'Active'">
					<li><s:property value="auditType.auditName" /> <s:date name="closedDate" format="MMM `yy"/> - <s:date name="expiresDate" format="MMM `yy" /></li>
				</s:if>
			</s:iterator>
			</ul>
		</div>
		<div class="tab" id="tab_facilities" style="display: none">
			<ul>
			<s:iterator value="contractor.operators">
				<li><s:property value="operatorAccount.name" /></li>
			</s:iterator>
			</ul>
		</div>
	</td>
</tr>
</table>
</div>
</body>
</html>
