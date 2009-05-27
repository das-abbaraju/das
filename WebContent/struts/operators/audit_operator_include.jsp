<%@ taglib prefix="s" uri="/struts-tags"%>
<div style="padding: 0px; margin: 0px; white-space: nowrap;">
<form id="ao<s:property value="htmlID" />"><s:hidden name="ao.id" value="%{id}" /> <s:hidden
	name="ao.auditType.id" value="%{auditType.id}" /> <s:hidden name="ao.operatorAccount.id" value="%{operatorAccount.id}" />

<s:checkbox name="ao.canSee" value="%{canSee}" title="View" onchange="$('button%{htmlID}').show()" /> | <s:checkbox
	name="ao.canEdit" value="canEdit" title="Edit" onchange="$('button%{htmlID}').show()" /> | <s:radio
	id="aorisk%{htmlID}" name="ao.minRiskLevel" list="riskLevelList" value="minRiskLevel"
	onchange="$('button%{htmlID}').show()" /> | <s:radio id="aoflag%{htmlID}" name="ao.requiredForFlag"
	list="FlagColorList" value="requiredForFlag" onchange="$('button%{htmlID}').show()" /> | <s:radio
	id="aostatus%{htmlID}" name="ao.requiredAuditStatus" list="AuditStatusList" value="requiredAuditStatus"
	onchange="$('button%{htmlID}').show()" /> <s:if test="auditType.classType.policy">
	<br>
	<s:textarea name="ao.help" value="%{help}" rows="3" cols="70" onkeyup="$('button%{htmlID}').show()"></s:textarea>
</s:if></form>
</div>
