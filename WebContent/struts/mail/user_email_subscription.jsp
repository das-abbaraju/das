<%@ taglib prefix="s" uri="/struts-tags"%>
<form id="eu<s:property value="subscription"/>">
	<s:hidden name="eu.subscription" value="%{subscription}" />
	<s:if test="id > 0">
		<s:hidden name="eu.id" value="%{id}" />
	</s:if><s:else>
		<s:hidden name="eu.user.id" value="%{permissions.userId}" />
	</s:else>				
	<s:radio list="subscription.supportedTimePeriods" id="%{subscription}_timePeriod" theme="pics" name="eu.timePeriod" value="timePeriod"/>
</form>