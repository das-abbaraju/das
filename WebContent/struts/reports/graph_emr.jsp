<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="GraphEmrRates.title" /></title>
<script src="js/FusionCharts.js?v=${version}" type="text/javascript"></script>
</head>
<body>
<h1><s:text name="GraphEmrRates.title" /></h1>

<div id="search">
<s:form>
<s:submit value="%{getText('button.Refresh')}" cssClass="picsbutton positive" />
<br/>
<s:text name="Filters.label.ForYear" />:
<s:select list="yearsList" cssClass="forms" name="years" multiple="true" size="5" />
<s:text name="Filters.label.ChartType" />: <s:select list="chartTypeList" name="chartType" />
<s:text name="Filters.label.ShowAverageEMR" />:  <s:checkbox name="showAvg"/>
<s:if test="permissions.admin || permissions.corporate">
	<s:select list="OperatorsList" cssClass="forms" name="operatorIDs" multiple="true" listKey="id" listValue="name" 
		headerKey="0" headerValue="- %{getText('global.Operator')} -" size="10"/>
</s:if>
</s:form>
</div>
<br clear="all"/>

<s:property value="flashChart" escape="false" />

</body>
</html>
