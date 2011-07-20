<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
<pics:permission perm="AllContractors">
	<div class="right"><a class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="javascript.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ContractorAssigned');" 
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a></div>
</pics:permission>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="assignContractorsForm" method="post" cssClass="forms">
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','a.creationDate DESC');">Registration</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','c.welcomeAuditor_id DESC,a.name');">Safety Professional</a></td>
				<td width="20"></td>
				<s:if test="showContact">
					<td>Primary Contact</td>
					<td>Phone</td>
					<td>Email</td>
					<td>Office Address</td>
					<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
					<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
					<td>Zip</td>
					<td>Web_URL</td>
				</s:if>
				<s:if test="showTrade">
					<td>Trade</td>
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
						id="%{'auditor_'.concat([0].get('id'))}" onchange="saveContractor('%{[0].get('id')}');" headerKey="" headerValue="- Safety Professional -" />
				</td>
				<td class="center" id="assignDate_<s:property value="[0].get('id')"/>">
				</td>
				<s:if test="showContact">
					<td><s:property value="get('contactname')"/></td>
					<td><s:property value="get('contactphone')"/></td>
					<td><s:property value="get('contactemail')"/></td>
					<td><s:property value="get('address')"/></td>
					<td><s:property value="get('city')"/></td>
					<td><s:property value="get('state')"/></td>
					<td><s:property value="get('zip')"/></td>
					<td><s:property value="get('web_URL')"/></td>
				</s:if>
				<s:if test="showTrade">
					<td><s:property value="get('main_trade')"/></td>
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
