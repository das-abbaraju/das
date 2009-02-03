<%@ taglib prefix="s" uri="/struts-tags"%>
<nobr>
<s:checkbox name="canSee%{htmlID}" value="canSee" title="View"
onclick="save('%{htmlID}', '%{auditType.id}', '%{operatorAccount.id}', '%{id}')" />
|
<s:checkbox name="canEdit%{htmlID}" value="canEdit" title="Edit"
onclick="save('%{htmlID}', '%{auditType.id}', '%{operatorAccount.id}', '%{id}')" />
|
<s:radio name="riskLevel%{htmlID}" list="riskLevelList" value="minRiskLevel" disabled="!canSee" 
onchange="save('%{htmlID}', '%{auditType.id}', '%{operatorAccount.id}', '%{id}')" />
|
<s:radio name="requiredForFlag%{htmlID}" list="FlagColorList" value="requiredForFlag" disabled="!canSee"
onchange="save('%{htmlID}', '%{auditType.id}', '%{operatorAccount.id}', '%{id}')" />
|
<s:radio name="requiredAuditStatus%{htmlID}" list="AuditStatusList" value="requiredAuditStatus" disabled="!canSee"
onchange="save('%{htmlID}', '%{auditType.id}', '%{operatorAccount.id}', '%{id}')" />
</nobr>
