<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Audit Rule History</title>
<s:include value="./reportHeader.jsp" />
<link rel="stylesheet" href="css/reports.css"/>
<link rel="stylesheet" href="css/forms.css"/>
<script>
$(function() {
	$('.datepicker').datepicker({
			changeMonth: true,
			changeYear:true,
			yearRange: '2008:'+new Date(),
			showOn: 'button',
			buttonImage: '../images/icon_calendar.gif',
			buttonImageOnly: true,
			buttonText: 'Choose a date...',
			constrainInput: true,
			showAnim: 'fadeIn'
	});
};
</script>
</head>
<body>
<h1>Audit Rule History</h1>
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
			Changed By: <s:textfield cssClass="forms" id="changedBy" name="filter.changedBy"/>
		</div>
		
		<div class="filterOption">
			Status: <s:select list="filter.statuses" name="filter.findStatus" headerKey="" headerValue="- Select Status -"/>
		</div>
		
		<div class="filterOption">
			Type: <s:select list="filter.types" name="filter.findType" headerKey="" headerValue="- Select Type -"/>
		</div>
		
		<div class="filterOption">
			From Date: <s:textfield name="filter.fromDate" cssClass="datepicker" />
		</div>
		
		<div class="filterOption">
			To Date: <s:textfield name="filter.toDate" cssClass="datepicker" />
		</div>
	</div>
	
	<br clear="all" />
</s:form>
</div> 

<div id="report_data">
	<s:include value="audit_rule_history_data.jsp"></s:include>
</div>

</body>
</html>