<%@ taglib prefix="s" uri="/struts-tags"%>

<s:include value="../actionMessages.jsp"/>

<s:iterator value="conAudit.operators">	
	<s:if test="status.awaiting">
		<div id="cao_<s:property value="operator.id"/>" style="padding:0px">
			<div id="alert">
				<button class="picsbutton positive" onclick="verifyReject('Verify',<s:property value="operator.id"/>)">Verify</button>
				<button class="picsbutton negative" onclick="verifyReject('Reject',<s:property value="operator.id"/>)">Reject</button>
				Verify or Reject the policy for <strong><s:property value="operator.name"/></strong> <br/>
				Notes: <s:textfield name="notes_%{operator.id}" value="%{notes}"/> 
			</div>
		</div>
	</s:if>
</s:iterator>