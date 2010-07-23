<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Incidence Rates (Graph)</title>
<script src="js/FusionCharts.js" type="text/javascript"></script>
</head>
<body>
<h1>Incidence Rates (Graph)</h1>

<div id="search">
<s:form>
<s:select list="shaTypes" cssClass="forms" name="shaType" multiple="true" />
<s:select list="allYears" cssClass="forms" name="years" multiple="true" />
<s:select list="chartTypeList" name="chartType" />
<s:submit value="Refresh" />
</s:form>
</div>
<br clear="all"/>

<s:property value="flashChart" escape="false" />

</body>
</html>
