<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<script>
</script>
<div id="messageDialog">
	<s:property value="saveMessage" />
	<s:property value="stepID" />
</div>
<div id="formDialog">
	<br />
	<s:textarea cssClass="clearOnce" rows="3" cols="30" id="addToNotes" value="%{noteMessage}" />
	<input type="hidden" id="clearOnceField" value="1" />
</div>
<div id="buttonsDialog">
	<input class="picsbutton positive" type="button" value="<s:property value="status.button" />" id="yesButton" />
	<input class="picsbutton negative" type="button" value="Cancel" id="noButton" />
</div>
