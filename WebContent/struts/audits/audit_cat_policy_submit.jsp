<%@ taglib prefix="s" uri="/struts-tags"%>

<s:include value="../actionMessages.jsp"/>

<s:iterator value="conAudit.currentOperators">
	<s:if test="status.pending">
		<div id="submit_<s:property value="operator.id"/>" style="padding:0px">
			<div class="alert">
				<button class="picsbutton positive" onclick="submitPolicy(<s:property value="operator.id"/>, false);return false;" >Submit</button>
				Click Submit after you've reviewed the <a href="ContractorForms.action?id=<s:property value="contractor.id"/>">Insurance Doc</a>
				for <strong><s:property value="operator.name"/></strong>
			</div>
		</div>
	</s:if>
</s:iterator>

<s:iterator value="conAudit.currentOperators">
	<s:if test="status.rejected">
		<div id="submit_<s:property value="operator.id"/>" style="padding:0px">
			<div class="alert">
				<button id="resubmit_<s:property value="operator.id"/>" disabled class="picsbutton positive" onclick="submitPolicy(<s:property value="operator.id"/>, true);return false;" >Resubmit</button>
				<input type="checkbox" onclick="changeButton('resubmit_<s:property value="operator.id"/>', this.checked)"/>
				I have reviewed the <strong><s:property value="audit.auditType.auditName"/></strong>
				requirements for <strong><s:property value="operator.name"/></strong> as per their <a href="ContractorForms.action?id=<s:property value="contractor.id"/>">Insurance Docs</a>
				<s:if test="notes != null && notes.trim().length() != 0">
					and I have addressed the previous problem of: <br/> &quot;<s:property value="notes"/>&quot;
				</s:if>
			</div>
		</div>
	</s:if>
</s:iterator>