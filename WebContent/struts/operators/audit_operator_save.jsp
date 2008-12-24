<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="ao">
	<s:checkbox name="canSee%{htmlID}" value="canSee" title="View"
	onclick="save('%{htmlID}', '%{auditType.auditTypeID}', '%{operatorAccount.id}', '%{auditOperatorID}')" />
	|
	<s:checkbox name="canEdit%{htmlID}" value="canEdit" title="Edit"
	onclick="save('%{htmlID}', '%{auditType.auditTypeID}', '%{operatorAccount.id}', '%{auditOperatorID}')" />
	|
	<s:radio name="riskLevel%{htmlID}" list="riskLevelList" value="minRiskLevel" disabled="!canSee" 
	onchange="save('%{htmlID}', '%{auditType.auditTypeID}', '%{operatorAccount.id}', '%{auditOperatorID}')" />
	|
	<s:radio name="requiredForFlag%{htmlID}" list="FlagColorList" value="requiredForFlag" disabled="!canSee"
	onchange="save('%{htmlID}', '%{auditType.auditTypeID}', '%{operatorAccount.id}', '%{auditOperatorID}')" />
	|
	<s:radio name="requiredAuditStatus%{htmlID}" list="AuditStatusList" value="requiredAuditStatus" disabled="!canSee"
	onchange="save('%{htmlID}', '%{auditType.auditTypeID}', '%{operatorAccount.id}', '%{auditOperatorID}')" />
	<s:if test="auditType.classType.toString().equals('Policy')">
		|
			<s:radio name="additionalInsuredFlag%{htmlID}" list="FlagColorList" value="additionalInsuredFlag" disabled="!canSee"
			onchange="save('%{htmlID}', '%{auditType.auditTypeID}', '%{operatorAccount.id}', '%{auditOperatorID}')" />
		|
			<s:radio name="waiverSubFlag%{htmlID}" list="FlagColorList" value="waiverSubFlag" disabled="!canSee"
			onchange="save('%{htmlID}', '%{auditType.auditTypeID}', '%{operatorAccount.id}', '%{auditOperatorID}')" />
	</s:if>
</s:iterator>