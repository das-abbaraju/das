<%@ taglib prefix="s" uri="/struts-tags"%>
<div style="padding: 0px; margin: 0px; white-space: nowrap;">
	<form id="ao<s:property value="htmlID" />">
		<s:if test="id > 0">
			<s:hidden name="ao.id" value="%{id}" />
		</s:if>
		<s:else>
			<s:hidden name="ao.auditType.id" value="%{auditType.id}" />
			<s:hidden name="ao.operatorAccount.id" value="%{operatorAccount.id}" />
		</s:else>
		<s:checkbox name="ao.canSee" value="%{canSee}" title="View" onclick="$('#button%{htmlID}').show()" /> | 
		<s:checkbox name="ao.canEdit" value="canEdit" title="Edit" onclick="$('#button%{htmlID}').show()" /> | 
		<s:radio id="aorisk%{htmlID}" name="ao.minRiskLevel" list="riskLevelList"
			value="minRiskLevel" onclick="$('#button%{htmlID}').show()" />
		<s:if test="operatorTag != null">
			<br />Restricted to: <s:property value="operatorTag.tag"/>
		</s:if>
		<s:if test="auditType.classType.policy"><br />
			<s:textarea name="ao.help" value="%{help}" rows="3" cols="70" 
				onkeyup="$('#button%{htmlID}').show()"></s:textarea>
		</s:if>
	</form>
<div style="margin-right: 10px"><s:include value="../actionMessages.jsp" /></div>
</div>