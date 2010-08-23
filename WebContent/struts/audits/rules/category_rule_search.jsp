<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage Category Rules</title>
<s:include value="../../reports/reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
$(function() {
	$('.searchAuto').each(function(){
		$(this).autocomplete('CategoryRuleSearchAjax.action', {
			extraParams: {fieldName: $(this).attr('id'), button: 'getAjax'},
			formatResult: function(data,i,count){
				return data;
			}
		});
	});
});

</script>
</head>
<body>

<h1>Manage Category Rules</h1>

<div id="search">
<s:form id="form1"
	action="%{filter.destinationAction}">
	<s:hidden name="orderBy" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="showPage" value="1" />

	<div class="filterOption"> 
		<button id="searchfilter" type="submit" name="button" value="Search"
			class="picsbutton positive" onclick="$('[name=showPage]').val(1)">Search</button>	
	</div>
	<br clear="all" />
	
	<div class="filterOption">  <!-- Auto Complete -->
		Audit Type: <s:textfield cssClass="searchAuto" id="auditType" name="filter.auditType"/>
	</div>
	
	<div class="filterOption">  <!-- Auto Complete -->
		Category: <s:textfield cssClass="searchAuto" id="category" name="filter.category"/>
	</div>
	
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
<s:include value="category_rule_search_data.jsp"></s:include>
</div>


<br clear="all" />
</body>
</html>
