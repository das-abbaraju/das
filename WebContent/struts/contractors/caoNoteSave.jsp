<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:hidden id="nR" value="%{noteRequired}" />
<div id="messageDialog" style="text-transform: uppercase;">
	<s:property value="saveMessage" />
</div>
<div id="formDialog">
	<s:textarea cssClass="clearOnce" rows="3" cols="30" id="addToNotes" value="%{noteMessage}" name="note" />
	<input type="hidden" id="clearOnceField" value="1" />
</div>
<div id="buttonsDialog">
	<div style="cursor: pointer;" class="button <s:property value="status.color" />" id="yesButton" ><s:property value="status.button" /></div>
</div>
<input class="picsbutton negative closeButton" type="button" value="Close" id="noButton" />
