<%@ taglib prefix="s" uri="/struts-tags"%>
<s:checkbox name="canSee%{ao.htmlID}" value="ao.canSee"
			onclick="save('%{ao.htmlID}', '%{ao.auditType.auditTypeID}', '%{ao.operatorAccount.id}', '%{ao.auditOperatorID}')" />
|
<s:checkbox name="canEdit%{ao.htmlID}" value="ao.canEdit"
					onclick="save('%{ao.htmlID}', '%{ao.auditType.auditTypeID}', '%{ao.operatorAccount.id}', '%{ao.auditOperatorID}')" />
|
<s:radio name="riskLevel%{ao.htmlID}" list="riskLevelList"
			value="ao.minRiskLevel" disabled="!ao.canSee"
			onchange="save('%{ao.htmlID}', '%{ao.auditType.auditTypeID}', '%{ao.operatorAccount.id}', '%{ao.auditOperatorID}')" />
|
<s:radio name="requiredForFlag%{ao.htmlID}"
			list="FlagColorList" value="ao.requiredForFlag" disabled="!ao.canSee"
			onchange="save('%{ao.htmlID}', '%{ao.auditType.auditTypeID}', '%{ao.operatorAccount.id}', '%{ao.auditOperatorID}')" />
