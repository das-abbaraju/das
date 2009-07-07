<%@ taglib prefix="s" uri="/struts-tags"%>
<form id="eu<s:property value="id" />_<s:property value="#num.index" />">
	<s:hidden name="eu.subscription" value="%{subscription}" />
	<s:if test="id > 0">
		<s:hidden name="eu.id" value="%{id}" />
	</s:if><s:else>
		<s:hidden name="eu.user.id" value="%{permissions.userId}" />
	</s:else>				
	<s:radio list="subscriptionTimePeriods" theme="pics" name="eu.timePeriod" value="timePeriod"/>
</form>