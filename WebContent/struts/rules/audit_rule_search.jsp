<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Search <s:property value="categoryRule ? 'Category' : 'Audit Type'" /> Rules</title>
<s:include value="../reports/reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
$(function() {
	$('.datepicker').datepicker({
			changeMonth: true,
			showOn: 'button',
			buttonImage: 'images/icon_calendar.gif',
			buttonImageOnly: true,
			buttonText: 'Choose a date...',
			constrainInput: true,
			showAnim: 'fadeIn'
	});
});
</script>
</head>
<body>

<h1>Search <s:property value="categoryRule ? 'Category' : 'Audit Type'" /> Rules</h1>

<div id="search">
<s:form id="form1"
	action="%{filter.destinationAction}">
	<s:hidden name="showPage" value="1" />

	
	<button id="searchfilter" type="submit" name="button" value="Search"
		class="picsbutton positive" onclick="$('[name=showPage]').val(1)">Search</button>
	<br clear="all" />
	
	<s:hidden name="orderBy" />
	<s:hidden name="filter.destinationAction" />
	
	<div id="searchFilterOptions">	
		<div class="filterOption">
			Rule Type:
			<s:select list="#{'-1':'Any','1':'Include','0':'Exclude'}" name="filter.include" value="filter.include" />
		</div>
		
		<div class="filterOption">
			Audit Type: <s:textfield cssClass="searchAuto" id="auditType" name="filter.auditType"/>
		</div>
		
		<s:if test="categoryRule">
			<div class="filterOption">
				Category: <s:textfield cssClass="searchAuto" id="category" name="filter.category"/>
			</div>
		</s:if>
		
		<div class="filterOption">
			Operator: <s:textfield cssClass="searchAuto" id="operator" name="filter.operator"/>
		</div>
		
		<div class="filterOption">
			Account Type: <s:select list="filter.contractorTypeList" name="filter.contractorType"
			 	listKey="name()" listValue="name()" headerKey="" headerValue="*" />
		</div>
		
		<div class="filterOption">Risk: 
			<s:select list="filter.riskLevelList" name="filter.riskLevel" value="filter.riskLevel"
				listKey="ordinal()" listValue="name()" headerKey="-1" headerValue="*" />
		</div>
		
		<div class="filterOption">
			Full/List-Only: <s:select list="#{'-1':'All',1:'List Only',0:'Full Account'}" name="filter.bid" value="filter.bid" />
		</div>
		
		<s:if test="!categoryRule">
			<div class="filterOption">
				Dependent Audit Type: <s:textfield cssClass="searchAuto" id="dependentAuditType" name="filter.dependentAuditType"/>
			</div>
		</s:if>
		
		<div class="filterOption">
			Effective Date: <s:textfield cssClass="datepicker" name="filter.checkDate" />
		</div>
	</div>
	
	<br clear="all" />
</s:form>
</div> 

<div id="report_data">
<s:include value="audit_rule_search_data.jsp"></s:include>
</div>


<br clear="all" />
</body>
</html>
