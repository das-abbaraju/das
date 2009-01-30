<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Assign Contractor</title>
<s:include value="reportHeader.jsp" />
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

<s:include value="filters.jsp" />

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="assignContractorsForm" method="post" cssClass="forms">
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','a.creationDate DESC');">Registration</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','c.accountDate DESC');">Paid</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','a.state');">State</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','c.welcomeAuditor_id DESC,a.name');">Auditor</a></td>
				<td width="20"></td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr id="audit_<s:property value="[0].get('id')"/>">
				<td class="right"><s:property
					value="#stat.index + report.firstRowNumber" /></td>
				<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a></td>
				<td class="reportDate"><s:date name="[0].get('creationDate')"
					format="M/d/yy" /></td>
				<td class="reportDate"><s:date name="[0].get('accountDate')"
					format="M/d/yy" /></td>	
				<td class="reportDate"><s:property value="[0].get('state')" /></td>
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
