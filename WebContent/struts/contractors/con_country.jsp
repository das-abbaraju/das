<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="getStateList(country).size() > 0">
<s:set name="lbl" value="(country == 'CA') ? 'Province': 'State'" />
<label>
	<s:property value="#lbl"/>:
</label>
<s:select list="getStateList(country)" id="state_sel" name="contractor.state" headerKey="" headerValue="- %{#lbl} -" value="state"/><span class="redMain" id="state_req">*</span>
</s:if>