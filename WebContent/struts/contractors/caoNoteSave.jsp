<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<div id="messageDialog" style="text-transform: uppercase;">
	<s:property value="saveMessage" />
</div>
<div id="formDialog">
	<br />
	<s:if test="noteRequired">
		<s:textarea cssClass="clearOnce" rows="3" cols="30" id="addToNotes" value="%{noteMessage}" name="note" />
	</s:if>
	<input type="hidden" id="clearOnceField" value="1" />
</div>
<div id="buttonsDialog">
	<input class="picsbutton positive" type="button" value="<s:property value="status.button" />" id="yesButton" />
	<input class="picsbutton negative" type="button" value="Cancel" id="noButton" />
</div>
