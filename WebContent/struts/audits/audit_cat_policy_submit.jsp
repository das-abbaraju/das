<%@ taglib prefix="s" uri="/struts-tags"%>

<s:include value="../actionMessages.jsp"/>

<s:iterator value="conAudit.operators">
	<s:if test="status.pending">
		<div id="submit_<s:property value="operator.id"/>" style="padding:0px">
			<div id="alert">
				<button class="picsbutton positive" onclick="submitPolicy(<s:property value="operator.id"/>, false);return false;" >Submit</button>
				Click Submit when you're ready to finalize the 
				<strong><s:property value="audit.auditType.auditName"/></strong> for <strong><s:property value="operator.name"/></strong>
			</div>
		</div>
	</s:if>
	<s:elseif test="status.rejected">
		<div id="submit_<s:property value="operator.id"/>" style="padding:0px">
			<div id="alert">
				<button class="picsbutton positive" onclick="submitPolicy(<s:property value="operator.id"/>, true);return false;" >Resubmit</button>
				I have reviewed and updated information on the   
				<strong><s:property value="audit.auditType.auditName"/></strong> Policy for <strong><s:property value="operator.name"/></strong>
			</div>
		</div>
	</s:elseif>
</s:iterator>