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
	$('.datepicker').datepicker({
			changeMonth: true,
			changeYear:true,
			yearRange: '1940:2010',
			showOn: 'button',
			buttonImage: 'images/icon_calendar.gif',
			buttonImageOnly: true,
			buttonText: 'Choose a date...',
			constrainInput: true,
			showAnim: 'fadeIn'
	});
	
	$('.searchAuto').each(function(){
		var field =  $(this).attr('id');
		if(field=='operator')
			var num = 100;
		else
			var num = 10;
		$(this).autocomplete('<s:property value="filter.destinationAction" />Ajax.action', {
			extraParams: {fieldName: field, button: 'searchAuto'},
			max: num,
			width: 200,
			formatItem : function(data,i,count){
				return data[1]
			},
			formatResult: function(data,i,count){
				return data[1];
			}
		}).result(function(event, data){
			event.preventDefault();
			getManageResult(data);
		});
	});
});
function getManageResult(data){
	if(data[0]=='auditType'){
		$('input[name=filter.auditTypeID]').val(data[2]);
	} else if(data[0]=='dependentAuditType'){
		$('input[name=filter.dependentAuditTypeID]').val(data[2]);
	} else if(data[0]=='cat'){
		$('input[name=filter.catID]').val(data[2]);
	} else if(data[0]=='op'){
		$('input[name=filter.opID]').val(data[2]);
	} else if(data[0]=='tag'){
		$('input[name=filter.tagID]').val(data[2]);
	}	
}
function clearFilter(){
	$('#searchFilterOptions input:hidden').val(0);
	$('#searchFilterOptions input').not(':hidden').val("");		
	$('select[name=filter.include]').val(2);
	$('select[name=filter.contractorType]').val(-1);
	$('select[name=filter.riskLevel]').val(-1);
}
</script>
</head>
<body>

<h1>Manage <s:property value="ruleType" /></h1>

<div id="search">
<s:form id="form1"
	action="%{filter.destinationAction}">
	<s:hidden name="showPage" value="1" />

	
	<button id="searchfilter" type="submit" name="button" value="Search"
		class="picsbutton positive" onclick="$('[name=showPage]').val(1)">Search</button>
	
	<button id="clear" 
		class="picsbutton" onclick="clearFilter(); return false;">Clear Filter</button>
	<br clear="all" />
	
	<div id="searchFilterOptions">	
	<s:hidden name="orderBy" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.auditTypeID" />
	<s:hidden name="filter.dependentAuditTypeID" />
	<s:hidden name="filter.catID" />
	<s:hidden name="filter.opID" />
	<s:hidden name="filter.tagID" />
		<div class="filterOption">
			Audit Type: <s:textfield cssClass="searchAuto forms" id="auditType" name="filter.auditType"/>
		</div>
		
		<div class="filterOption">  <!-- Auto Complete -->
			Include:
			<s:select cssClass="forms" list="#{'2':'Any','1':'Yes','0':'No'}" name="filter.include" value="filter.include" />
		</div>
		
		<s:if test="filter.showCategory">
			<div class="filterOption">  <!-- Auto Complete -->
				Category: <s:textfield cssClass="searchAuto forms" id="category" name="filter.category"/>
			</div>
		</s:if>
		
		<div class="filterOption">
			Contractor Type: <s:select cssClass="forms" list="filter.contractorTypeList" name="filter.contractorType"
			 	listKey="ordinal()" listValue="name()" headerKey="-1" headerValue="*" />
		</div>
		
		<div class="filterOption">  <!-- Auto Complete -->
			Operator: <s:textfield cssClass="searchAuto forms" id="operator" name="filter.operator"/>
		</div>	
		
		<div class="filterOption">Risk: 
			<s:select cssClass="forms" list="filter.riskLevelList" name="filter.riskLevel" value="filter.riskLevel"
				listKey="ordinal()" listValue="name()" headerKey="-1" headerValue="*" />
		</div>
		
		<div class="filterOption"> <!-- Auto Complete -->
			Tag: <s:textfield cssClass="searchAuto forms" id="tag" name="filter.tag"/>
		</div>
		
		<div class="filterOption"> <!-- Auto Complete -->
			Bid-Only: <s:checkbox label="Bid-Only" name="filter.bid" value="filter.bid" />
		</div>
		
		<s:if test="filter.showDependentAuditType">
			<div class="filterOption">  <!-- Auto Complete -->
				Dependent Audit Type: <s:textfield cssClass="searchAuto forms" id="dependentAuditType" name="filter.dependentAuditType"/>
			</div>
		</s:if>
		
		<s:if test="filter.showDependentAuditStatus">
			<div class="filterOption">  <!-- Auto Complete -->
				Dependent Audit Status: 
				<s:select cssClass="forms" list="filter.dependentAuditStatusList" name="filter.dependentAuditStatus"
					listKey="ordinal()" listValue="name()" headerKey="-1" headerValue="*" />
			</div>
		</s:if>
		
		<div class="filterOption"> 
				Effective Date: <s:textfield cssClass="datepicker forms" name="filter.checkDate" />
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
