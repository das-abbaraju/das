<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../actionMessages.jsp" />
<h3>Approval Status for <s:property value="cao.operator.name"/></h3>
<s:form id="caoForm" action="CaoEditAjax">
	<s:hidden id="cao.id" name="cao.id"/>
	<input type="hidden" name="button" value="save"/>
	<s:radio name="cao.status" list="#{'Approved':'Approve', 'Rejected':'Reject', 'NotApplicable':'Not Applicable'}"/>
	<!--<s:if test="cao.operator.parent != null">
		<s:checkbox name="cao.inherit"></s:checkbox> Default to status at <s:property value="cao.operator.parent.name"/>
	<br />
	Updated By: <s:property value="cao.updatedBy.name"/> from <s:property value="cao.updatedBy.account.name"/>
	</s:if>-->
	<br />
	<s:if test="!cao.visible">
		Pics Recommendation: <s:property value="cao.flag.smallIcon" escape="false"/> <s:property value="cao.flag" /> <br />
	</s:if>
	<s:textarea id="cao.notes" name="cao.notes" cols="60" rows="2"/>
	<div>
		<input type="button" class="picsbutton positive" onclick="editCao(<s:property value="cao.id"/>,'save')" value="Save"/>
		<input type="button" class="picsbutton negative" onclick="$('caoSection').hide();" value="Close"/>
	</div>
</s:form>
