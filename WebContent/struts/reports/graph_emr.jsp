<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>EMR Rates</title>
<script src="js/FusionCharts.js" type="text/javascript"></script>
</head>
<body>
<h1>EMR Rates</h1>

<div id="search">
<s:form>
<s:submit value="Refresh" />
<br/>
Year: <s:select list="yearsList"
	cssClass="forms" name="years" multiple="true" size="5"/>
Chart Type: <s:select list="chartTypeList" name="chartType" />
Show Average EMR:  <s:checkbox name="showAvg"/>
<s:if test="permissions.admin || permissions.corporate">
	<s:select list="OperatorsList" cssClass="forms" name="operatorIDs" multiple="true" listKey="id" listValue="name" headerKey="0" headerValue="- Operator -" size="10"/>
</s:if>
</s:form>
</div>
<br clear="all"/>

<s:property value="flashChart" escape="false" />

</body>
</html>
