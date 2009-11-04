<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Assign Contractor</title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
	function saveContractor(conID) {
		var auditor = $('#auditor_' + conID).val();

		var data = {
				'ca.id': conID,
				'auditorId': auditor
		};
		
		$('#assignDate_'+conID).load('ContractorSaveAjax.action', data, function(text, status) {
			if (status='success')
				$('#audit_'+conID).effect('highlight', {color: '#FFFF11'}, 1000);
		}
		);
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
				<td align="center"><a href="javascript: changeOrderBy('form1','c.welcomeAuditor_id DESC,a.name');">Auditor</a></td>
				<td width="20"></td>
				<s:if test="showContact">
					<td>Primary Contact</td>
					<td>Phone</td>
					<td>Phone2</td>
					<td>Email</td>
					<td>Office Address</td>
					<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
					<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
					<td>Zip</td>
					<td>Second Contact</td>
					<td>Second Phone</td>
					<td>Second Email</td>
					<td>Web_URL</td>
				</s:if>
				<s:if test="showTrade">
					<td>Trade</td>
					<td>Industry</td>			
				</s:if>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr id="audit_<s:property value="[0].get('id')"/>">
				<td class="right"><s:property
					value="#stat.index + report.firstRowNumber" /></td>
				<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a></td>
				<td class="reportDate"><s:date name="[0].get('creationDate')"
					format="M/d/yy" /></td>
				<td>
					<s:select cssClass="blueMain" list="auditorList" listKey="id"
						listValue="name" value="%{[0].get('welcomeAuditor_id')}"
						id="%{'auditor_'.concat([0].get('id'))}" onchange="saveContractor('%{[0].get('id')}');" headerKey="" headerValue="- Auditor -" />
				</td>
				<td class="center" id="assignDate_<s:property value="[0].get('id')"/>">
				</td>
				<s:if test="showContact">
					<td><s:property value="get('contact')"/></td>
					<td><s:property value="get('phone')"/></td>
					<td><s:property value="get('phone2')"/></td>
					<td><s:property value="get('email')"/></td>
					<td><s:property value="get('address')"/></td>
					<td><s:property value="get('city')"/></td>
					<td><s:property value="get('state')"/></td>
					<td><s:property value="get('zip')"/></td>
					<td><s:property value="get('secondContact')"/></td>
					<td><s:property value="get('secondPhone')"/></td>
					<td><s:property value="get('secondEmail')"/></td>
					<td><s:property value="get('web_URL')"/></td>
				</s:if>
				<s:if test="showTrade">
					<td><s:property value="get('main_trade')"/></td>
					<td><s:property value="get('industry')"/></td>
				</s:if>
			</tr>
		</s:iterator>
	</table>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
