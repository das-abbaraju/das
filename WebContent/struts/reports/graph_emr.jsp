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
<s:checkbox name="show04" />2004
<s:checkbox name="show05" />2005
<s:checkbox name="show06" />2006
<s:checkbox name="show07" />2007
<s:select list="chartTypeList" name="chartType" />
<s:submit value="Refresh" />
</s:form>
</div>

</body>
</html>
