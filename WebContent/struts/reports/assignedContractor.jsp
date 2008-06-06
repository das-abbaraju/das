<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Assign Contractor</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript"
	src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
<script type="text/javascript">
	function saveContractor(conID) {
		var pars = "ca.id=" + conID;
		
		var auditor = $F($('auditor_' + conID));
		pars = pars + "&auditorId=" + auditor;

		var divName = 'assignDate_'+conID;
		$(divName).innerHTML = '<img src="images/ajax_process.gif" />';
		var myAjax = new Ajax.Updater(divName,'ContractorSaveAjax.action',
					 {
					 	method: 'post', 
					 	parameters: pars,
					 	onSuccess: function(transport) {
					 	new Effect.Highlight('audit_'+conID, {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
					 	}
					 });
	}
</script>
</head>
<body>
<h1>Assign Contractor</h1>
<div id="search">
<div id="showSearch" onclick="showSearch()" <s:if test="filtered">style="display: none"</s:if> ><a href="#">Show Filter Options</a></div>
<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if> ><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<s:form id="form1" method="post" cssStyle="%{filtered ? '' : 'display: none'}">
	<table>
		<tr>
			<td style="vertical-align: middle;"><s:textfield
				name="accountName" cssClass="forms" size="8"
				onfocus="clearText(this)" /> 
			<s:select cssClass="blueMain" list="auditorList" listKey="id"
			listValue="name" value="conAuditorId" name="conAuditorId"
			/>
			<s:submit name="imageField" type="image"
				src="images/button_search.gif" onclick="runSearch( 'form1')" /></td>
		</tr>
		
	</table>
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
	<div class="alphapaging"><s:property
		value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
</s:form></div>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="assignContractorsForm" method="post" cssClass="forms">
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','createdDate DESC');">Account Date</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','auditorID DESC,name');">Auditor</a></td>
				<td width="20"></td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr id="audit_<s:property value="[0].get('id')"/>">
				<td class="right"><s:property
					value="#stat.index + report.firstRowNumber" /></td>
				<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a></td>
				<td class="reportDate"><s:date name="[0].get('accountDate')"
					format="M/d/yy" /></td>
				<td>
					<s:select cssClass="blueMain" list="auditorList" listKey="id"
						listValue="name" value="%{[0].get('welcomeAuditor_id')}"
						id="%{'auditor_'.concat([0].get('id'))}" onchange="saveContractor('%{[0].get('id')}');" />
				</td>
				<td class="center" id="assignDate_<s:property value="[0].get('id')"/>">
				</td>
			</tr>
		</s:iterator>
	</table>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
