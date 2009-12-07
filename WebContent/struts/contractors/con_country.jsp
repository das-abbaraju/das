<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="getStateList(account.country).size() > 0">
<label>
<s:if test="account.country == 'US'">
	State:
</s:if>
<s:elseif test="account.country == 'CA'">
	Province:
</s:elseif>
</label>
<s:select list="getStateList(account.country)" id="state_sel" name="contractor.state"/><span class="redMain" id="state_req">*</span>
</s:if>