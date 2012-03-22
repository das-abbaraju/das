<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<head>
	<title>Unsynced Translations</title>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	
	<script type="text/javascript" src="js/ReportSearch.js?v=<s:property value="version"/>"></script>
	<script type="text/javascript" src="js/filters.js?v=<s:property value="version"/>"></script>
</head>
<body>
	<h1>Unsynced Translations</h1>
	
	<s:include value="../actionMessages.jsp" />
	<s:include value="../config_environment.jsp" />
	
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
	
	<table class="report">
		<thead>
			<tr>
				<th>
					Key
				</th>
				<th>
					Locale
				</th>
				<th>
					Local Translation
				</th>
				<th>
					Config Translation
				</th>
			</tr>
		</thead>
	    <tbody>
	    	<s:iterator value="data">
		    	<tr>
		    		<td>
		    			<s:property value="get('msgKey')" />
		    		</td>
		    		<td>
		    			<s:property value="get('locale')" />
		    		</td>
		    		<td>
		    			<s:property value="get('translationLocal')" />
		    		</td>
		    		<td>
		    			<s:property value="get('translationConfig')" />
		    		</td>
		    	</tr>
	    	</s:iterator>
	    </tbody>
	</table>
	
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
</body>