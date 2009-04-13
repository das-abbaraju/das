<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../actionMessages.jsp" />
<h3>Approval Status for <s:property value="cao.operator.name"/></h3>
<s:form id="caoForm" action="CaoEditAjax">
	<s:hidden id="cao.id" name="cao.id"/>
	<input type="hidden" name="button" value="save"/>
	<s:radio name="cao.status" list="#{'Approved':'Approve', 'Rejected':'Reject', 'NotApplicable':'Not Applicable'}"/>
	<s:if test="cao.operator.parent != null">
		<s:checkbox name="cao.inherit"></s:checkbox> Default to status at <s:property value="cao.operator.parent.name"/>
	</s:if>
	<br />
	<s:textarea id="cao.notes" name="cao.notes" cols="60" rows="2"/>
	<div class="buttons">
		<button class="positive" onclick="editCao(<s:property value="cao.id"/>,'save')">Save</button>
		<a class="negative" href="#" onclick="javascript: $('caoSection').hide(); return false;">Close</a>
	</div>
</s:form>
