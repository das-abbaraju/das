<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../actionMessages.jsp"/>
<s:form action="ManageWebcams" id="webcam_form">
	<fieldset class="form">
		<legend><span>Webcam Edit</span></legend>
		<ol>
			<li><label>ID:</label><s:property value="webcam.id"/><s:hidden name="webcam.id"/></li>
			<li><label>Make:</label><s:textfield name="webcam.make"/></li>
			<li><label>Model:</label><s:textfield name="webcam.model"/></li>
			<li><label>Active:</label><s:checkbox name="webcam.active"/></li>
			<li><label>Serial Number:</label><s:textfield name="webcam.serialNumber"/></li>
			<li><label>Replacement Cost:</label><s:textfield name="webcam.replacementCost"/></li>
		</ol>
	</fieldset>
	<div>
		<input type="submit" class="picsbutton positive" name="button" value="Save"/>
		<s:if test="webcam.id > 0">
			<input type="submit" class="picsbutton negative" name="button" value="Delete"/>
			<input type="button" class="picsbutton" name="button" value="Add New" onclick="loadForm(0); return false;"/>
		</s:if>
	</div>
</s:form>