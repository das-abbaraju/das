<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Search - Operator</title>
<script type="text/javascript" src="js/Search.js" />


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
<s:form id="form1" method="post">
	<table border="0" align="center" cellpadding="2" cellspacing="0">
		<tr>
			<td align="left"><s:textfield name="accountName"
				cssClass="forms" size="8" onfocus="clearText(this)" /><s:select
				list="tradeList" cssClass="forms" name="trade" /><s:submit
				name="imageField" type="image" src="images/button_search.gif"
				onclick="runSearch( 'form1')" /></td>
		</tr>
		<tr>
			<td><s:if test="operator">
				<s:select list="flagStatusList" cssClass="forms" name="flagStatus" />
			</s:if> <s:if test="corporate">
				<s:select list="operatorList" cssClass="forms" name="operator" />
			</s:if></td>
		</tr>
	</table>
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<table border="0" cellpadding="0" cellspacing="0" width="900">
	<tr>
		<td align="left"><s:property
			value="report.startsWithLinksWithDynamicForm" escape="false" /></td>
		<td align="right"><s:property
			value="report.pageLinksWithDynamicForm" escape="false" /></td>
	</tr>
</table>
<table border="0" cellpadding="1" cellspacing="1">
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan="2">Contractor Name</td>
		<s:if test="operator">
			<td align="center" bgcolor="#6699CC"><a
				href="?orderBy=flag DESC" class="whiteTitle">Flag</a></td>
		</s:if>
	</tr>
	<s:iterator value="data">
		<tr class="blueMain"
			<s:property value="color.nextBgColor" escape="false" />>
			<td align="right"><s:property value="color.counter" /></td>
			<td align="center"><s:property value="[0].get('name')" /></td>
			<td align="center"><s:if test="operator">&nbsp;&nbsp;
				<a href="con_redFlags.jsp?id=<s:property value="[0].get('id')"/>"
					title="Click to view Flag Color details"> <img
					src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif"
					width="12" height="15" border="0"></a>
			</s:if></td>
		</tr>
	</s:iterator>
</table>
<center><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></center>
</body>
</html>
