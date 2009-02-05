<%@ taglib prefix="s" uri="/struts-tags"%>
<s:actionmessage/>
<form id="caoForm">
	<span style="margin: 20px;">
		<s:hidden id="cao.id" name="cao.id"/>
		<input type="hidden" name="button" value="save"/>
		<s:radio name="cao.status" list="#{'Approved':'Approve', 'Rejected':'Reject', 'NotApplicable':'Mark as N/A', 'Awaiting':'Awaiting'}"/>
	</span><br/> 
	<span style="margin: 20px;">
		<s:textarea id="cao.notes" name="cao.notes" cols="40" rows="5"/>
	</span><br/>
	<div class="buttons" style="margin: 20px;">
		<s:submit cssStyle="font-size: 16px; padding: 8px; margin: 5px; color: darkgreen; font-weight: bold;" value="Save" onclick="javascript: return saveCao();"/>
		<s:submit cssStyle="font-size: 16px; padding: 8px; margin: 5px; color: #d12f19;; font-weight: bold;" value="Close" onclick="javascript: $('caoSection').hide(); return false;"/>
	</div>
</form>