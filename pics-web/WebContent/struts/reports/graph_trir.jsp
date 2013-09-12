<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<head>
	<title>
		<s:text name="GraphTrirRates.title" />
	</title>
	<script src="js/FusionCharts.js" type="text/javascript"></script>
</head>
<body>
	<h1>
		<s:text name="GraphTrirRates.title" />
	</h1>
	
	<div id="search">
		<s:form>
			<s:text name="Filters.label.SHAType" />: <s:select list="shaTypes" cssClass="forms" name="shaType" multiple="true" />
			<s:text name="Filters.label.ForYear" />: <s:select list="allYears" cssClass="forms" name="years" multiple="true" />
			<s:text name="Filters.label.ChartType" />: <s:select list="chartTypeList" name="chartType" />
			<s:submit value="%{getText('button.Refresh')}" />
		</s:form>
	</div>
	<br clear="all"/>
	<s:property value="flashChart" escape="false" />
</body>
