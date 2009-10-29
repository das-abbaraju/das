<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />
<s:form action="ManageWebcams" id="webcam_form">
	<s:hidden name="webcam.id" />
	<fieldset class="form"><legend><span><s:property
		value="%{webcam.id > 0 ? 'Edit' : 'Create'}" /> Webcam</span></legend>
	<ol>
		<li><label>ID:</label> <s:property value="%{webcam.id > 0 ? webcam.id : 'NEW'}" /></li>
		<li><label>Active:</label><s:checkbox name="webcam.active" /></li>
		<li><label>Comment:</label><s:textfield name="webcam.model" /></li>
		<li><label>Make/Model:</label><s:textfield name="webcam.make" /></li>
		<li><label>Replacement Cost:</label> $<s:textfield name="webcam.replacementCost" size="6" /></li>
	</ol>
	</fieldset>

	<s:if test="webcam.id > 0">
		<fieldset class="form"><legend><span>Shipping Info</span></legend>
		<ol>
			<s:if test="webcam.contractor == null">
				<li><label>Received By:</label> <s:property value="webcam.receivedBy.name" /></li>
				<li><label>Received On:</label> <s:date name="webcam.receivedDate" /></li>
			</s:if>
			<s:else>
				<li><label>Contractor:</label> <a
					href="ContractorView.action?id=<s:property
				value="webcam.contractor.id" />"><s:property
					value="webcam.contractor.name" /></a></li>
				<li><label>Sent By:</label> <s:property value="webcam.sendBy.name" /></li>
				<li><label>Sent On:</label> <s:date name="webcam.sentDate" /></li>
				<li><label>Carrier:</label> <s:property value="webcam.carrier" /></li>
				<li><label>Shipping Method:</label> <s:property value="webcam.shippingMethod" /></li>
				<li><label>Tracking Number:</label> <s:property value="webcam.trackingNumber" /></li>
			</s:else>
		</ol>
		</fieldset>
	</s:if>

	<pics:permission perm="ManageWebcam" type="Edit">
		<fieldset class="form submit">
			<input type="submit" class="picsbutton positive" name="button" value="Save" /> 
			<s:if test="webcam.id > 0">
				<s:if test="webcam.contractor == null">
					<pics:permission perm="ManageWebcam" type="Delete">
						<input type="submit" class="picsbutton negative" name="button" value="Delete" onclick="return confirm('Are you sure you want to delete this webcam? This cannot be undone.');"/>
					</pics:permission>
				</s:if>
				<s:else>
					<input type="submit" class="picsbutton" name="button" value="Receive" />
				</s:else>
				<input type="button" class="picsbutton" name="button" value="Add New" onclick="loadForm(0); return false;" />
			</s:if>
		</fieldset>
	</pics:permission>
</s:form>
