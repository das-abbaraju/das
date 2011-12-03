<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Report</title>
<link rel="stylesheet" type="text/css" href="js/extjs/resources/css/ext-all.css">
<script type="text/javascript" src="js/extjs/ext-all.js"></script>
</head>
<body>
<h1>Dynamic Report</h1>

<div id="report_extjs"></div>

<script type="text/javascript">
Ext.onReady(function() {
	var baseStore = Ext.create('Ext.data.Store', {
		proxy : {
			type : 'ajax',
			url : 'ReportDynamic!data.action?<s:if test="report.id > 0">report=<s:property value="report.id"/></s:if><s:else>report.base=<s:property value="report.base"/></s:else>',
			reader : {
				type : 'json',
				root : 'data'
			}
		},
		autoLoad : true,
		fields : <s:property value="availableFields" escape="false" />
	});

	Ext.create('Ext.grid.Panel', {
		renderTo : 'report_extjs',
		store : baseStore,
		// stripeRows : true,
		// multiSelect : true,
		// width : 800,
		// height : 600,
		columns : <s:property value="gridColumns" escape="false" />
	});
});
</script>

</body>
</html>
