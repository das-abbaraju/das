<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<script type="text/javascript">
function rollback(id) {
	$("#row" + id).hide();
}

</script>

<table class="report">
	<thead>
		<tr>
			<th>Old CAO</th>
			<th>status</th>
			<th>Percent complete</th>
			<th>New CAO</th>
			<th>status</th>
			<th>Percent complete</th>
			<th>Rollback</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<s:form action="ContractorFlagChangesAjaxCaoDetails" method="POST">
				<s:hidden value="%{newCao.id}" name="id" />
				<s:hidden value="%{oldCao.id}" name="previousID" />
				<td><s:property value="oldContractorAuditOperator" /></td>
				<td><s:property value="oldContractorAuditOperatorStatus"/></td>
				<td><s:property value="oldContractorAuditOperatorPercentComplete"/></td>
				<td><s:property value="newContractorAuditOperator" /></td>
				<td><s:property value="newContractorAuditOperatorStatus"/></td>
				<td><s:property value="newContractorAuditOperatorPercentComplete"/></td>
				<td><s:submit method="rollback" value="Rollback" cssClass="save picsbutton negative" id="rollbackButton" /></td>
			</s:form>
		</tr>	
	</tbody>	
</table>
