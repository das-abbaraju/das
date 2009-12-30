<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="getStateList(countryString).size() > 0">
<s:set name="lbl" value="(countryString == 'CA') ? 'Province': 'State'" />
<label>
	<s:property value="#lbl"/>:
</label>
<s:select list="getStateList(countryString)" id="state_sel" name="%{prefix}state.isoCode" 
	headerKey="" headerValue="- %{#lbl} -" listKey="isoCode" listValue="name" value="stateString"/>
	<s:if test="stateString == null">
		<span class="redMain" id="state_req">*</span>
	</s:if>
</s:if>