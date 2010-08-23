<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage <s:property value="ruleType" /></title>
<s:include value="../../reports/reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
$(function() {
	$('.searchAuto').each(function(){
		var field =  $(this).attr('id');
		if(field=='operator')
			var num = 100;
		else
			var num = 10;
		$(this).autocomplete('<s:property value="filter.destinationAction" />Ajax.action', {
			extraParams: {fieldName: field, button: 'searchAuto'},
			max: num,
			formatResult: function(data,i,count){
				return data;
			}
		});
	});
});

</script>
</head>
<body>

<h1>Manage <s:property value="ruleType" /></h1>

<div id="search">
<s:form id="form1"
	action="%{filter.destinationAction}">
	<s:hidden name="orderBy" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="showPage" value="1" />

	
	<button id="searchfilter" type="submit" name="button" value="Search"
		class="picsbutton positive" onclick="$('[name=showPage]').val(1)">Search</button>
			
	<br clear="all" />
	
	<div class="filterOption">
		Audit Type: <s:textfield id="auditType" name="filter.auditType"/>
	</div>
	
	<div class="filterOption">  <!-- Auto Complete -->
		Include:
		<s:select cssClass="forms" list="#{'2':'Any','1':'Yes','0':'No'}" name="filter.include" value="filter.include" />
	</div>
	
	<s:if test="filter.showCategory">
		<div class="filterOption">  <!-- Auto Complete -->
			Category: <s:textfield cssClass="searchAuto" id="category" name="filter.category"/>
		</div>
	</s:if>
	
	<div class="filterOption">
	<s:if test="filter.accountType==NULL">
		<s:set var="acT" value='"*"' />
	</s:if>
	<s:else>
		<s:set var="acT" value='filter.accountType' />
	</s:else>
		Account Type: <s:select cssClass="forms" list="filter.accountTypeList" name="filter.accountType" value="acT" />
	</div>
	
	<div class="filterOption">  <!-- Auto Complete -->
		Operator: <s:textfield cssClass="searchAuto" id="operator" name="filter.operator"/>
	</div>	
	
	<div class="filterOption">Risk: 
		<s:select cssClass="forms" list="#{'0':'*','1':'Low','2':'Medium','3':'High'}" name="filter.riskLevel" value="filter.riskLevel" />
	</div>
	
	<div class="filterOption"> <!-- Auto Complete -->
		Tag: <s:textfield cssClass="searchAuto" id="tag" name="filter.tag"/>
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
