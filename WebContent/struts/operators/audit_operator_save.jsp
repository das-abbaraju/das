<%@ taglib prefix="s" uri="/struts-tags"%>
<s:radio name="riskLevel%{ao.htmlID}" list="riskLevelList"
			value="ao.minRiskLevel"
			onchange="save('%{ao.htmlID}', '%{ao.auditType.auditTypeID}', '%{ao.operatorAccount.id}', '%{ao.auditOperatorID}')" />
|
<s:radio name="requiredForFlag%{ao.htmlID}"
			list="FlagColorList" value="ao.requiredForFlag"
			onchange="save('%{ao.htmlID}', '%{ao.auditType.auditTypeID}', '%{ao.operatorAccount.id}', '%{ao.auditOperatorID}')" />
