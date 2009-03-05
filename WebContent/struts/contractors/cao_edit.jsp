<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../actionMessages.jsp" />
<form id="caoForm">
	<s:hidden id="cao.id" name="cao.id"/>
	<input type="hidden" name="button" value="save"/>
	<s:radio name="cao.status" list="#{'Approved':'Approve', 'Rejected':'Reject', 'NotApplicable':'Mark as N/A', 'Awaiting':'Awaiting'}"/>
	<br />
	<s:textarea id="cao.notes" name="cao.notes" cols="60" rows="2"/>
	<div class="buttons">
		<button type="button" class="positive" onclick="javascript: return saveCao();">Save</button>
		<a class="negative" href="#" onclick="javascript: $('caoSection').hide(); return false;">Close</a>
	</div>
</form>
