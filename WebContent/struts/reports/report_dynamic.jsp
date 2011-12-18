<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<html>
<head>
<title><s:property value="report.summary"/></title>
</head>
<body>
| <s:iterator value="availableReports"><a href="?report=<s:property value="id"/>"><s:property value="summary"/></a> | </s:iterator>

<h1><s:property value="report.summary"/></h1>

<div id="chart_extjs"></div>
<div id="report_extjs"></div>

<a href="ReportDynamic!data.action?report=<s:property value="report.id"/>" target="reportData">See JSON Data</a>

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
	
	Ext.create('Ext.chart.Chart', {
	    renderTo: 'chart_extjs',
	    width: 500,
	    height: 300,
	    animate: true,
		store : baseStore,
	    axes: [{
	        type: 'Numeric',
	        position: 'bottom',
	        fields: ['data1'],
	        label: {
	            renderer: Ext.util.Format.numberRenderer('0,0')
	        },
	        title: 'Sample Values',
	        grid: true,
	        minimum: 0
	    }, {
	        type: 'Category',
	        position: 'left',
	        fields: ['name'],
	        title: 'Sample Metrics'
	    }],
	    series: [{
	        type: 'bar',
	        axis: 'bottom',
	        highlight: true,
	        tips: {
	          trackMouse: true,
	          width: 140,
	          height: 28,
	          renderer: function(storeItem, item) {
	            this.setTitle(storeItem.get('name') + ': ' + storeItem.get('data1') + ' views');
	          }
	        },
	        label: {
	          display: 'insideEnd',
	            field: 'data1',
	            renderer: Ext.util.Format.numberRenderer('0'),
	            orientation: 'horizontal',
	            color: '#333',
	            'text-anchor': 'middle'
	        },
	        xField: 'name',
	        yField: ['data1']
	    }]
	});
	
	Ext.create('Ext.grid.Panel', {
		renderTo : 'report_extjs',
		store : baseStore,
		columns : <s:property value="gridColumns" escape="false" />
	});
});
</script>

<%
/*
*/
%>

</body>
</html>
