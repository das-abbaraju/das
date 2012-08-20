<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
</head>
<body>
<h1><s:property value="reportName" /></h1>

<s:include value="../actionMessages.jsp"></s:include>

<div>
	<h2>ContractorCron Velocity Report (last 24 hours)</h2>
	<table class="report">
		<thead>
			<tr>		
			  <s:iterator var="columnName" value="flagAnalyzer.velocityData.columnNames">
				<th><s:property value="columnName"/></th>
			  </s:iterator>
			</tr>
		</thead>
		<tbody>
		  <s:iterator var="row" value="flagAnalyzer.velocityData.rows">
			  <tr>
			  	<s:iterator var="datum" value="#row" status="stat">
			  		<td><s:property value="#datum.getValue()"/></td>
			  	</s:iterator>
			  </tr>
		  </s:iterator>
		</tbody>
	</table>
</div>
<div>
	<h2>Flag Differences High-Level Report</h2>
	<table class="report">
		<thead>
			<tr>		
			  <s:iterator var="columnName" value="flagAnalyzer.flagDiffData.columnNames">
				<th><s:property value="columnName"/></th>
			  </s:iterator>
			</tr>
		</thead>
		<tbody>
		  <s:iterator var="row" value="flagAnalyzer.flagDiffData.rows">
			  <tr>
			  	<s:iterator var="datum" value="#row" status="stat">
			  		<td><s:property value="#datum.getValue()"/></td>
			  	</s:iterator>
			  </tr>
		  </s:iterator>
		</tbody>
	</table>
</div>
<div>
	<h2>Flag Differences CAO Status Report</h2>
	<table class="report">
		<thead>
			<tr>		
			  <s:iterator var="columnName" value="flagAnalyzer.flagDiffCaoStatus.columnNames">
				<th><s:property value="columnName"/></th>
			  </s:iterator>
			</tr>
		</thead>
		<tbody>
		  <s:iterator var="row" value="flagAnalyzer.flagDiffCaoStatus.rows">
			  <tr>
			  	<s:iterator var="datum" value="#row" status="stat">
			  		<td><s:property value="#datum.getValue()"/></td>
			  	</s:iterator>
			  </tr>
		  </s:iterator>
		</tbody>
	</table>
</div>
</body>
</html>
