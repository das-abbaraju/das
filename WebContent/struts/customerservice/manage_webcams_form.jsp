<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />
<s:form action="ManageWebcams" id="webcam_form">
	<s:hidden name="webcam.id" />
	<fieldset class="form bottom"><legend><span><s:property
		value="%{webcam.id > 0 ? 'Edit' : 'Create'}" /> Webcam</span></legend>
	<ol>
		<li><label>ID:</label> <s:property
			value="%{webcam.id > 0 ? webcam.id : 'NEW'}" /></li>
		<li><label>Make:</label><s:textfield name="webcam.make" /></li>
		<li><label>Model:</label><s:textfield name="webcam.model" /></li>
		<li><label>Active:</label><s:checkbox name="webcam.active" /></li>
		<li><label>Serial Number:</label><s:textfield
			name="webcam.serialNumber" size="30" /></li>
		<li><label>Replacement Cost:</label> $<s:textfield
			name="webcam.replacementCost" size="6" /></li>
	</ol>
	</fieldset>

	<pics:permission perm="ManageWebcam" type="Edit">
		<fieldset class="submit"><input type="submit"
			class="picsbutton positive" name="button" value="Save" /> <s:if
			test="webcam.id > 0">
			<pics:permission perm="ManageWebcam" type="Edit">
				<input type="submit" class="picsbutton negative" name="button"
					value="Delete" />
			</pics:permission>
			<input type="button" class="picsbutton" name="button" value="Add New"
				onclick="loadForm(0); return false;" />
		</s:if></fieldset>
	</pics:permission>
</s:form>
