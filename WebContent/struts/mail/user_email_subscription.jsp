<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="timePeriod.toString() != 'None'">
	<s:radio list="@com.picsauditing.mail.SubscriptionTimePeriod@getValuesWithDefault(subscription)" id="timePeriod_%{subscription}" theme="pics" value="%{timePeriod}" onchange="save('%{subscription}', %{id}, this)"/>
</s:if>
