<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>EMR Rates</title>
<script src="js/FusionCharts.js" type="text/javascript"></script>
</head>
<body>
<h1>EMR Rates</h1>

<s:property value="flashChart" escape="false" />

<div id="search">
<s:form>
<s:select list="{2001,2002,2003,2004,2005,2006,2007,2008}"
	cssClass="forms" name="years" multiple="true" size="5" />
<s:select list="chartTypeList" name="chartType" />
<s:submit value="Refresh" />
</s:form>
</div>

</body>
</html>
