<%@ taglib prefix="s" uri="/struts-tags"%>
<table border="0">
	<tr>
		<td style="text-align: right">Minimum Risk Level:</td>
		<td><s:radio name="riskLevel%{ao.htmlID}" list="riskLevelList"
			value="ao.minRiskLevel"
			onchange="save('%{ao.htmlID}', '%{ao.auditType.auditTypeID}', '%{ao.operatorAccount.id}', '%{ao.auditOperatorID}')" /></td>
	</tr>
	<tr>
		<td style="text-align: right">Flagged if Missing:</td>
		<td><s:radio name="requiredForFlag%{ao.htmlID}"
			list="FlagColorList" value="ao.requiredForFlag"
			onchange="save('%{ao.htmlID}', '%{ao.auditType.auditTypeID}', '%{ao.operatorAccount.id}', '%{ao.auditOperatorID}')" /></td>
	</tr>
</table>
