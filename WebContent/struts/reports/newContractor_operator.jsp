<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<title><s:text name="NewContractorSearch.title" /></title>
    
	<s:include value="reportHeader.jsp" />
    
	<style type="text/css">
		table.report thead a.cluetip {
			color: #FFF;
			text-decoration: none;
			background: url("images/help.gif") no-repeat left center;
			padding: 0px 0px 0px 12px;
			margin: 0px;
		}
		
		td.tradeList .hidden {
			display: none;
		}
	</style>
</head>
<body>
    <h1><s:text name="NewContractorSearch.title" /></h1>
    
    <s:include value="filters.jsp" />
    
    <div id="report_data">
    	<s:if test="data.size > 0">
    		<s:include value="newContractor_operator_data.jsp" />
    	</s:if>
    	<s:else>
	    	<div class="info">
	    		<s:text name="NewContractorSearch.message.SearchByNameOrTrade" />
	    	</div>
    	</s:else>
    </div>
</body>