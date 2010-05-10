<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>TRIR Rates</title>
<script src="js/FusionCharts.js" type="text/javascript"></script>
</head>
<body>
<h1>TRIR Rates</h1>

<div id="search">
<s:form>
<s:select list="shaTypes" cssClass="forms" name="shaType" headerKey="" headerValue="- Osha Type -" />
<s:select list="chartTypeList" name="chartType" />
<s:submit value="Refresh" />
</s:form>
</div>
<br clear="all"/>

<s:property value="flashChart" escape="false" />

</body>
</html>
