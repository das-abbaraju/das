<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Accounts Report</title>
<script src="js/Search.js?v=20091105" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css?v=20091105" />

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>
<link rel="stylesheet" href="js/jquery/jquery-ui/jquery-ui-1.7.2.custom.css">

<script type="text/javascript">
function showOperator(id) {
	$.get('OperatorQuickAjax.action', {id: id}, function(data){
		$('#operatorQuick').html(data);
		$('#operatorQuick').dialog('open');
	});
}
</script>

</head>
<body>
<h1>Manage <s:property value="accountType"/> Accounts</h1>
<s:if test="canEdit">
	<s:if test="accountType.equals('Corporate')">
		<div><a href="FacilitiesEdit.action?type=<s:property value="accountType"/>">Create New Corporate</a></div>
	</s:if>
	<s:else>
		<div><a href="FacilitiesEdit.action?type=<s:property value="accountType"/>">Create New</a></div>	
	</s:else>
</s:if>

<s:form id="form1">
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<div class="alphapaging"><s:property
value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
    <table class="report">
        <thead><tr> 
           <td></td>
			<th><a href="?orderBy=a.name">Name</a></th>
			<td>Industry</td>
			<td>City</td>
			<td>State</td>
			<td>Primary Contact</td>
			<td><s:if test="accountType.startsWith('O')">Contractors</s:if>
			<s:else>Operators</s:else>
			</td>
			<td></td>
		</tr></thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			
			<s:if test="%{get('active') == \"Y\"}">
			<td><a href="FacilitiesEdit.action?id=<s:property value="get('id')"/>&type=<s:property value="get('type')"/>"><s:property value="get('name')"/></a>
				<a href="#" onclick="showOperator(<s:property value="get('id')"/>); return false;">^</a>
			</td></s:if>
			<s:else>
			<td class="inactive"><a href="FacilitiesEdit.action?id=<s:property value="get('id')"/>&type=<s:property value="get('type')"/>"><s:property value="get('name')"/>*</a>
			</td>
			</s:else>
			<td><s:property value="get('industry')"/></td>
			<td class="right"><s:property value="get('city')"/></td>
			<td class="right"><s:property value="get('state')"/></td>
			<td class="right"><s:property value="get('contact')"/></td>
			<td><s:property value="get('subCount')"/></td>
			<td><s:if test="canDelete && get('subCount') == null">
			<s:form action="ReportAccountList" method="POST">
			<s:submit value="Remove" name="button"/>
			<s:hidden value="%{get('id')}" name="AccountID"/>
			</s:form>
			</s:if>
			</td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>