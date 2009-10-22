<%@ taglib prefix="s" uri="/struts-tags"%>

<h4><s:property value="subscription.description"/></h4>
<div>
	<s:property value="subscription.longDescription"/>
</div>
<s:radio list="subscription.supportedTimePeriods" id="timePeriod_%{subscription}" theme="pics" value="%{timePeriod}" onclick="save('%{subscription}', %{id}, this)"/>
