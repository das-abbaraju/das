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
<s:select list="#{'2001':'2001','2002':'2002','2003':'2003','2004':'2004','2005':'2005','2006':'2006','2007':'2007','2008':'2008','2009':'2009'}"
	cssClass="forms" name="years" multiple="true" size="5"/>
<s:select list="chartTypeList" name="chartType" />
<s:submit value="Refresh" />
</s:form>
</div>
<br clear="all"/>

<s:property value="flashChart" escape="false" />

</body>
</html>
